package com.example.grocerez.databaseActivities

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        val addNewPantryItemButton = findViewById<Button>(R.id.addNewPantryItem_button)
        addNewPantryItemButton.setOnClickListener {
            val intent = Intent(this, NewPantryItemActivity::class.java)
            startActivity(intent)
            finish()
        }

        val addNewShoppingListItemButton = findViewById<Button>(R.id.addNewShoppingListItem_button)
        addNewShoppingListItemButton.setOnClickListener {
            val intent = Intent(this, NewShoppingListItemActivity::class.java)
            startActivity(intent)
            finish()
        }

        val addNewRecipeButton = findViewById<Button>(R.id.addNewRecipe_button)
        addNewRecipeButton.setOnClickListener {
//            testRecipe()
            val intent = Intent(this, NewRecipeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val seeAllRecipesButton = findViewById<Button>(R.id.seeRecipesButton)
        seeAllRecipesButton.setOnClickListener {
            val intent = Intent(this, ViewRecipesActivity::class.java)
            startActivity(intent)
            finish()
        }

        val myPlateButton = findViewById<Button>(R.id.myPlateButton)
        myPlateButton.setOnClickListener {
            val intent = Intent(this, MyPlateActivity::class.java)
            startActivity(intent)
            finish()
        }
    }




}