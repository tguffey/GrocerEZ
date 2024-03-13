package com.example.grocerez

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.grocerez.ui.theme.GrocerEZTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditItemActivity : ComponentActivity() {

    private lateinit var itemDao: ItemDao // Inject your DAO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_item_dialog)

        // Initialize database with context
        val database = AppDatabase.getInstance(this)

        // Get the DAO using the database instance
        itemDao = database.itemDao()


        // get the strings from the previous activity using an intent
        val itemName = intent.getStringExtra(getString(R.string.extra_item_name))
        val itemCategory = intent.getStringExtra(getString(R.string.extra_item_category))
        val item_id = intent.getLongExtra("item_id", -1L) // use -1L as default if not found



        // find text view of the current displays in resources
        val currentItemNameTextView = findViewById<TextView>(R.id.current_item_name_textview)
        val currentItemCategoryTextView = findViewById<TextView>(R.id.current_category_textview)

        // setting the new string to display_________________________________
        val newItemNameDisplay = "Current Item Name: $itemName"
        val newCategoryNameDisplay = "Current Category: $itemCategory"
        currentItemNameTextView.setText(newItemNameDisplay)
        currentItemCategoryTextView.setText(newCategoryNameDisplay)

        // find textboxes for new field input in resources for name and category
        val edit_item_name_txt = findViewById<EditText>(R.id.new_item_name_editText)
        val edit_category_name_txt = findViewById<EditText>(R.id.new_category_editText)

        // find submit button and display feed back test.
        val submit_btn = findViewById<Button>(R.id.submit_button)
        val feedback_textview = findViewById<TextView>(R.id.feedback_textview)

        submit_btn.setOnClickListener {

            if (edit_item_name_txt.text.isNotEmpty() && edit_category_name_txt.text.isNotEmpty() ){
                val newName = edit_item_name_txt.text.toString()
                val newCategory = edit_category_name_txt.text.toString()

                updateItemInDatabase(item_id, newName, newCategory)



                feedback_textview.text = "fields changed: \nname: " + newName + "\ncategory: "+newCategory
            }else{
                feedback_textview.text = "no changes detected, enter both fields"
            }
        }

        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }
    fun updateItemInDatabase(item_id: Long, newName: String, newCategory: String){
        val updatedItem = Item(item_id, newName, newCategory)
        // Update the item in the database
        CoroutineScope(Dispatchers.IO).launch {
            itemDao.update(updatedItem)
            withContext(Dispatchers.Main) {
//                        // Assuming your ItemDao is named itemDao
//                        itemDao.update(updatedItem)

                findViewById<EditText>(R.id.new_item_name_editText).text = null
                findViewById<EditText>(R.id.new_category_editText).text = null
                findViewById<TextView>(R.id.current_item_name_textview).text = "Current Item Name: $newName"
                findViewById<TextView>(R.id.current_category_textview).text = "Current Category: $newCategory"
            }
        }
    }

}


