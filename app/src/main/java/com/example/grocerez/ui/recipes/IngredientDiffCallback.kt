package com.example.grocerez.ui.recipes
import androidx.recyclerview.widget.DiffUtil

class IngredientDiffCallback
    (private val oldRecipeItems: List<IngredientItem>, // Old list of recipe items
     private val newRecipeItems: List<IngredientItem> // New list of recipe items
) : DiffUtil.Callback()
{
    // Return the size of the old recipe item list
    override fun getOldListSize(): Int = oldRecipeItems.size

    // Return the size of the new recipe item list
    override fun getNewListSize(): Int = newRecipeItems.size

    // Check if the old and new items are the same based on their IDs
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldRecipeItems[oldItemPosition].id == newRecipeItems[newItemPosition].id
    }

    // Check if the contents of the old and new items are the same
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldRecipeItems[oldItemPosition] == newRecipeItems[newItemPosition]
    }
}