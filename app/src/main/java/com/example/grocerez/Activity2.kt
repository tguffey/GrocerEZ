package com.example.grocerez

import android.os.Bundle
import android.util.Log
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

        }

//        val displayText = findViewById<TextView>(R.id.displayText)
    }

    private fun addItemToDatabase(){

        //get values from the text boxes
        val itemName = findViewById<EditText>(R.id.itemnameText).text.toString()
        val itemCategory = findViewById<EditText>(R.id.itemcategoryText).text.toString()

        messageTextView = findViewById<TextView>(R.id.displayText)
        // Create new Item object
        val newItem = Item(name = itemName, category = itemCategory)
        CoroutineScope(Dispatchers.IO).launch{
            try {
                itemDao.insert(newItem)
                withContext(Dispatchers.Main){
                    messageTextView.text = "Item added successfully! Details: Name: ${newItem.name}, Category: ${newItem.category}"
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
}