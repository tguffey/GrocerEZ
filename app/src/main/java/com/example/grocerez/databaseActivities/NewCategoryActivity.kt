package com.example.grocerez.databaseActivities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.grocerez.R
import com.example.grocerez.dao.CategoryDao
import com.example.grocerez.dao.UnitDao
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Unit
import com.example.grocerez.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewCategoryActivity : AppCompatActivity() {
    private lateinit var categoryDao: CategoryDao
    private lateinit var textViewBox: TextView
    private lateinit var editTextCategoryName: EditText
    private lateinit var buttonInsert: Button
    private lateinit var buttonDisplay: Button
    private lateinit var buttonSearch: Button
    private lateinit var backButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_category)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        editTextCategoryName = findViewById(R.id.newCategoryInputText)
        buttonInsert = findViewById(R.id.addCategory_btn)
        buttonDisplay = findViewById(R.id.displayCategory_btn)
        textViewBox = findViewById(R.id.textBox)

        buttonSearch = findViewById(R.id.searchByCategory_button)

        val appDatabase = AppDatabase.getInstance(applicationContext)
        categoryDao = appDatabase.categoryDao()

        buttonInsert.setOnClickListener {
            val categoryName = editTextCategoryName.text.toString()
            if (categoryName.isNotBlank()) {
                insertCategory(categoryName)
                editTextCategoryName.setText("")
            } else{
                textViewBox.text = "please enter non null string"
            }
        }

        buttonDisplay.setOnClickListener {
            displayCategories()
        }

        buttonSearch.setOnClickListener {
            searchCategoryByName(editTextCategoryName.text.toString())
        }

        backButton = findViewById(R.id.back_category_button)
        backButton.setOnClickListener {
//            val intent = Intent(this, DatabaseActivity::class.java)
//            startActivity(intent)
            finish()
        }
    }

    // has error handling
    private fun insertCategory(categoryName: String){

        try {
            val newCategory = Category(categoryName)
            CoroutineScope(Dispatchers.IO).launch {
                categoryDao.insertCategory(newCategory)
                textViewBox.text = ("new unit is inserted: $categoryName\n")

                //TODO: handle error and duplicates
            }
        } catch (e: Exception){
            textViewBox.text = "Error: ${e.message}"
        }

    }

    private fun displayCategories(){
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val categories = categoryDao.getAllCategories()
                val categoryList = categories.joinToString(separator = "\n") { it.name }
                textViewBox.text = categoryList
            }
        } catch (e: Exception){
            textViewBox.text = "Error: ${e.message}"
        }

    }

    // search a function by its category and display the result,
    // has error handling
    private fun searchCategoryByName(categoryName: String) {
        CoroutineScope(Dispatchers.IO).launch {

            try{
                val unit = categoryDao.findCategoryByName(categoryName)
                // Update UI with the unit object
                if (unit != null) {
                    textViewBox.text = "Category found: ${unit.name}"
                } else {
                    textViewBox.text = "Category not found"
                }
            } catch (e: Exception){
                textViewBox.text = "Error: ${e.message}"
            }


        }

    }
}