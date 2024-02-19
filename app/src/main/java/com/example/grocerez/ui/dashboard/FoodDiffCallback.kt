package com.example.grocerez.ui.dashboard

import androidx.recyclerview.widget.DiffUtil

// Custom DiffUtil callback for comparing old and new lists of FoodItem objects
class FoodItemDiffCallback(
    private val oldFoodItems: List<FoodItem>, // List of old FoodItems
    private val newFoodItems: List<FoodItem> // List of new FoodItems
) : DiffUtil.Callback() {

    // Return the size of the old list
    override fun getOldListSize(): Int = oldFoodItems.size

    // Return the size of the new list
    override fun getNewListSize(): Int = newFoodItems.size

    // Check if the items at the same position in the old and new lists are the same based on their ids
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldFoodItems[oldItemPosition].id == newFoodItems[newItemPosition].id
    }

    // Check if the contents of the items at the same position in the old and new lists are the same
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldFoodItems[oldItemPosition] == newFoodItems[newItemPosition]
    }
}
