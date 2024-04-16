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
import com.example.grocerez.dao.CategoryDao
import com.example.grocerez.dao.ItemDao
import com.example.grocerez.dao.ShoppingListItemDao
import com.example.grocerez.dao.UnitDao
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.ShoppingListItem
import com.example.grocerez.data.model.Unit
import com.example.grocerez.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewShoppingListItemActivity : AppCompatActivity() {

    private lateinit var buttonSearchItemExternal: Button
    private lateinit var buttonInsert: Button
    private lateinit var buttonDisplay: Button
    private lateinit var buttonSearch: Button

    private lateinit var editTextName: EditText
    private lateinit var editTextCategory: EditText
    private lateinit var editTextUnit:EditText
    private lateinit var editTextNotes:EditText
    private lateinit var editTextNumberDecimal: EditText

    private lateinit var textViewFeedback_box: TextView


    private lateinit var categoryDao: CategoryDao
    private lateinit var unitDao: UnitDao
    private lateinit var itemDao: ItemDao
    private lateinit var shoppingListItemDao: ShoppingListItemDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_shopping_list_item)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val appDatabase = AppDatabase.getInstance(applicationContext)
        categoryDao = appDatabase.categoryDao()
        unitDao = appDatabase.unitDao()
        itemDao = appDatabase.itemDao()
        shoppingListItemDao = appDatabase.shoppingListItemDao()

        editTextName = findViewById(R.id.editTextItemName)
        editTextCategory = findViewById(R.id.editTextCategory)
        editTextUnit = findViewById(R.id.editTextUnit)
        editTextNotes = findViewById(R.id.editTextNotes)
        editTextNumberDecimal = findViewById(R.id.editTextNumberDecimal)

        // Define variables to track if EditText fields are empty
        var isNameNotEmpty = false
        var isCategoryNotEmpty = false
        var isUnitNotEmpty = false
        var isNotesNotEmpty = false
        var isNumberDecimalNotEmpty = false
        var isAllRequiredFieldNotEmpty = false

