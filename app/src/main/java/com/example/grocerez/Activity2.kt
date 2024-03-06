package com.example.grocerez

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Activity2 : AppCompatActivity() {
    private lateinit var itemDao: ItemDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize database with context
        val database = AppDatabase.getInstance(this)

        // Get the DAO using the database instance
        itemDao = database.itemDao()

        val backButton = findViewById<Button>(R.id.backBtn)
        backButton.setOnClickListener {
            finish()
        }

        var itemname: EditText? = null
        var category: EditText? = null
//        var amount: EditText? = null

        itemname = findViewById(R.id.itemnameText);
        category = findViewById(R.id.itemcategoryText);
        val inputitemBtn = findViewById<Button>(R.id.inputItemBtn)
        inputitemBtn.setOnClickListener {

        }

        val displayText = findViewById<TextView>(R.id.displayText)
    }
}