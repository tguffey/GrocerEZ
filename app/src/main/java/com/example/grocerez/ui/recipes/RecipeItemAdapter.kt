package com.example.grocerez.ui.recipes

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.databinding.RecipeItemCellBinding

// Adapter class for the RecyclerView displaying recipe items
class RecipeItemAdapter (
    private var recipeItems: List<RecipeItem>, // List of recipe items to be displayed
    private val clickListener: RecipeItemClickListener // Click listener interface for editing recipe items
): RecyclerView.Adapter<RecipeItemViewHolder>() {

    // Function to update the list of recipe items
    fun updateRecipeItems(newRecipeItems: List<RecipeItem>){
        // Calculate the difference between the old and new list of recipe items
        val diffCallback = RecipeDiffCallback(recipeItems, newRecipeItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        // Update the recipe items list and notify the adapter of the changes
        recipeItems = newRecipeItems
        diffResult.dispatchUpdatesTo(this)
    }

    // Create a new ViewHolder instance
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeItemViewHolder {
        // Inflate the layout for recipe item cells
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeItemCellBinding.inflate(inflater, parent, false)
        // Create and return a new ViewHolder instance
        return RecipeItemViewHolder(parent.context, binding, clickListener)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: RecipeItemViewHolder, position: Int) {
        // Bind the recipe item data to the ViewHolder
        holder.bindRecipeItem(recipeItems[position])
    }

    // Return the total number of recipe items
    override fun getItemCount(): Int = recipeItems.size

}
