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

class EditItemActivity : ComponentActivity() {

    private lateinit var itemDao: ItemDao // Inject your DAO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_item_dialog)

        // get the strings from the previous activity
        val itemName = intent.getStringExtra(getString(R.string.extra_item_name))
        val itemCategory = intent.getStringExtra(getString(R.string.extra_item_category))
        val item_id = intent.getLongExtra("item_id", -1L) // use -1L as default if not found



        // find text view in resources
        val currentItemNameTextView = findViewById<TextView>(R.id.current_item_name_textview)
        val currentItemCategoryTextView = findViewById<TextView>(R.id.current_category_textview)

        // setting the new string to display_________________________________
        val newItemNameDisplay = "Current Item Name: $itemName"
        val newCategoryNameDisplay = "Current Category: $itemCategory"
        currentItemNameTextView.setText(newItemNameDisplay)
        currentItemCategoryTextView.setText(newCategoryNameDisplay)

        // find textboxes for new field in resources
        val edit_item_name_txt = findViewById<EditText>(R.id.new_item_name_editText)
        val edit_category_name_txt = findViewById<EditText>(R.id.new_category_editText)

        // find submit button and display feed back test.
        val submit_btn = findViewById<Button>(R.id.submit_button)
        val feedback_textview = findViewById<TextView>(R.id.feedback_textview)

        submit_btn.setOnClickListener {

            if (edit_item_name_txt.text.isNotEmpty() || edit_category_name_txt.text.isNotEmpty() ){
                feedback_textview.text = "fields changed: "
            }else{
                feedback_textview.text = "no changes detected"
            }
        }

        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }
    fun updateItemInDatabase(updatedName: String, updatedCategory: String){

    }

}


