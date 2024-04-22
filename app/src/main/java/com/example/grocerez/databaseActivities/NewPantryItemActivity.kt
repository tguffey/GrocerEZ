package com.example.grocerez.databaseActivities

import android.media.metrics.EditingSession
import android.app.DatePickerDialog
import java.text.SimpleDateFormat
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
import com.example.grocerez.dao.ItemDao
import com.example.grocerez.dao.PantryItemDao
import com.example.grocerez.dao.UnitDao
import com.example.grocerez.database.AppDatabase
import java.util.Calendar
import java.util.Locale


class NewPantryItemActivity : AppCompatActivity() {
    private lateinit var editTextName: EditText
    private lateinit var editTextInputDate: EditText
    private lateinit var editTextShelfLife: EditText
    private lateinit var editTextAmountLeft: EditText
    private lateinit var editTextUnit: EditText
    private lateinit var editTextCategory: EditText

    private lateinit var buttonFindItem: Button
    private lateinit var buttonAdd2Pantry: Button
    private lateinit var buttonSeeAllPantry: Button

    private lateinit var textViewFeedbackPantry: TextView

    private lateinit var categoryDao: CategoryDao
    private lateinit var unitDao: UnitDao
    private lateinit var itemDao: ItemDao
    private lateinit var pantryItemDao: PantryItemDao

    private lateinit var calendar: Calendar
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

        //

        // binding textview
        textViewFeedbackPantry = findViewById(R.id.textviewPantryFeedback)

        // set onclicklisteners
        buttonFindItem.setOnClickListener{

        }
        buttonAdd2Pantry.setOnClickListener{

        }
        buttonSeeAllPantry.setOnClickListener{

        }

        editTextInputDate.setOnClickListener {
            showDatePicker()
        }
    }

    fun areAllRequiredFieldsNotNull(): Boolean {
        val isNameNotEmpty = editTextName.text.isNotEmpty()
        val isCategoryNotEmpty = editTextCategory.text.isNotEmpty()
        val isUnitNotEmpty = editTextUnit.text.isNotEmpty()

        val isDateNotEmpty = editTextInputDate.text.isNotEmpty()
        val isShelfLifeEmpty = editTextShelfLife.text.isNotEmpty()
        val isAmountNotEmpty = editTextAmountLeft.text.isNotEmpty()


        // Check if all required fields (excluding notes) are not empty
        return (isNameNotEmpty
                && isCategoryNotEmpty
                && isUnitNotEmpty
                && isDateNotEmpty
                && isShelfLifeEmpty
                && isAmountNotEmpty)
    }
    // this will let user choose from calendar
    private fun showDatePicker() {
        val calendar = Calendar.getInstance() // get instance of current date and time

        // extract the 3 from calendar instance
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // Set up DatePickerDialog to show current date as default
        val datePickerDialog = DatePickerDialog(
            this, // context
            {  _, selectedYear, selectedMonth, selectedDay ->
                // Update the EditText with the selected date
                // month is default 0 so +1 to format
                // represents day
                // the year mod 100 to have 2 final digit only
                val selectedDate = "${selectedMonth + 1}/$selectedDay/${selectedYear % 100}"
                editTextInputDate.setText(selectedDate) //update the edittext with the slected date
            },
            year,
            month,
            dayOfMonth
        )

        // Show DatePickerDialog to user
        datePickerDialog.show()
    }
}