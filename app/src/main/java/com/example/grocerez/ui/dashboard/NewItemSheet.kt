package com.example.grocerez.ui.dashboard

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.grocerez.databinding.FragmentNewItemSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// BottomSheetDialogFragment for adding a new task
class NewTaskSheet(var foodItem: FoodItem?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewItemSheetBinding
    private lateinit var itemViewModel: DashboardViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(foodItem != null)
        {
            binding.foodTitle.text = "Edit Item"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(foodItem!!.name)
            binding.value.text = editable.newEditable(foodItem!!.prog.toString())
        }
        else{
            binding.foodTitle.text = "New Item"
        }

        // Initialize ViewModel
        val activity = requireActivity()
        itemViewModel = ViewModelProvider(activity)[DashboardViewModel::class.java]

        // Set OnClickListener for saveButton
        binding.saveButton.setOnClickListener{
            saveAction()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNewItemSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Function to handle save action
    private fun saveAction() {
        // Set name and value LiveData in ViewModel
        val name = binding.name.text.toString()
        val value = binding.value.text.toString().toInt()
        if(foodItem == null){
            val newFood = FoodItem(name, value, null, null)
            itemViewModel.addFoodItem(newFood)
        }
        else{
            itemViewModel.updateFoodItem(foodItem!!.id, name, value, null)
        }

        // Clear input fields
        binding.name.setText("")
        binding.value.setText("")
        // Dismiss the bottom sheet
        dismiss()
    }
}
