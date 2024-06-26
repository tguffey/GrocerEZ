package com.example.grocerez.ui.recipes

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.data.model.Recipe
import com.example.grocerez.databinding.RecipeItemCellBinding

// ViewHolder class for recipe items in the RecyclerView
class RecipeItemViewHolder (
    private val context: Context, // Context for accessing resources or system services
    private val binding: RecipeItemCellBinding, // Binding object for accessing views
    private val clickListener: RecipeItemClickListener // Click listener interface for editing recipe items
):RecyclerView.ViewHolder(binding.root)
{
    // Function to bind recipe item data to the ViewHolder
    fun bindRecipeItem(recipeItem: Recipe){
        // Set the recipe name and description in the corresponding views
        binding.recipeName.text = recipeItem.name
    }
}