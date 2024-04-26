package com.example.grocerez.ui.shopping

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.grocerez.R
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.ShoppingListItem
import com.example.grocerez.databinding.FragmentNewShoppingItemBinding
import com.example.grocerez.ui.ItemAmount
import com.example.grocerez.ui.Unit
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewGrocerySheet(
    // will need these to implement editing items
    var groceryItem: Item?,
    var categoryItem: Category?
) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewShoppingItemBinding
    private lateinit var itemViewModel: ShoppingViewModel
    private lateinit var selectedUnit: String
//    private val TAG = "NEW ITEM SHEET" // for the debug log

    // Create the UI for the sheet
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNewShoppingItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Assuming the bottom sheet UI is created and showing, listen for button clicks
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        val activity = requireActivity()
        itemViewModel = ViewModelProvider(activity)[ShoppingViewModel::class.java]

        setSpinner()

        // add the entered item to the list
        binding.saveButton.setOnClickListener{
            saveAction()
        }

        // Dismiss input box when the cancel button is pressed
        binding.cancelButton.setOnClickListener {
            clearFields()
        }


    }

    // Clear input fields and hide the bottom sheet
    private fun clearFields() {
        // Set all input fields to empty
        binding.name.setText("")
        binding.category.setText("")
        binding.quantity.setText("")
        binding.Note.setText("")
        // Dismiss the bottom sheet
        dismiss()
    }

    // set the drop down menu
    private fun setSpinner() {
        // Create a list of the units, Units label is the first element
        var options: Array<String> = ItemAmount.getAllUnits()
        options[0] = "Units:"

        val context = requireContext()
        val arrayAdapter = object : ArrayAdapter<String>(context,
            R.layout.shopping_quantity_spinner, options) {

            // Show the Units label as grayed out and choices as black text
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                if (position == 0) {
                    view.setTextColor(Color.GRAY)
                } else {
                    view.setTextColor(Color.BLACK)
                }
                return view
            }
        }
        // adapter for the actual list, creates an Item Amount object for the quantity
        arrayAdapter.setDropDownViewResource(R.layout.shopping_quantity_spinner)
        binding.quantitySpinner.adapter = arrayAdapter

        binding.quantitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedUnit = parent?.getItemAtPosition(position) as String

                if (position == 0) {
                    selectedUnit = "unit"
                }

                // Only the units choices can be selected and not the Units label
                if (position > 0) {
                    Toast.makeText(
                        context, "Selected : $selectedUnit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    Toast.makeText(
                        context, "Quantity Units automatically selected as None",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedUnit = "unit"
            }
        }
    }

    // Saves the new grocery item to be added to the shopping list
    private fun saveAction() {
        val name = binding.name.text.toString()
        val category = binding.category.text.toString()
        // not using this variable right now
//        val quantity =
//            Unit.getBySymbol(selectedUnit)?.let {
//                ItemAmount(
//                    binding.quantity.text.toString().toFloat(),
//                    it
//                )
//            }
        val quantityVal = binding.quantity.text.toString().toFloat()
        val note = binding.Note.text.toString()

        // THONG: i realized i was very stupid with my previous code, here is a cleaner and more effiicent code probably.
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Check if the category exists, and add it if it doesn't
                val existingCategory = itemViewModel.findCategoryByName(category)
                if (existingCategory == null) {
                    val newCategory = Category(category)
                    itemViewModel.addCategory(newCategory)
                }

                // Check if the unit exists, and add it if it doesn't
                val existingUnit = itemViewModel.findUnitByName(selectedUnit)
                if (existingUnit == null) {
                    val newUnit = com.example.grocerez.data.model.Unit(selectedUnit)
                    itemViewModel.addUnit(newUnit)
                }

                val existingItem = itemViewModel.findItemByName(name)

                // see if item exists in the database already or not.
                if (existingItem == null) {
                    // Item doesn't exist, create a new one and insert it into the Item table
                    val newItem = Item(
                        name = name, category = category,
                        unitName = selectedUnit, useRate = 0.0f
                    )
                    itemViewModel.addItem(newItem)

                    // Wait for the item addition operation to complete
                    val displayItem = itemViewModel.findItemByName(name)

                    var shopListName = displayItem?.name ?: name
//                    if (displayItem != null) {
//                        shopListName = itemViewModel.getItemName(displayItem)
////                        Log.v("NEW SHEET", "item dne, ${displayItem.getItemName()}, $name")
//                    }
//                    Log.v("NEW SHEET", "item dne, $displayItem, $name")
                    //now insert shoppingListItem
                    val newShoppingListItem = ShoppingListItem(
                        itemName = shopListName,
                        checkbox = false, notes = note, quantity = quantityVal
                    )
                    itemViewModel.addShoppingListItem(newShoppingListItem)

                } else {
                    // now insert shoppingListItem if item exists
//                    var shopListName = ""
                    val displayItem = itemViewModel.findItemByName(name)
                    var shopListName = displayItem?.name ?: name
//                    if (displayItem != null) {
//                        shopListName = itemViewModel.getItemName(displayItem)
////                        Log.v("NEW SHEET", "item exists, ${displayItem.getItemName()}, $name")
//                    }
//                    Log.v("NEW SHEET", "item exists, $displayItem, $name")
                    val newShoppingListItem = ShoppingListItem(
                        itemName = shopListName,
                        checkbox = false, notes = note, quantity = quantityVal
                    )
                    itemViewModel.addShoppingListItem(newShoppingListItem)
                }

            } catch (e: Exception) {
                // Handle the exception here
                        Log.e("Error", "An error occurred: ${e.message}")
                // You can also show an error message to the user if needed
                // For example:
                withContext(Dispatchers.Main) {
                    // Show a toast or a snackbar with the error message
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

        // Clear input fields and dismiss the sheet
        clearFields()
    }
}