package com.example.grocerez.databaseActivities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.grocerez.R

class DatabaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_database)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val addNewUnitBtn = findViewById<Button>(R.id.addNewUnit)
        addNewUnitBtn.setOnClickListener {
            val intent = Intent(this, NewUnitActivity::class.java)
            startActivity(intent)
            finish()
        }


        val addNewCategoryButton = findViewById<Button>(R.id.addNewCategory)
        addNewCategoryButton.setOnClickListener {
            val intent = Intent(this, NewCategoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        val addNewItemButton  = findViewById<Button>(R.id.addNewItem)
        addNewItemButton.setOnClickListener {
            val intent = Intent(this, NewItemActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}