package com.example.grocerez.ui.shopping

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerez.R
import com.example.grocerez.data.model.PantryItem
import com.example.grocerez.databinding.FragmentShoppingHistoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class HistoryFragment : Fragment(), HistoryItemClickListener {

    private lateinit var binding: FragmentShoppingHistoryBinding
    private lateinit var viewModel: ShoppingViewModel
    private lateinit var editText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShoppingHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity()
        viewModel = ViewModelProvider(activity)[ShoppingViewModel::class.java]

        val thisClickListener = this
        viewModel.historyItems.observe(viewLifecycleOwner){
            binding.historyRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = HistoryItemAdapter(it, thisClickListener)
            }
        }

        binding.backButton.setOnClickListener {
            // Navigate back
            findNavController().popBackStack()
        }

        // send all checked off items to pantry
        binding.toPantryButton.setOnClickListener {
            val history = viewModel.historyItems.value
            if (history!!.isNotEmpty()) {
                for (item in history) {
                    enterExpirationDateDialog(item.name)
                }
                sendToPantry()
            }

        }
    }

    override fun checkItem(historyItem: HistoryItem) {
        viewModel.toggleCheckHistoryItem(historyItem)
    }

    private fun sendToPantry() {
        val history = viewModel.historyItems.value
        if (history!!.isNotEmpty()) {
            for (item in history) {
                CoroutineScope(Dispatchers.IO).launch {
                    val existingItem = viewModel.findPantryItemByName(item.name)
                    if (existingItem != null) {
                        val updatedQuantity = existingItem.amountFromInputDate + item.quantity.toFloat()
                        existingItem.amountFromInputDate = updatedQuantity
                        viewModel.updatePantryItem(existingItem)
                    } else {
                        val currentDate = Date()
                        val formattedDate = getCurrentDateFormatted(currentDate)
//                        enterExpirationDateDialog(item.name)
                        val shelfLife = calculateShelfLife(currentDate, editText.text.toString())
                        val pantryItem = PantryItem(
                            itemName = item.name,
                            amountFromInputDate = item.quantity.toFloat(),
                            inputDate = formattedDate,
                            shelfLifeFromInputDate = shelfLife
                        )
                        viewModel.addPantryItem(pantryItem)
                    }
                    viewModel.removeHistoryItem(item)
                }
            }
        }
    }

    private fun getCurrentDateFormatted(currentDate: Date): String {
        val dateFormat = SimpleDateFormat("MM/dd/yy")
        return dateFormat.format(currentDate)
    }

    private fun calculateShelfLife(startDate:Date, selectedDate: String): Int {
        val dateFormat = SimpleDateFormat("MM/dd/yy", Locale.getDefault())

        // Get the current date
        val parsedSelectedDate = dateFormat.parse(selectedDate)
        val selectedDateCalendar = Calendar.getInstance().apply { time = parsedSelectedDate }

        // Calculate the difference in milliseconds between the current date and selected date
        val differenceInMs = -(startDate.time - selectedDateCalendar.timeInMillis)

        // Convert milliseconds to days
        val daysSince = TimeUnit.MILLISECONDS.toDays(differenceInMs).toInt()

        return daysSince
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance() // get instance of current date and time

        // extract the 3 from calendar instance
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        editText.text.toString().let {
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
                editText.setText(selectedDate) //update the edittext with the slected date
            },
            year,
            month,
            dayOfMonth
        )

        // Show DatePickerDialog to user
        datePickerDialog.show()
    }

    fun enterExpirationDateDialog(itemName: String) {
        val context = requireContext()
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        builder.setTitle("Expiration Date")
        builder.setMessage("Enter the expiration date (or an estimate) for $itemName")
        val dialogLayout = inflater.inflate(R.layout.dialog_enter_expiration, null)
        editText = dialogLayout.findViewById<EditText>(R.id.date)
        builder.setView(dialogLayout)
        editText.setOnClickListener {
            showDatePicker()
        }
        // remove the checked off items from the list if the OK button it pressed
        builder.setPositiveButton("Save", DialogInterface.OnClickListener { dialog, which ->
            viewModel.removeCheckedItems()
        })
        builder.show()


    }

}