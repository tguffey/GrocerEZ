package com.example.grocerez.ui.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.data.model.PantryItem
import com.example.grocerez.databinding.FoodItemCellBinding
import java.time.LocalDate

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

        fun bindPantryItem(pantryItem: PantryItem) {
            binding.name.text = pantryItem.itemName
            binding.itemProgressBar.progress = pantryItem.shelfLifeFromInputDate

            binding.root.setOnClickListener {
                onItemClickListener?.invoke(pantryItem)
            }
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
