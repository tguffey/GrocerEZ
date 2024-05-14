package com.example.grocerez.ui.dashboard

import android.animation.ValueAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.data.model.PantryItem
import com.example.grocerez.databinding.FoodItemCellBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

// Adapter for the RecyclerView displaying food items
class FoodItemAdapter(
    private var pantryItems: List<PantryItem>, // List of food items to display
    private val clickListener: FoodItemClickListener // Click listener for handling item clicks
) : RecyclerView.Adapter<FoodItemAdapter.PantryItemViewHolder>() {

    private var onItemClickListener: ((PantryItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (PantryItem) -> Unit) {
        onItemClickListener = listener
    }

    inner class PantryItemViewHolder(
        private val context: Context,
        private val binding: FoodItemCellBinding,
        private val clickListener: FoodItemClickListener
    ) :RecyclerView.ViewHolder(binding.root) {

        private val progressBar: ProgressBar = binding.itemProgressBar
        private var currentProgress: Int = 0 // Track current progress

        fun bindPantryItem(pantryItem: PantryItem) {
            binding.name.text = pantryItem.itemName
            val healthiness = calculateHealthiness(pantryItem.inputDate, pantryItem.shelfLifeFromInputDate)
            animateProgressBar(progressBar, currentProgress, healthiness)
            binding.root.setOnClickListener {
                onItemClickListener?.invoke(pantryItem)
            }
        }

        private fun animateProgressBar(progressBar: ProgressBar, from: Int, to: Int) {
            if (from != to) { // Only animate if the progress has changed
                val animator = ValueAnimator.ofInt(from, to)
                animator.addUpdateListener { animation ->
                    progressBar.progress = animation.animatedValue as Int
                }
                animator.duration = 1000 // Adjust the duration of the animation as needed
                animator.start()
                currentProgress = to // Update current progress
            } else {
                progressBar.progress = to // Update progress without animation
            }
        }

        private fun calculateHealthiness(inputDate: String, shelfLife: Int): Int {
            // Parse the input date to LocalDate
            val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yy")
            val currentDate = LocalDate.now()

            // Try parsing the input date with the desired format
            try {
                val inputDateFormatted = LocalDate.parse(convertToDesiredFormat(inputDate), dateFormatter)
                val elapsedDays = ChronoUnit.DAYS.between(inputDateFormatted, currentDate).toInt()
                return ((1 - elapsedDays.toDouble() / shelfLife) * 100).toInt()
            } catch (e: DateTimeParseException) {
                // Handle the case where parsing fails
                // For example, log an error or throw an exception
                throw IllegalArgumentException("Invalid date format: $inputDate")
            }
        }

        private fun convertToDesiredFormat(inputDate: String): String {
            // Split the input date string by '/'
            val parts = inputDate.split("/")
            // Ensure that the month and day are two digits
            val month = parts[0].padStart(2, '0')
            val day = parts[1].padStart(2, '0')
            // Extract the last two digits of the year
            val year = parts[2].takeLast(2)
            // Concatenate the parts in the desired format
            return "$month/$day/$year"
        }

    }

    fun calculateProgress(startingDate: LocalDate?, expirationDate: LocalDate?): Int {
        if (startingDate == null || expirationDate == null) {
            return 0
        }

        val currentDate = LocalDate.now()
        val totalDays = expirationDate!!.toEpochDay() - startingDate!!.toEpochDay()
        val elapsedDays = currentDate.toEpochDay() - startingDate!!.toEpochDay()

        return (((totalDays.toDouble() - elapsedDays.toDouble()) / totalDays.toDouble()) * 100).toInt()
    }

    // Called when RecyclerView needs a new ViewHolder of the given type to represent an item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PantryItemViewHolder {
        // Inflating the layout for the food item cell using Data Binding
        val inflater = LayoutInflater.from(parent.context)
        val binding = FoodItemCellBinding.inflate(inflater, parent, false)
        return PantryItemViewHolder(parent.context, binding, clickListener) // Creating and returning a new ViewHolder
    }

    // Called by RecyclerView to display the data at the specified position
    override fun onBindViewHolder(holder: PantryItemViewHolder, position: Int) {
        holder.bindPantryItem(pantryItems[position]) // Binding the data to the ViewHolder
    }

    // Returns the total number of items in the data set held by the adapter
    override fun getItemCount(): Int = pantryItems.size
}
