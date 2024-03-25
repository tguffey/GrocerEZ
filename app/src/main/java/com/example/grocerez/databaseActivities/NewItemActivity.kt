package com.example.grocerez.databaseActivities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.grocerez.R
import com.example.grocerez.dao.CategoryDao
import com.example.grocerez.dao.UnitDao
import com.example.grocerez.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewItemActivity : AppCompatActivity() {
    private lateinit var categoryDao: CategoryDao
    private lateinit var unitDao: UnitDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_item)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val appDatabase = AppDatabase.getInstance(applicationContext)
        categoryDao = appDatabase.categoryDao()
        unitDao = appDatabase.unitDao()

        // Assuming you have a Spinner in your layout XML with id "spinnerCategories" and "spinnerUnits"
        val spinnerCategories: Spinner = findViewById(R.id.categoryDropDown)
        val spinnerUnits: Spinner = findViewById(R.id.unitDropDown)

        // Populate category dropdown menu
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
        }


    }
}