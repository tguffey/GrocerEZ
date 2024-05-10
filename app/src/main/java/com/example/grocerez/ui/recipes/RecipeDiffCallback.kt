package com.example.grocerez.ui.recipes

import androidx.recyclerview.widget.DiffUtil
import com.example.grocerez.data.model.Recipe

// Callback class for calculating the difference between old and new recipe item lists
class RecipeDiffCallback (
    private val oldRecipeItems: List<Recipe>, // Old list of recipe items
    private val newRecipeItems: List<Recipe> // New list of recipe items
) : DiffUtil.Callback()
{
    // Return the size of the old recipe item list
    override fun getOldListSize(): Int = oldRecipeItems.size

    // Return the size of the new recipe item list
    override fun getNewListSize(): Int = newRecipeItems.size

    // Check if the old and new items are the same based on their IDs
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldRecipeItems[oldItemPosition].recipeId == newRecipeItems[newItemPosition].recipeId
    }

    // Check if the contents of the old and new items are the same
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldRecipeItems[oldItemPosition] == newRecipeItems[newItemPosition]
    }
}
