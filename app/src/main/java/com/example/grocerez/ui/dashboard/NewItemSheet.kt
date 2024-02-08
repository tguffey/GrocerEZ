package com.example.grocerez.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.grocerez.databinding.FragmentNewItemSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// BottomSheetDialogFragment for adding a new task
class NewTaskSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewItemSheetBinding
    private lateinit var itemViewModel: DashboardViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        itemViewModel.name.value = binding.name.text.toString()
        itemViewModel.value.value = binding.value.text.toString().toInt()
        // Clear input fields
        binding.name.setText("")
        binding.value.setText("")
        // Dismiss the bottom sheet
        dismiss()
    }
}
