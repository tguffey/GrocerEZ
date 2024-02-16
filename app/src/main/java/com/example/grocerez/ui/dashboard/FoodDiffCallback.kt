package com.example.grocerez.ui.dashboard

import androidx.recyclerview.widget.DiffUtil

class FoodItemDiffCallback(
    private val oldFoodItems: List<FoodItem>,
    private val newFoodItems: List<FoodItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldFoodItems.size
    override fun getNewListSize(): Int = newFoodItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldFoodItems[oldItemPosition].id == newFoodItems[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldFoodItems[oldItemPosition] == newFoodItems[newItemPosition]
    }

    // Optional: If you have additional checks for content equality, implement them here
}
