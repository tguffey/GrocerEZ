package com.example.grocerez.databaseActivities

import android.media.metrics.EditingSession
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.grocerez.R
import com.example.grocerez.dao.CategoryDao
import com.example.grocerez.dao.ItemDao
import com.example.grocerez.dao.PantryItemDao
import com.example.grocerez.dao.ShoppingListItemDao
import com.example.grocerez.dao.UnitDao
import com.example.grocerez.database.AppDatabase

class NewPantryItemActivity : AppCompatActivity() {
    private lateinit var editTextName: EditText
    private lateinit var editTextInputDate: EditText
    private lateinit var editTextShelfLife: EditText
    private lateinit var editTextAmountLeft: EditText
    private lateinit var editTextUnit: EditText
    private lateinit var editTextCategory: EditText

    private lateinit var buttonFindItem: EditText
    private lateinit var buttonAdd2Pantry: EditText
    private lateinit var buttonSeeAllPantry: EditText

    private lateinit var textViewFeedbackPantry: EditText

    private lateinit var categoryDao: CategoryDao
    private lateinit var unitDao: UnitDao
    private lateinit var itemDao: ItemDao
    private lateinit var pantryItemDao: PantryItemDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_pantry_item)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // database initialization
        val appDatabase = AppDatabase.getInstance(applicationContext)
        categoryDao = appDatabase.categoryDao()
        unitDao = appDatabase.unitDao()
        itemDao = appDatabase.itemDao()
        pantryItemDao = appDatabase.pantryItemDao()

        // binding ui elements: edit texts
        editTextName = findViewById(R.id.editTextPantryItemName) //input item name
        editTextInputDate = findViewById(R.id.editTextPantryDate) // input date item was added
        editTextShelfLife = findViewById(R.id.editTextPantryRemainingShelfLife) // input shelf life in int
        editTextAmountLeft = findViewById(R.id.editTextPantryAmount) // input amount left of pantry item
        editTextUnit = findViewById(R.id.editTextPantryUnit) // input unit (same as how we have to input for shopping list item)
        editTextCategory= findViewById(R.id.editTextPantryCategory) // input category (same as how we have to input for shopping list item)

        // bidning buttons
        buttonFindItem = findViewById(R.id.buttonPantryFindItem) // find the iten based on name and retrieve category and unit
        buttonAdd2Pantry = findViewById(R.id.buttonPantryAddItem)
        buttonSeeAllPantry = findViewById(R.id.buttonPantryShowAll)

        // binding textview
        textViewFeedbackPantry = findViewById(R.id.textviewPantryFeedback)

        // set onclicklisteners
        buttonFindItem =
        buttonAdd2Pantry
        buttonSeeAllPantry
    }
}