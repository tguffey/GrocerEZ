package com.example.grocerez.databaseActivities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.grocerez.R
import com.example.grocerez.dao.UnitDao
import com.example.grocerez.data.model.Unit
import com.example.grocerez.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

class NewUnitActivity : AppCompatActivity() {

    private lateinit var unitDao: UnitDao
    private lateinit var textViewUnitList: TextView
    private lateinit var editTextUnitName: EditText
    private lateinit var buttonInsert: Button
    private lateinit var buttonDisplay: Button
    private lateinit var buttonFind: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_unit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editTextUnitName = findViewById(R.id.inputNewUnit_text)
        buttonInsert = findViewById(R.id.addNewUnit_btn)
        buttonDisplay = findViewById(R.id.displayAllUnits_btn)
        textViewUnitList = findViewById(R.id.feedbackTextView)

        buttonFind = findViewById(R.id.searchByName_button)


        val appDatabase = AppDatabase.getInstance(applicationContext)
        unitDao = appDatabase.unitDao()

        buttonInsert.setOnClickListener {
            val unitName = editTextUnitName.text.toString()
            if (unitName.isNotBlank()) {
                insertUnit(unitName)
                editTextUnitName.setText("")
            } else{
                textViewUnitList.text = "Please enter non null string"
            }

        }


        buttonDisplay.setOnClickListener {
            displayUnits()
        }

        buttonFind.setOnClickListener {
            searchUnitByName(editTextUnitName.text.toString())
        }

    }

    private fun insertUnit(unitName: String) {
        val newunit = Unit(unitName)

        CoroutineScope(Dispatchers.IO).launch {
            unitDao.insertUnit(newunit)
            textViewUnitList.text = ("new unit is inserted: $unitName\n")

            //TODO: handle error and duplicates
        }
    }

    private fun displayUnits(){
//        val units = unitDao.getAllUnits()
//        val unitList = units.joinToString(separator = "\n") { it.name }
//        textViewUnitList.text = unitList

        CoroutineScope(Dispatchers.IO).launch {
            val units = unitDao.getAllUnits()
            val unitList = units.joinToString(separator = "\n") { it.name }
            textViewUnitList.text = unitList
        }
    }

    // DONE: add error catching.
    private fun searchUnitByName(unitName: String) {
        CoroutineScope(Dispatchers.IO).launch {

            try{
                val unit = unitDao.findUnitByName(unitName)
                // Update UI with the unit object
                if (unit != null) {
                    textViewUnitList.text = "Unit found: ${unit.name}"
                } else {
                    textViewUnitList.text = "Unit not found"
                }
            } catch (e: Exception){
                textViewUnitList.text = "Error: ${e.message}"
            }


        }

    }



}