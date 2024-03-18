package com.example.grocerez.ui.recipes

import androidx.recyclerview.widget.DiffUtil

class RecipeDiffCallback (
    private val oldRecipItems: List<RecipeItem>,
    private val newRecipeItems: List<RecipeItem>
) : DiffUtil.Callback()
{
    override fun getOldListSize(): Int = oldRecipItems.size

    override fun getNewListSize(): Int = newRecipeItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldRecipItems[oldItemPosition].id == newRecipeItems[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldRecipItems[oldItemPosition] == newRecipeItems[newItemPosition]
    }
}