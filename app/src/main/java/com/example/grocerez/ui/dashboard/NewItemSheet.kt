package com.example.grocerez.ui.dashboard

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.grocerez.dao.CategoryDao
import com.example.grocerez.dao.ItemDao
import com.example.grocerez.dao.UnitDao
import com.example.grocerez.database.AppDatabase
import com.example.grocerez.databinding.FragmentNewItemSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

// BottomSheetDialogFragment for adding a new task
class NewTaskSheet(var foodItem: FoodItem?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewItemSheetBinding
    private lateinit var itemViewModel: DashboardViewModel
    private val calendar = Calendar.getInstance()
    // Define the date formatter
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // declaring all the things related to the database operations
    private lateinit var categoryDao: CategoryDao
    private lateinit var unitDao: UnitDao
    private lateinit var itemDao: ItemDao
    private lateinit var appDatabase: AppDatabase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Initialize database and DAO objects
//        appDatabase = AppDatabase.getInstance(context)
        // Thong: I dont know how contexts work in this case

//        categoryDao = appDatabase.categoryDao()
//        unitDao = appDatabase.unitDao()
//        itemDao = appDatabase.itemDao()


        if(foodItem != null)
        {

            binding.foodTitle.text = "Edit Item"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(foodItem!!.name)
            binding.startingDate.text = "Date: ${foodItem!!.startingDate?.format(dateFormatter)}"
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

        binding.btnShowStartDatePicker.setOnClickListener{
            showStartDatePicker()
        }

        // Set OnClickListener for datePickerButton
        binding.btnShowDatePicker.setOnClickListener {
            showExpDatePicker()
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

    private fun showStartDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(), { datePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                binding.startingDate.text = "Date: $formattedDate"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
//        val colorInt: Int = Color.parseColor("#99D982")
//        datePickerDialog.datePicker.setBackgroundColor(colorInt)
        datePickerDialog.show()
    }

    private fun showExpDatePicker() {
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
        val name = binding.name.text.toString()
        val expirationDateText = binding.expirationDate.text.toString().replace("Date: ", "")
        val startingDateText = binding.startingDate.text.toString().replace("Date: ", "")

        // Check if the name is empty
        if (name.isEmpty()) {
            // Show a message to the user indicating that the name cannot be empty
            Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return // Exit the function without adding the item to the list
        }

        // Check if either expiration date or starting date is empty
        if (expirationDateText.isEmpty() || startingDateText.isEmpty()) {
            Toast.makeText(requireContext(), "Please select both starting and expiration dates", Toast.LENGTH_SHORT).show()
            return
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val expirationDate = dateFormat.parse(expirationDateText)
        val startingDate = dateFormat.parse(startingDateText)

        val expirationLocalDate = if (expirationDate != null) {
            Instant.ofEpochMilli(expirationDate.time)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        } else {
            null
        }

        val startingLocalDate = if (startingDate != null) {
            Instant.ofEpochMilli(startingDate.time)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        } else {
            null
        }

        val value = foodItem?.calculateProgress(startingLocalDate, expirationLocalDate) ?: 0

        if (foodItem == null) {
            val newFood = FoodItem(name, expirationLocalDate, startingLocalDate)
            itemViewModel.addFoodItem(newFood)
        } else {
            itemViewModel.updateFoodItem(foodItem!!.id, name, startingLocalDate, expirationLocalDate)
        }

        binding.name.setText("")
        binding.expirationDate.text = "Date: "
        binding.startingDate.text = "Date: "

        dismiss()
    }
}





