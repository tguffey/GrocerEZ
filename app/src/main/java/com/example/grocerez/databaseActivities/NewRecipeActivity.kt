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
import org.w3c.dom.ProcessingInstruction

class NewRecipeActivity : AppCompatActivity() {

    private lateinit var recipeName: EditText
    private lateinit var recipeInstruction: EditText
    private lateinit var ingredientName: EditText
    private lateinit var ingredientAmt: EditText
    private lateinit var ingredientCat: EditText
    private lateinit var ingredientUnit: EditText

    private lateinit var findItemBtn: Button
    private lateinit var addItem2RecipeBtn: Button
    private lateinit var addRecipeBtn: Button

    private lateinit var recipeTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_recipe)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        recipeName = findViewById(R.id.recipeNameEditText)
        recipeInstruction = findViewById(R.id.recipeInstructionEditText)

        ingredientName = findViewById(R.id.ingredientNameEditText)
        ingredientAmt = findViewById(R.id.ingredientAmountEditText)
        ingredientCat = findViewById(R.id.ingredientCategoryEditText)
        ingredientUnit = findViewById(R.id.ingredientUnitEditText)

        findItemBtn = findViewById(R.id.recipeFindItemButton)
        addItem2RecipeBtn = findViewById(R.id.recipeAddItemButton)
        addRecipeBtn = findViewById(R.id.addRecipeToDatabase)

        recipeTextView = findViewById(R.id.recipePageTextView)




    }
}