//
        buttonSearchItemExternal = findViewById(R.id.findItemExternalButton)
        buttonInsert = findViewById(R.id.addShopItem_button)
        buttonDisplay = findViewById(R.id.displayShopList_button)
        buttonSearch = findViewById(R.id.searchShopItemByName_button)

        textViewFeedback_box = findViewById(R.id.textViewFeedbackBox)


        buttonSearchItemExternal.setOnClickListener {
            // if the item name is not empty, find it in the other table.
            // this function already has null check
            searchItemAndPopulateFields()
        }

        buttonInsert.setOnClickListener {
            if (areAllRequiredFieldsNotNull() == false){
                textViewFeedback_box.text = "please fill out all the required fields (all fields are required except for notes)"
            } else {
                val itemName = editTextName.text.toString()
                val category = editTextCategory.text.toString()
                val unit = editTextUnit.text.toString()
                val notes = editTextNotes.text.toString()
                val quantity = editTextNumberDecimal.text.toString().toFloatOrNull() ?: 0.0f
                val checkBox = false
                textViewFeedback_box.text = ""
                addShoppingListItem(itemName = itemName, category = category, unit = unit, checkbox = checkBox, notes = notes, quantity = quantity)
            }
        }

        buttonDisplay.setOnClickListener {
            displayAllShoppingListItem()
        }

        buttonSearch.setOnClickListener {
            val itemName = editTextName.text.toString()

            if (itemName.isEmpty()){
                textViewFeedback_box.text = "you cant leave the name empty"
            } else {
                searchShoppingListItemByName(itemName = itemName)
            }


        }
    }

    fun areAllRequiredFieldsNotNull(): Boolean {
        val isNameNotEmpty = editTextName.text.isNotEmpty()
        val isCategoryNotEmpty = editTextCategory.text.isNotEmpty()
        val isUnitNotEmpty = editTextUnit.text.isNotEmpty()
        val isNumberDecimalNotEmpty = editTextNumberDecimal.text.isNotEmpty()

        // Check if all required fields (excluding notes) are not empty
        return isNameNotEmpty && isCategoryNotEmpty && isUnitNotEmpty && isNumberDecimalNotEmpty
        // ____________________- Put this in a button listener
//        if (editTextName.text.isNotEmpty()) {isNameNotEmpty = true }
//        if (editTextCategory.text.isNotEmpty()) {isCategoryNotEmpty = true }
//        if (editTextUnit.text.isNotEmpty()) {isUnitNotEmpty = true }
//        if (editTextNotes.text.isNotEmpty()) {isNotesNotEmpty = true }
//        if (editTextNumberDecimal.text.isNotEmpty()) {isNumberDecimalNotEmpty = true }
//
//        // if all required fields (exclude notes) are not empty, set this one var to true
//        if (isNameNotEmpty && isCategoryNotEmpty && isUnitNotEmpty && isNumberDecimalNotEmpty){
//            isAllRequiredFieldNotEmpty = true
//        }
//        // ___________________________________________________________________________

    }

    // has null check and error handling.
    private fun searchItemAndPopulateFields() {
        val itemName = editTextName.text.toString().trim()
        // if variable is empty, stop here
        if (itemName.isEmpty()) {
            textViewFeedback_box.text = "Please enter a name"
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

                        textViewFeedback_box.text = "item found: ${item.name} \n" +
                                "ID: ${item.item_id}\n" +
                                "category: ${item.category}\n" +
                                "unit: ${item.unitName}\n" +
                                "use rate: ${item.useRate} "

                    } else {
                        textViewFeedback_box.text = "item not found"
                    }
                }

            } catch (e: Exception){
                textViewFeedback_box.text = "Error: ${e.message}"
            }
        }
    }

    private fun addToItemTable(itemName: String, category: String, unit: String){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existingCategory = categoryDao.findCategoryByName(category)
                val existingUnit = unitDao.findUnitByName(unit)

                if (existingCategory == null || existingUnit == null){
                    withContext(Dispatchers.Main){
                        textViewFeedback_box.text = "category or unit does not exist yet. now adding \n"
                        val newCategory = Category(category)
                        val newUnit = Unit(unit)
                        categoryDao.insertCategory(newCategory)
                        unitDao.insertUnit(newUnit)

                    }



                    withContext(Dispatchers.Main){
                        textViewFeedback_box.append("\n new category is inserted \n")
                    }
                    val newItem = Item(name = itemName, category = category, unitName = unit, useRate = 0.0f)
                    itemDao.insertItem(newItem)
                } else {
                    val newItem = Item(name = itemName, category = category, unitName = unit, useRate = 0.0f)
                    itemDao.insertItem(newItem)
                }
                // we can do this because the respective DAO objects does replace on conflict

            }catch (e: Exception){
                textViewFeedback_box.text = "adding item to item table Error: ${e.message}"
            }
        }
    }
    // the ultimate goal.
    // TODO: update function, add items on duplicate entry, now that we have the find function.
    private fun addShoppingListItem(itemName: String, category: String, unit: String, checkbox: Boolean, notes: String, quantity: Float){

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existingItem = itemDao.findItemByName(itemName)

                // see if item exists in the database already or not.
                if (existingItem == null) {
                    textViewFeedback_box.text = "no existinge item exist."
                    // Item doesn't exist, create a new one and insert it into the Item table
//                    val newItem = Item(name = itemName, category = category, unitName = unit, useRate = 0.0f)
//
//                    itemDao.insertItem(newItem)
//                    addToItemTable(itemName,category,unit)
                    try {
                        val existingCategory = categoryDao.findCategoryByName(category)
                        val existingUnit = unitDao.findUnitByName(unit)

                        if (existingCategory == null || existingUnit == null){
                            withContext(Dispatchers.Main){
                                textViewFeedback_box.text = "category or unit does not exist yet. now adding \n"
                                val newCategory = Category(category)
                                val newUnit = Unit(unit)
                                categoryDao.insertCategory(newCategory)
                                unitDao.insertUnit(newUnit)

                            }



                            withContext(Dispatchers.Main){
                                textViewFeedback_box.append("\n new category is inserted \n")
                            }
                            val newItem = Item(name = itemName, category = category, unitName = unit, useRate = 0.0f)
                            itemDao.insertItem(newItem)
                        } else {
                            val newItem = Item(name = itemName, category = category, unitName = unit, useRate = 0.0f)
                            itemDao.insertItem(newItem)
                        }
                        // we can do this because the respective DAO objects does replace on conflict

                    }catch (e: Exception){
                        textViewFeedback_box.text = "adding item to item table Error: ${e.message}"
                    }
                    //_______________________________
                    val displayItem = itemDao.findItemByName(itemName)
                    var displayString = ""
                    if (displayItem != null){
                        displayString = "\nitem doesn't exist yet. Adding new item: \n" +
                                "id: ${displayItem.item_id} \n" +
                                "name: ${displayItem.name} \n" +
                                "category: ${displayItem.category} \n" +
                                "userate: ${displayItem.useRate} ${displayItem.unitName} per week \n"
                    } else {
                        displayString = "error inserting item"
                    }

                    var shopListName = "a"
                    if (displayItem != null){
                        shopListName = displayItem.name
                    }
                    //now insert shoppingListItem
                    val newShoppingListItem = ShoppingListItem(itemName = shopListName, checkbox = false, notes = notes, quantity = quantity)
                    shoppingListItemDao.insertShoppingListItem(newShoppingListItem)

                    displayString = displayString +
                            "\nsucessfully insert new item into shopping list: \n" +
                            "name: ${itemName} \n" +
                            "check: ${checkbox} \n" +
                            "notes: ${notes} \n" +
                            "quantity: ${quantity} ${unit}"

                    withContext(Dispatchers.Main){
                        textViewFeedback_box.append(displayString)
                    }

                } else if (existingItem != null){
                    //now insert shoppingListItem
                    var shopListName = "a"
                    val displayItem = itemDao.findItemByName(itemName)
                    if (displayItem != null){
                        shopListName = displayItem.name
                    }
                    val newShoppingListItem = ShoppingListItem(itemName = shopListName, checkbox = false, notes = notes, quantity = quantity)
                    shoppingListItemDao.insertShoppingListItem(newShoppingListItem)

                    var displayString = "item is found in item table, no need to insert new."+
                            "\nsucessfully insert new item into shopping list: \n" +
                            "name: ${itemName} \n" +
                            "check: ${checkbox} \n" +
                            "notes: ${notes} \n" +
                            "quantity: ${quantity} ${unit}"
                    withContext(Dispatchers.Main){
                        textViewFeedback_box.append(displayString)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        textViewFeedback_box.text = "looks lke there were problems inserting"
                    }
                }

            } catch (e: Exception){
                withContext(Dispatchers.Main) {
                    textViewFeedback_box.append( "ShopListError: ${e.message}")
                }

            }
        }
    }

    private fun displayAllShoppingListItem(){

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val allShoppingListItems = shoppingListItemDao.getAllShoppingListItem()
                withContext(Dispatchers.Main) {
                    if (allShoppingListItems.isEmpty()){
                        textViewFeedback_box.text = "no items added to shopping list yet"
                    } else {
                        val itemListText = StringBuilder()
                        for (shopListItem in allShoppingListItems) {
                            // retrieving data to display
                            val item = itemDao.findItemByName(shopListItem.itemName)

                            var unitForThis = "unit"
                            if (item != null){
                                unitForThis = item.unitName.toString()
                            }

                            //string to display
                            var displayString = "ID: ${shopListItem.shoppingListItemId},\n" +
                                    "Name: ${shopListItem.itemName},\n"+
                                    "Check: ${shopListItem.checkbox},\n"+
                                    "notes: ${shopListItem.notes},\n"+
                                    "quantity: ${shopListItem.quantity} $unitForThis\n\n"

                            itemListText.append(displayString)
                        }
                        textViewFeedback_box.text = "shopping list:\n" + itemListText.toString()
                    }
                }
            }catch (e: Exception){
                textViewFeedback_box.text = "error when display: $e"
            }
        }
    }

    private fun searchShoppingListItemByName(itemName: String){
        CoroutineScope(Dispatchers.IO).launch{
            try{
                val specificItem = shoppingListItemDao.findShoppingListItemByName(itemName)
                withContext(Dispatchers.Main) {
                    if (specificItem != null) {
                        // _____________________ retrievin field from items table
                        val item = itemDao.findItemByName(specificItem.itemName)
                        var unitForThis = "unit"
                        if (item != null){
                            unitForThis = item.unitName.toString()
                        }
                        // ____________________

                        textViewFeedback_box.text = "shopping list item found: \n" +
                                "ID: ${specificItem.shoppingListItemId},\n" +
                                "Name: ${specificItem.itemName},\n"+
                                "Check: ${specificItem.checkbox},\n"+
                                "notes: ${specificItem.notes},\n"+
                                "quantity: ${specificItem.quantity} $unitForThis\n\n"
                    } else {
                        textViewFeedback_box.text = "item not found"
                    }
                }

            }catch (e: Exception){

            }
            shoppingListItemDao.findShoppingListItemByName(itemName)
        }

    }
}