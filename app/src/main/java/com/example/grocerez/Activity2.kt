package com.example.grocerez

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Activity2 : AppCompatActivity() {
    private lateinit var itemDao: ItemDao
    private lateinit var messageTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        messageTextView = findViewById<TextView>(R.id.displayText)

        // Initialize database with context
        val database = AppDatabase.getInstance(this)

        // Get the DAO using the database instance
        itemDao = database.itemDao()

        val backButton = findViewById<Button>(R.id.backBtn)
        backButton.setOnClickListener {
            finish()
        }

//        var itemname: EditText? = null
//        var category: EditText? = null
//        var amount: EditText? = null

//        itemname = findViewById(R.id.itemnameText);
//        category = findViewById(R.id.itemcategoryText);
        val inputitemBtn = findViewById<Button>(R.id.inputItemBtn)
        inputitemBtn.setOnClickListener {
            addItemToDatabase()
        }

        val displayAllItems = findViewById<Button>(R.id.displayBtn)
        displayAllItems.setOnClickListener{
            getAllItemsAndShow()
        }

        val deleteItemBtn = findViewById<Button>(R.id.deleteItemBtn)
        deleteItemBtn.setOnClickListener {
            deleteFromDatabase()
        }

        val editItemBtn = findViewById<Button>(R.id.editItemBtn)
        editItemBtn.setOnClickListener {
            val itemName = findViewById<EditText>(R.id.itemnameText).text.toString()
            val itemCategory = findViewById<EditText>(R.id.itemcategoryText).text.toString()

            val activity = this as Activity2
            val intent = Intent(activity, EditItemActivity::class.java)
            startActivity(intent)
//            CoroutineScope(Dispatchers.IO).launch {
//                val itemToEdit = itemDao.findItemByNameAndCategory(itemName, itemCategory)
//                if (itemToEdit != null) {
//                    //item is found, show edit dialog
//                    val activity = this as Activity2
//                    val intent = Intent(activity, EditItemActivity::class.java)
//
//
////                    val itemNameExtra = getString(R.string.extra_item_name)
////                    val itemCategoryExtra = getString(R.string.extra_item_category)
////                    intent.putExtra(itemNameExtra, itemName)
////                    intent.putExtra(itemCategoryExtra, itemCategory)
//
//                    startActivity(intent)
//                    // willl only work if item is definitely not null, or else, itemToEdit will be of type <Item?>
//                } else {
//                    //item is not found. display message:
//                    withContext(Dispatchers.Main){
//                        messageTextView.text = "Item not found in database"
//                    }
//                }
//            }



        }

//        val displayText = findViewById<TextView>(R.id.displayText)
    }

    private fun addItemToDatabase(){

        //get values from the text boxes
        val itemName = findViewById<EditText>(R.id.itemnameText).text.toString()
        val itemCategory = findViewById<EditText>(R.id.itemcategoryText).text.toString()

        // check for blanks
        if(itemName.isEmpty() || itemCategory.isEmpty()){
            messageTextView.text = "Please enter both category and item name."
            return // Exit the function if fields are empty
        }

        // actual insertion:
        // 1. create item object
        // 2. try catch block
        // 3. within try block, find if item is already in db with DAO function findItemByNameAndCategory()
        // 4. if not already in, insert the item and display sucess message.
        // 5. another if else loop within that to check for insert error
        // 6. else (item already exists): display item already in there message
        val newItem = Item(name = itemName, category = itemCategory)
        CoroutineScope(Dispatchers.IO).launch{
            try {
                //find the item first to see if there's anythign in there
                val existingItem = itemDao.findItemByNameAndCategory(itemName, itemCategory)

                // Handle conflict (item already exists)
                if (existingItem == null){
                    val insertedId = itemDao.insert(newItem)
                    withContext(Dispatchers.Main){
                        if (insertedId > 0) {

                            messageTextView.text = "Item added successfully! Details: Name: ${newItem.name}, Category: ${newItem.category}"

                            //clear up the textboxes upon sucessful insert
                            findViewById<EditText>(R.id.itemnameText).text = null
                            findViewById<EditText>(R.id.itemcategoryText).text = null

                        } else {
                            // originally just this
                            messageTextView.text = "Failed to add item! Please try again later"
                        }
                    }
                }else{
                    withContext(Dispatchers.Main) {
                        messageTextView.text = "Item already exists in pantry!"
                    }
                }
            } catch (e: Exception){
                withContext(Dispatchers.Main) {
                    // Handle error gracefully (display error message)
                    Log.e("RoomDatabase", "Error inserting item: ${e.message}")
//                    Toast.makeText(this@Activity2, "Error adding item!", Toast.LENGTH_SHORT).show()
                    withContext(Dispatchers.Main) {
                        messageTextView.text = "Error adding item: ${e.message}"
                    }

                }
            }
        }
    }

    private fun getAllItemsAndShow(){
        messageTextView = findViewById<TextView>(R.id.displayText)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val items = itemDao.getAllItems()
                withContext(Dispatchers.Main) {
                    if(items.isEmpty()){
                        // Build formatted string of items
                        messageTextView.text = "No items found in the database!"
                    } else {
                        val stringBuilder = StringBuilder()
                        for (item in items) {
                            stringBuilder.append("ID: ${item.id}, Name: ${item.name}, Category: ${item.category}\n")
                        }
                        messageTextView.text = stringBuilder.toString()
                    }
                }
            }catch(e: Exception){
                withContext(Dispatchers.Main) {
                    messageTextView.text = "Error reading items: ${e.message}"
                }
            }
        }
    }

    private fun deleteFromDatabase(){
        val itemName = findViewById<EditText>(R.id.itemnameText).text.toString()
        val itemCategory = findViewById<EditText>(R.id.itemcategoryText).text.toString()
        val itemToDelete = Item(name = itemName, category = itemCategory)
        if(itemName.isEmpty() || itemCategory.isEmpty()){
            messageTextView.text = "Please enter both category and item name."
            return
        } // Exit the function if fields are empty
        CoroutineScope(Dispatchers.IO).launch{
            try {
                // use findItemByNameAndCategory for now because it is more reliable to find if item is in there or not
                val itemToDelete = itemDao.findItemByNameAndCategory(itemName, itemCategory)
                // successful deletion
                if (itemToDelete != null){
                    itemDao.delete(itemToDelete)
                    withContext(Dispatchers.Main) {
                        messageTextView.text = "Item deleted successfully!Details: Name: ${itemToDelete.name}, Category: ${itemToDelete.category}"

                        //clear up the textboxes upon sucessful delete
                        findViewById<EditText>(R.id.itemnameText).text = null
                        findViewById<EditText>(R.id.itemcategoryText).text = null
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        messageTextView.text = "Item not found in pantry!"
                    }
                }

                withContext(Dispatchers.Main){

                }
            } catch (e: Exception){
                messageTextView.text = "Error deleting item: ${e.message}"
            }

        }



    }


    private fun showEditItemDialog(item: Item){
        val dialog = AlertDialog.Builder(this).create()
        // Inflate the custom layout for the dialog
        val dialogView = LayoutInflater.from(this).inflate(R.layout.edit_item_dialog, null)

        dialogView.findViewById<Button>(R.id.back_button).setOnClickListener {
            dialog.dismiss() // Dismiss the dialog
        }
        dialog.setView(dialogView)
        dialog.show()
    }
}