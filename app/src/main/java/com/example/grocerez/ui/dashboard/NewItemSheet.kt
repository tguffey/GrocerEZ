package com.example.grocerez.ui.dashboard

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.grocerez.R
import com.example.grocerez.data.model.Category
import com.example.grocerez.data.model.Item
import com.example.grocerez.data.model.PantryItem
import com.example.grocerez.databinding.FragmentNewItemSheetBinding
import com.example.grocerez.ui.ItemAmount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
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
    private lateinit var itemPrevious: Item
    private lateinit var pantryItemPrevious: PantryItem

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

        // Retrieving the pantryItemId from fragment arguments in onViewCreated
        val args = arguments
        val pantryItemName = args?.getString("pantryItemName", "none") // Default value 'none' if not found
        CoroutineScope(Dispatchers.IO).launch{
            if (pantryItemName != null){
                val pantryItem = itemViewModel.findPantryItemByName(pantryItemName)
                val item = itemViewModel.findItemByName(pantryItemName)
                if(pantryItem != null && item != null)
                {
                    itemPrevious = item
                    pantryItemPrevious = pantryItem
                    binding.foodTitle.text = "Edit Item"
                    withContext(Dispatchers.Main) {
                        binding.name.setText(pantryItem.itemName)
                        binding.startingDate.text = pantryItem.inputDate.format(dateFormatter)
                        binding.quantity.setText(pantryItem.amountFromInputDate.toString())
                        binding.category.setText(item.category)
                        binding.expirationLength.setText(pantryItem.shelfLifeFromInputDate.toString())
                        setSpinner(item.unitName)
                        selectedUnit = item.unitName
                    }
                }
            }
            else {
                binding.foodTitle.text = "New Item"
                selectedUnit = ""
                setSpinner(selectedUnit)
            }
        }

        // Initialize database and DAO objects
//        appDatabase = AppDatabase.getInstance(context)
        // Thong: I dont know how contexts work in this case

//        categoryDao = appDatabase.categoryDao()
//        unitDao = appDatabase.unitDao()
//        itemDao = appDatabase.itemDao()

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

    private fun clearFields() {
        binding.name.setText("")
    }

    private fun setSpinner(initialSelectedUnit: String) {
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
                // Nothing to do here
            }
        }

        // Set the selected unit in the spinner
        val selectedIndex = options.indexOf(initialSelectedUnit)
        if (selectedIndex != -1) {
            binding.quantitySpinner.setSelection(selectedIndex)
        }
    }




    // Function to handle save action
    private fun saveAction() {
        val name = binding.name.text.toString()
        val category = binding.category.text.toString()
        val startingDateText = binding.startingDate.text.toString().replace("Date: ", "")
        val expirationLengthText = binding.expirationLength.text.toString().toIntOrNull()?:0
        val quantity = binding.quantity.text.toString().toFloatOrNull()?:0.0f

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
                val newItem = Item(
                    name = name, category = category,
                    unitName = selectedUnit, useRate = 0.0f
                )

                // see if item exists in the database already or not.
                if (existingItem == null || newItem != itemPrevious) {
                    itemViewModel.addItem(newItem)
                }


//                    if (displayItem != null) {
//                        shopListName = itemViewModel.getItemName(displayItem)
////                        Log.v("NEW SHEET", "item dne, ${displayItem.getItemName()}, $name")
//                    }
//                    Log.v("NEW SHEET", "item dne, $displayItem, $name")
                    //now insert shoppingListItem

                // Check if the pantry item already exists
                val existingPantryItem = itemViewModel.findPantryItemByName(name)
                val newPantryItem = PantryItem(
                    itemName = name,
                    amountFromInputDate = quantity,
                    inputDate = startingDateText,
                    shelfLifeFromInputDate = expirationLengthText
                )

                if (existingPantryItem == null || pantryItemPrevious != newPantryItem) {
                    itemViewModel.addFoodItem(newPantryItem)
                }

                if (binding.foodTitle.text == "Edit Item" && (newPantryItem != pantryItemPrevious && newItem == itemPrevious)){
                    itemViewModel.deletePantryItem(pantryItemPrevious)
                }
                else if (binding.foodTitle.text == "Edit Item" && (newPantryItem == pantryItemPrevious && newItem != itemPrevious)) {
                    itemViewModel.deleteItem(itemPrevious)
                }
                else if (binding.foodTitle.text == "Edit Item"){
                    itemViewModel.deleteItem(itemPrevious)
                    itemViewModel.deletePantryItem(pantryItemPrevious)
                }
                else if (binding.foodTitle.text == "Edit Item" && itemPrevious == newItem && pantryItemPrevious == newPantryItem) {

                }

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

        binding.name.setText("")
        binding.expirationLength.setText("")
        binding.startingDate.text = "Date: "
        findNavController().popBackStack()
    }
}
