package com.example.grocerez.ui.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.example.grocerez.databinding.FragmentNewShoppingItemBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

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

}