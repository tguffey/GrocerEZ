package com.example.grocerez.ui.dashboard

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.grocerez.databinding.FragmentNewItemSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.time.format.DateTimeFormatter

// BottomSheetDialogFragment for adding a new task
class NewTaskSheet(var foodItem: FoodItem?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewItemSheetBinding
    private lateinit var itemViewModel: DashboardViewModel
    private val calendar = Calendar.getInstance()
    // Define the date formatter
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (foodItem != null) {
            binding.foodTitle.text = "Edit Item"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(foodItem!!.name)
            binding.expirationDate.text = "Date: ${foodItem!!.expirationDate?.format(dateFormatter)}"
        } else {
            binding.foodTitle.text = "New Item"
        }

        // Initialize ViewModel
        val activity = requireActivity()
        itemViewModel = ViewModelProvider(activity)[DashboardViewModel::class.java]

        // Set OnClickListener for cancleButton
        binding.cancelButton.setOnClickListener {
            clearFields()
        }

        // Set OnClickListener for saveButton
        binding.saveButton.setOnClickListener {
            saveAction()
        }

        // Set OnClickListener for datePickerButton
        binding.btnShowDatePicker.setOnClickListener {
            showDatePicker()
        }

        // Set OnEditorActionListener for the TextInputEditText "name"
        binding.name.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Hide the keyboard
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.name.windowToken, 0)

                // Clear focus from the TextInputEditText to remove focus from the text box
                binding.name.clearFocus()
                true // Return true to indicate that the action has been handled
            } else {
                false // Return false to indicate that the action has not been handled
            }
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(), { datePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                binding.expirationDate.text = "Date: $formattedDate"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
//        val colorInt: Int = Color.parseColor("#99D982")
//        datePickerDialog.datePicker.setBackgroundColor(colorInt)
        datePickerDialog.show()
    }

    private fun clearFields() {
        binding.name.setText("")
        dismiss()
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
        // Get name and expiration date from the input fields
        val name = binding.name.text.toString()
        val expirationDateText = binding.expirationDate.text.toString().replace("Date: ", "")
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val expirationDate = dateFormat.parse(expirationDateText)

        // Convert expirationDate to LocalDate
        val localDate = if (expirationDate != null) {
            Instant.ofEpochMilli(expirationDate.time)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        } else {
            null
        }

        // Calculate the time difference in milliseconds
        val currentTime = Calendar.getInstance().timeInMillis
        val timeDifferenceMillis = expirationDate!!.time - currentTime

        // Calculate the total time difference in milliseconds (for example, 30 days)
        val totalDifferenceMillis: Long = 30L * 24 * 60 * 60 * 1000 // Assuming the expiration is 30 days from the current date

        // Calculate the time difference in hours
        val timeDifferenceHours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis).toDouble()

        // Calculate the total time difference in hours
        val totalDifferenceHours = TimeUnit.MILLISECONDS.toHours(totalDifferenceMillis).toDouble()

        // Calculate the percentage
        val percentage = (timeDifferenceHours / totalDifferenceHours) * 100

        // Set the percentage as the value
        val value = percentage.toInt()

        // Add or update the food item in the ViewModel
        if (foodItem == null) {
            val newFood = FoodItem(name, value, localDate, null)
            itemViewModel.addFoodItem(newFood)
        } else {
            itemViewModel.updateFoodItem(foodItem!!.id, name, value, localDate)
        }

        // Clear input fields
        binding.name.setText("")
        binding.expirationDate.text = "Date: "

        // Dismiss the bottom sheet
        dismiss()
    }


}


