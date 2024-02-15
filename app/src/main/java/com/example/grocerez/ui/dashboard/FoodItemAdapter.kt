package com.example.grocerez.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.databinding.FoodItemCellBinding

// Adapter for the RecyclerView displaying food items
class FoodItemAdapter(
    private val foodItems: List<FoodItem>, // List of food items to display
    private val clickListener: FoodItemClickListener // Click listener for handling item clicks
) : RecyclerView.Adapter<FoodItemViewHolder>() {

    // Called when RecyclerView needs a new ViewHolder of the given type to represent an item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        // Inflating the layout for the food item cell using Data Binding
        val inflater = LayoutInflater.from(parent.context)
        val binding = FoodItemCellBinding.inflate(inflater, parent, false)
        return FoodItemViewHolder(parent.context, binding, clickListener) // Creating and returning a new ViewHolder
    }

    // Called by RecyclerView to display the data at the specified position
    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        holder.bindFoodItem(foodItems[position]) // Binding the data to the ViewHolder
    }

    // Returns the total number of items in the data set held by the adapter
    override fun getItemCount(): Int = foodItems.size
}
