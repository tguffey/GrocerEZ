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

class NewGrocerySheet() : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewShoppingItemBinding
    private lateinit var itemViewModel: ShoppingViewModel

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

        // TODO: Set OnClickListener for saveButton
        binding.saveButton.setOnClickListener{

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
                val selectedItemText = parent?.getItemAtPosition(position) as String

                // Only the units choices can be selected and not the Units label
                if (position > 0) {
                    Toast.makeText(
                        context, "Selected : $selectedItemText",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}