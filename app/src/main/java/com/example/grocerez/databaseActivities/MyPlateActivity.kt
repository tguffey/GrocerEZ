package com.example.grocerez.databaseActivities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.grocerez.R
import com.example.grocerez.dao.CategoryDao
import com.example.grocerez.dao.MyPlateDao
import com.example.grocerez.dao.UnitDao
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.MyPlateItem
import com.example.grocerez.data.model.Unit
import com.example.grocerez.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPlateActivity : AppCompatActivity() {
    private lateinit var editTextCategory: EditText
    private lateinit var editTextAmount: EditText
    private lateinit var editTextUnit: EditText

    private lateinit var buttonAddMyPlateItem: Button
    private lateinit var buttonSeeAllMyPlate: Button

    private lateinit var textViewFeedback: TextView

    private lateinit var myPlateDao: MyPlateDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var unitDao: UnitDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_plate)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // database initialization
        val appDatabase = AppDatabase.getInstance(applicationContext)
        myPlateDao = appDatabase.myPlateDao()
        categoryDao = appDatabase.categoryDao()
        unitDao = appDatabase.unitDao()

        // binding of ui elements
        editTextCategory = findViewById(R.id.editTextMyplateCategory)
        editTextAmount = findViewById(R.id.editTextMyPlateAmount)
        editTextUnit = findViewById(R.id.editTextMyPlateUnit)

        //feedback textbox
        textViewFeedback = findViewById(R.id.textViewMyPlateFeedback)

        // buttons
        buttonAddMyPlateItem = findViewById(R.id.buttonMyPlateAddEntry)
        buttonSeeAllMyPlate = findViewById(R.id.buttonMyPlateSeeAll)

        // button onlicks
        buttonAddMyPlateItem.setOnClickListener {
            val category = editTextCategory.text.toString().trim()
            val amountText  = editTextAmount.text.toString().trim()
            val unit = editTextUnit.text.toString().trim()

            // Check if any field is empty
            val isAnyFieldEmpty = category.isEmpty()
                    || amountText.isEmpty()
                    || unit.isEmpty()

            // Check if amount is a non-negative float
            val amount: Float? = amountText.toFloatOrNull()
            val isAmountInvalid = amount == null || amount < 0

            // Check if any field is empty or amount is invalid
            if (isAnyFieldEmpty || isAmountInvalid) {
                textViewFeedback.text = "Please fill in all fields and a non-negative amount."
                return@setOnClickListener // exit function early
            }

            Toast.makeText(this, "Now adding to database", Toast.LENGTH_SHORT).show()
            addToMyPlate(category, amount!!, unit)
        }


        buttonSeeAllMyPlate.setOnClickListener {
            displayAllMyplate()
        }

    }


    private fun addToMyPlate(categoryName: String, amount: Float, unitName: String){
        // Perform database operation to add the MyPlate item
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Check if the category exists in the category table
                var category = categoryDao.findCategoryByName(categoryName = categoryName)

                // If the category doesn't exist, insert it into the category table
                if (category == null) {
                    category = Category(categoryName)
                    categoryDao.insertCategory(category)
                }

                // Check if the unit exists in the unit table
                var unit = unitDao.findUnitByName(unitName)

                // If the unit doesn't exist, insert it into the unit table
                if (unit == null) {
                    unit = Unit(unitName)
                    unitDao.insertUnit(unit)
                }

                // Now that we have ensured that the category and unit exist, add the MyPlateItem
                val myPlateItem = MyPlateItem(
                    categoryName = categoryName,
                    amount = amount,
                    unit = unitName
                )
                myPlateDao.insertMyPlateItem(myPlateItem)

                withContext(Dispatchers.Main) {
                    textViewFeedback.text = "Item added to My Plate successfully."
                }
            }
            catch (e:Exception){
                withContext(Dispatchers.Main) {
                    textViewFeedback.text = "Error adding item to My Plate: ${e.message}"
                }

            }
        }








    }

    private fun displayAllMyplate(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val myPlateItems = myPlateDao.getAllMyPlateItems()
                if (myPlateItems.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        textViewFeedback.text = "No items added yet."
                    }
                }
                else {
                    val formattedString = buildString {
                        myPlateItems.forEach { item ->
                            append("ID: ${item.id}\n")
                            append("Category: ${item.categoryName}\n")
                            append("Amount: ${item.amount} ${item.unit}\n\n")
                        }
                    }
                    withContext(Dispatchers.Main) {
                        textViewFeedback.text = formattedString
                    }
                }


            }
            catch (e:Exception){
                withContext(Dispatchers.Main) {
                    textViewFeedback.text = "Error: ${e.message}"
                }
            }
        }
    }
}