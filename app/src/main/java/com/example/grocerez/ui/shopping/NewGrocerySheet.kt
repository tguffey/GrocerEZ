package com.example.grocerez.ui.shopping

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.grocerez.R

import com.example.grocerez.databinding.FragmentNewShoppingItemBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.grocerez.ui.ItemAmount
import com.example.grocerez.ui.Unit

class NewGrocerySheet(var groceryItem: GroceryItem?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewShoppingItemBinding
    private lateinit var itemViewModel: ShoppingViewModel
    private lateinit var selectedUnit: String

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
        var options: Array<String> = arrayOf("Units")
        options += ItemAmount.getAllUnits()

        val context = requireContext()
        val arrayAdapter = object : ArrayAdapter<String>(context,
            R.layout.shopping_quantity_spinner, options) {

            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

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

                // Only the units choices can be selected and not the Units label
                if (position > 0) {
                    Toast.makeText(
                        context, "Selected : $selectedUnit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // Saves the new grocery item to be added to the shopping list
    private fun saveAction() {
        val name = binding.name.text.toString()
        val category = binding.category.text.toString()
        val quantity =
            Unit.getBySymbol(selectedUnit)?.let {
                ItemAmount(binding.quantity.text.toString().toFloat(),
                    it
                )
            }
        val note = binding.Note.text.toString()
        if (groceryItem == null)
        {
            val newGrocery = quantity?.let { GroceryItem(name, category, it, note) }
            if (newGrocery != null) {
                itemViewModel.addGroceryItem(newGrocery)
            }
        }

        // Clear input fields and dismiss the sheet
        clearFields()
    }
}