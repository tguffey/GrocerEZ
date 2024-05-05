package com.example.grocerez.ui.dashboard

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.PantryItem
import com.example.grocerez.databinding.FragmentNewItemSheetBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

// BottomSheetDialogFragment for adding a new task
class NewTaskSheet() : Fragment() {

    private var _binding: FragmentNewItemSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var itemViewModel: DashboardViewModel
    private val calendar = Calendar.getInstance()
    // Define the date formatter
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private lateinit var pantryItemViewModel: DashboardViewModel
    private lateinit var selectedUnit: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewItemSheetBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        val activity = requireActivity()
        itemViewModel = ViewModelProvider(activity)[DashboardViewModel::class.java]

        val args = arguments
        val foodItem = args?.getParcelable<FoodItem>("pantry item")

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


        // Set OnClickListener for cancleButton
        binding.cancelButton.setOnClickListener {
            clearFields()
            findNavController().popBackStack()
        }

        // Set OnClickListener for saveButton
        binding.saveButton.setOnClickListener {
            saveAction()
        }

        binding.btnShowStartDatePicker.setOnClickListener{
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
        val calendar = Calendar.getInstance() // get instance of current date and time

        // extract the 3 from calendar instance
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        binding.startingDate.text.toString().let {
            if (it.isNotEmpty()) {
                // make a list of the date month year sperated by /
                //mm/dd/yy
                val parts =  it.split("/")
                if (parts.size == 3) {
                    month = parts[0].toInt() - 1 // months are 0-indexed in Calendar
                    dayOfMonth = parts[1].toInt()
                    year = parts[2].toInt() + 2000 // Add 2000 to get full year
                }
            }
        }

        // Set up DatePickerDialog to show current date as default
        val datePickerDialog = DatePickerDialog(
            requireContext(), // context
            {  _, selectedYear, selectedMonth, selectedDay ->
                // Update the EditText with the selected date
                // month is default 0 so +1 to format
                // represents day
                // the year mod 100 to have 2 final digit only
                val selectedDate = "${selectedMonth + 1}/$selectedDay/${selectedYear % 100}"
                binding.startingDate.setText(selectedDate) //update the edittext with the slected date
            },
            year,
            month,
            dayOfMonth
        )

        // Show DatePickerDialog to user
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
    }


    // Function to handle save action
    private fun saveAction() {
        val name = binding.name.text.toString()
        val category = binding.category.text.toString()
        val startingDateText = binding.startingDate.text.toString().replace("Date: ", "")
        val expirationLengthText = binding.expirationLength.text.toString().toIntOrNull()?:0
        val quantity = binding.quantity.text.toString().toFloatOrNull()?:0.0f
        selectedUnit = "count"

        // Check if the name is empty
        if (name.isEmpty()) {
            // Show a message to the user indicating that the name cannot be empty
            Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return // Exit the function without adding the item to the list
        }

        // Check if either expiration date or starting date is empty
        if (startingDateText.isEmpty()) {
            Toast.makeText(requireContext(), "Please select both starting and expiration dates", Toast.LENGTH_SHORT).show()
            return
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startingDate = dateFormat.parse(startingDateText)

        CoroutineScope(Dispatchers.IO).launch {
            try {
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
                }
                    // Wait for the item addition operation to complete
                    val displayItem = itemViewModel.findItemByName(name)

                    var pantryName = displayItem?.name ?: name
//                    if (displayItem != null) {
//                        shopListName = itemViewModel.getItemName(displayItem)
////                        Log.v("NEW SHEET", "item dne, ${displayItem.getItemName()}, $name")
//                    }
//                    Log.v("NEW SHEET", "item dne, $displayItem, $name")
                    //now insert shoppingListItem

                    val newPantryItem = PantryItem(
                        itemName = pantryName,
                        amountFromInputDate = quantity,
                        inputDate = startingDateText,
                        shelfLifeFromInputDate = expirationLengthText
                    )



                    itemViewModel.addFoodItem(newPantryItem)



            } catch (e: Exception) {
                // Handle the exception here
                Log.e("Error", "An error occurred: ${e.message}")
                // You can also show an error message to the user if needed
                // For example:
                withContext(Dispatchers.Main) {
                    // Show a toast or a snackbar with the error message
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "An error occurred: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        val startingLocalDate = if (startingDate != null) {
            Instant.ofEpochMilli(startingDate.time)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        } else {
            null
        }

        binding.name.setText("")
        binding.expirationDate.text = "Date: "
        binding.startingDate.text = "Date: "
        findNavController().popBackStack()
    }
}





