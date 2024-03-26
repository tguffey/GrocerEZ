package com.example.grocerez.databaseActivities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.grocerez.R
import com.example.grocerez.dao.CategoryDao
import com.example.grocerez.dao.ItemDao
import com.example.grocerez.dao.UnitDao
import com.example.grocerez.data.model.Item
import com.example.grocerez.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewItemActivity : AppCompatActivity() {
    private lateinit var categoryDao: CategoryDao
    private lateinit var unitDao: UnitDao
    private lateinit var itemDao: ItemDao

    private lateinit var buttonInsert: Button
    private lateinit var buttonDisplay: Button
    private lateinit var editTextItemName: EditText
    private lateinit var editTextUseRate: EditText

    private lateinit var textViewBox: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_item)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textViewBox = findViewById(R.id.feedbackTextBox)

        //define database instance and data acess objects
        val appDatabase = AppDatabase.getInstance(applicationContext)
        categoryDao = appDatabase.categoryDao()
        unitDao = appDatabase.unitDao()
        itemDao = appDatabase.itemDao()

        // get the spinner objects from xml file
        val spinnerCategories: Spinner = findViewById(R.id.categoryDropDown)
        val spinnerUnits: Spinner = findViewById(R.id.unitDropDown)


        // Populate unit and category dropdown menu
        CoroutineScope(Dispatchers.IO).launch{
            // get the category objects from category dao
            val categories = categoryDao.getAllCategories()

            // use the map function to map each object in the list of object, to a list of its name
            // then convert that list of its name to an array of strings
            val categoryNames = categories.map { it.name }.toTypedArray()

            // simple adapter object, adapt categoryNames to a spinner object using built in android element (simple soinner item)
            val categoryAdapter = ArrayAdapter(this@NewItemActivity, android.R.layout.simple_spinner_item, categoryNames)

            // use that adapter, put that into a built in drop down element
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategories.adapter = categoryAdapter

            // _________________________________________________ now units
            // get the category objects from category dao
            val units = unitDao.getAllUnits()

            // use the map function to map each object in the list of object, to a list of its name
            // then convert that list of its name to an array of strings
            val unitNames = units.map { it.name }.toTypedArray()

            // simple adapter object, adapt categoryNames to a spinner object using built in android element (simple soinner item)
            val unitAdapter = ArrayAdapter(this@NewItemActivity, android.R.layout.simple_spinner_item, unitNames)

            // use that adapter, put that into a built in drop down element
            unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerUnits.adapter = unitAdapter
        }

        buttonInsert = findViewById(R.id.add_new_item_button) // button to insert
        buttonDisplay = findViewById(R.id.display_Button) // button to display
        editTextItemName = findViewById(R.id.inputItemName) // textbox to input name
        editTextUseRate = findViewById(R.id.editTextUseRate) // textbox to input rate




        buttonInsert.setOnClickListener {
            val itemName = editTextItemName.text.toString()
            val useRate = editTextUseRate.text.toString().toFloatOrNull() ?: 0.0f // default to 0

            val selectedCategoryName = spinnerCategories.selectedItem.toString() // select from drop downs
            val selectedUnitName = spinnerUnits.selectedItem.toString()

            if (itemName.isNotBlank() && useRate >= 0) {
                val newItem = Item(name = itemName, useRate = useRate, category = selectedCategoryName, unitName = selectedUnitName)
//                CoroutineScope(Dispatchers.IO).launch {
//                    itemDao.insertItem(newItem)
//                }
//                editTextItemName.setText("")
//                editTextUseRate.setText("")
//                textViewBox.text = "sucessfull added item: $itemName \n category: $selectedCategoryName \n use rate: $useRate $selectedUnitName per week"

                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        itemDao.insertItem(newItem)
                    }
                    editTextItemName.setText("")
                    editTextUseRate.setText("")
                    textViewBox.text = "sucessfull added item: $itemName \n category: $selectedCategoryName \n use rate: $useRate $selectedUnitName per week"

                } catch (e: Exception){
                    runOnUiThread {
                        // TODO: this line was supposed to check for duplicates, but after all, we should still
                        // TODO: write a custom function for the unique names
                        textViewBox.error = "Error occurred while adding category"
                    }
                }


            } else if(itemName.isBlank()) {
                // Handle invalid input
                textViewBox.text = "please enter a name"
            } else if(useRate < 0){
                textViewBox.text = "please enter a positive number for use rate"
            } else {
                textViewBox.text = "error inserting item"
            }
        }



        buttonDisplay.setOnClickListener {

            //perform database function on the IO thread to be safe
            CoroutineScope(Dispatchers.IO).launch {
                val items = itemDao.getAllItems()

                // once have the items, switch back to main thread to update UI
                withContext(Dispatchers.Main) {
                    if (items.isEmpty()){
                        textViewBox.text = "no items added yet"
                    } else {
                        val itemListText = StringBuilder()
                        for (item in items) {
                            itemListText.append("ID: ${item.item_id},\n Name: ${item.name},\n Use Rate: ${item.useRate},\n Category: ${item.category},\n Unit: ${item.unitName}\n\n")
                        }
                        textViewBox.text = itemListText.toString()
                    }
                }


            }
        }
    }
}