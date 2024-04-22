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
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.PantryItem
import com.example.grocerez.data.model.ShoppingListItem
import com.example.grocerez.data.model.Unit
import com.example.grocerez.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


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
        // DONE
        buttonFindItem.setOnClickListener{
            searchItemAndPopulateFields()
        }

        // DONE
        buttonAdd2Pantry.setOnClickListener{
            if (areAllRequiredFieldsNotNull() == false){
                val check = areAllRequiredFieldsNotNull()
                textViewFeedbackPantry.text = "$check , please fill out all the required fields (all fields are required)"
            } else {
                val itemName = editTextName.text.toString()
                val inputDate = editTextInputDate.text.toString()
                val shelfLife = editTextShelfLife.text.toString().toInt()
                val amountLeft = editTextAmountLeft.text.toString().toFloat()
                val unit = editTextUnit.text.toString()
                val category = editTextCategory.text.toString()
                val check = areAllRequiredFieldsNotNull()
                try{
                    addPantryItem(itemName = itemName, category = category, unit = unit, amount = amountLeft, inputDate = inputDate, shelfLifeFromInputDate = shelfLife)
//                    textViewFeedbackPantry.text = "in the else, $check"
                }catch (e: Exception){
                    textViewFeedbackPantry.text = "something went wrong when adding, $e"
                }
            }

        }

        // TODO
        buttonSeeAllPantry.setOnClickListener{
//            val selectedDateStr = editTextInputDate.text.toString()
//            val daysSince = calculateDaysSinceInputDate(selectedDateStr)
//            textViewFeedbackPantry.text = "Selected Date: $selectedDateStr, \nDays Since: $daysSince"
            seeAllPantryItems()
        }

        // DONE, this is to show the calender for date input
        editTextInputDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun areAllRequiredFieldsNotNull(): Boolean {
        val isNameNotEmpty = editTextName.text.isNotEmpty()
        val isCategoryNotEmpty = editTextCategory.text.isNotEmpty()
        val isUnitNotEmpty = editTextUnit.text.isNotEmpty()

        val isDateNotEmpty = editTextInputDate.text.isNotEmpty()
        val isShelfLifeNotEmpty = editTextShelfLife.text.isNotEmpty()
        val isAmountNotEmpty = editTextAmountLeft.text.isNotEmpty()


        // Check if all required fields (excluding notes) are not empty
        return isNameNotEmpty
                && isCategoryNotEmpty
                && isUnitNotEmpty
                && isDateNotEmpty
                && isShelfLifeNotEmpty
                && isAmountNotEmpty
    }
    // this will let user choose from calendar for the input date field
    private fun showDatePicker() {
        val calendar = Calendar.getInstance() // get instance of current date and time

        // extract the 3 from calendar instance
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        editTextInputDate.text.toString().let {
            if (it.isNotEmpty()) {
                // make a list of the date month year sperated by /
                //mm/dd/yy
                val parts =  it.split("/")
                if (parts.size == 3) {
                    month = parts[0].toInt() - 1 // months are 0-indexed in Calendar
                    dayOfMonth = parts[1].toInt()
                    year = parts[2].toInt() + 2000 // Add 2000 to get full year
                }
            }
        }

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

    // DONE
    // Main function to add the stuff to the stuff
    private fun addPantryItem(itemName: String, category: String, unit: String, inputDate: String, shelfLifeFromInputDate: Int, amount: Float){

        var displayString = ""
        CoroutineScope(Dispatchers.IO).launch {

            try {
                val existingItem = itemDao.findItemByName(itemName)

                // see if item exists in the database already or not.
                // if isnull, check for category and unit, add those if not exist. then add item
                if (existingItem == null) {
                    withContext(Dispatchers.Main){
                        textViewFeedbackPantry.text = "no existing item exist with that name in the items table."
                    }
                    // Item doesn't exist, create a new one and insert it into the Item table
                    // this block added the items
                    try {
                        val existingCategory = categoryDao.findCategoryByName(category)
                        val existingUnit = unitDao.findUnitByName(unit)

                        if (existingCategory == null || existingUnit == null){

                            displayString += "category or unit does not exist yet. now adding \n"
                            val newCategory = Category(category)
                            val newUnit = Unit(unit)

                            // can only do this because we have onconflictstrategy.replace
                            categoryDao.insertCategory(newCategory)
                            unitDao.insertUnit(newUnit)

                            // handle insert item after making sure category and unit is added
                            val newItem = Item(name = itemName, category = category, unitName = unit, useRate = 0.0f)
                            itemDao.insertItem(newItem)
                        } else {
                            val newItem = Item(name = itemName, category = category, unitName = unit, useRate = 0.0f)
                            itemDao.insertItem(newItem)
                        }
                        // we can do this because the respective DAO objects does replace on conflict
                        withContext(Dispatchers.Main){
                            textViewFeedbackPantry.text = (displayString)
                        }

                    }catch (e: Exception){
                        withContext(Dispatchers.Main){
                            textViewFeedbackPantry.text = "adding item to item table Error: ${e.message}"
                        }
                    }

                    // now will handle adding pantry item_______________________________
                    val displayItem = itemDao.findItemByName(itemName)
//                    var displayString2 = ""

                    // ideally the if line below trigger
                    if (displayItem != null){
                        displayString += "\nitem is found in item table: \n" +
                                "id: ${displayItem.item_id} \n" +
                                "name: ${displayItem.name} \n" +
                                "category: ${displayItem.category} \n" +
                                "userate: ${displayItem.useRate} ${displayItem.unitName} per week \n"
                    } else {
                        displayString += "\nerror inserting item"
                    }

                    var pantryItemName = ""
                    if (displayItem != null){
                        pantryItemName = displayItem.name
                    }
                    //now insert shoppingListItem
                    val newPantryItem = PantryItem(itemName = pantryItemName, amountFromInputDate = amount, inputDate = inputDate, shelfLifeFromInputDate = shelfLifeFromInputDate)
                    pantryItemDao.insertPantryItemDao(newPantryItem)

                    displayString = displayString +
                            "\nsucessfully insert new item into myPantry: \n" +
                            "name: ${itemName} \n" +
                            "inputDate: ${inputDate} \n" +
                            "shelflife from input date: ${shelfLifeFromInputDate} \n" +
                            "amount left: ${amount} ${unit}"

                    withContext(Dispatchers.Main){
                        textViewFeedbackPantry.append(displayString)
                    }

                } else if (existingItem != null){
                    //now insert shoppingListItem
                    val displayItem = itemDao.findItemByName(itemName)
//                    var displayString2 = ""
                    var pantryItemName = ""
                    if (displayItem != null){
                        pantryItemName = displayItem.name
                    }
                    //now insert shoppingListItem
                    val newPantryItem = PantryItem(itemName = pantryItemName, amountFromInputDate = amount, inputDate = inputDate, shelfLifeFromInputDate = shelfLifeFromInputDate)
                    pantryItemDao.insertPantryItemDao(newPantryItem)

                    displayString = displayString +
                            "\nsucessfully insert new item into My Pantry: \n" +
                            "name: ${itemName} \n" +
                            "inputDate: ${inputDate} \n" +
                            "shelflife from input date: ${shelfLifeFromInputDate} \n" +
                            "amount left: ${amount} ${unit}"

                    withContext(Dispatchers.Main){
                        textViewFeedbackPantry.append(displayString)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        textViewFeedbackPantry.text = "looks lke there were problems inserting"
                    }
                }

            } catch (e: Exception){
                withContext(Dispatchers.Main) {
                    textViewFeedbackPantry.append( "PantryItemError: ${e.message}")
                }

            }
        }
    }

    private fun seeAllPantryItems(){
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val allShoppingListItems = pantryItemDao.getAllPantryItem()
                withContext(Dispatchers.Main) {
                    if (allShoppingListItems.isEmpty()){
                        textViewFeedbackPantry.text = "no items added to shopping list yet"
                    } else {
                        val itemListText = StringBuilder()
                        for (pantryItem in allShoppingListItems) {
                            // retrieving data to display
                            val item = itemDao.findItemByName(pantryItem.itemName)

                            var unitForThis = "unit"
                            if (item != null){
                                unitForThis = item.unitName.toString()
                            }

                            var daysSinceInput = calculateDaysSinceInputDate(pantryItem.inputDate)
                            var remainingShelfLife = pantryItem.shelfLifeFromInputDate - daysSinceInput


                            //string to display
                            var displayString = "ID: ${pantryItem.pantryItemId},\n" +
                                    "Name: ${pantryItem.itemName},\n"+
                                    "date input: ${pantryItem.inputDate},\n"+
                                    "amount from input date: ${pantryItem.amountFromInputDate} $unitForThis \n"+
                                    "shelf life from input date: ${pantryItem.shelfLifeFromInputDate} \n" +
                                    "it has been ${daysSinceInput} days since input date\n" +
                                    "remaining shelf life: about ${remainingShelfLife} days. \n\n"


                            itemListText.append(displayString)
                        }
                        textViewFeedbackPantry.text = "my Pantry:\n" + itemListText.toString()
                        editTextName.text.clear()
                        editTextInputDate.text.clear()
                        editTextShelfLife.text.clear()
                        editTextAmountLeft.text.clear()
                        editTextUnit.text.clear()
                        editTextCategory.text.clear()
                    }
                }
            }catch (e: Exception){
                textViewFeedbackPantry.text = "error when display: $e"
            }
        }

    }
    private fun searchItemAndPopulateFields() {
        val itemName = editTextName.text.toString().trim()
        // if variable is empty, stop here
        if (itemName.isEmpty()) {
            textViewFeedbackPantry.text = "Please enter a name"
            return
        }
        // Perform a database query to search for the item by name
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val item = itemDao.findItemByName(itemName)

                // Update UI if object found and catch db error
                withContext(Dispatchers.Main) {
                    if (item != null) {
                        // Item found, update UI
                        editTextCategory.setText(item.category)
                        editTextUnit.setText(item.unitName)

                        textViewFeedbackPantry.text = "item found: ${item.name} \n" +
                                "ID: ${item.item_id}\n" +
                                "category: ${item.category}\n" +
                                "unit: ${item.unitName}\n" +
                                "use rate: ${item.useRate} "

                    } else {
                        textViewFeedbackPantry.text = "item not found"
                    }
                }

            } catch (e: Exception){
                textViewFeedbackPantry.text = "Error: ${e.message}"
            }
        }
    }


    // this function takes date string and returns the days since
    private fun calculateDaysSinceInputDate(selectedDate: String): Int {

//

        val dateFormat = SimpleDateFormat("MM/dd/yy", Locale.getDefault())

        // Get the current date
        val currentDate = Date()
        val parsedSelectedDate = dateFormat.parse(selectedDate)
        val selectedDateCalendar = Calendar.getInstance().apply { time = parsedSelectedDate }

        // Calculate the difference in milliseconds between the current date and selected date
        val differenceInMs = currentDate.time - selectedDateCalendar.timeInMillis

        // Convert milliseconds to days
        val daysSince = TimeUnit.MILLISECONDS.toDays(differenceInMs).toInt()

        return daysSince
    }
}