package com.example.grocerez.ui.recipes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.data.model.Recipe
import com.example.grocerez.databinding.RecipeItemCellBinding

// Adapter class for the RecyclerView displaying recipe items
class RecipeItemAdapter (
    private var recipes: List<Recipe>, // List of recipe items to be displayed
    private val clickListener: RecipeItemClickListener // Click listener interface for editing recipe items
): RecyclerView.Adapter<RecipeItemAdapter.RecipeViewHolder>() {

    // Function to update the list of recipe items
    fun updateRecipeItems(newRecipeItems: List<Recipe>){
        // Calculate the difference between the old and new list of recipe items
        val diffCallback = RecipeDiffCallback(recipes, newRecipeItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        // Update the recipe items list and notify the adapter of the changes
        recipes = newRecipeItems
        diffResult.dispatchUpdatesTo(this)
    }

    inner class RecipeViewHolder(
        private val binding: RecipeItemCellBinding,
        private val clickListener: RecipeItemClickListener
    ) : RecyclerView.ViewHolder(binding.root)    {
        fun bindRecipe(recipe: Recipe){
            binding.recipeName.text = recipe.name

            // Set click listener for the root view
            binding.root.setOnClickListener{
                clickListener.editRecipeItem(recipe.name)
            }
        }
    }

    // Create a new ViewHolder instance
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        // Inflate the layout for recipe item cells
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeItemCellBinding.inflate(inflater, parent, false)
        // Create and return a new ViewHolder instance
        return RecipeViewHolder(binding, clickListener)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        // Bind the recipe item data to the ViewHolder
        holder.bindRecipe(recipes[position])
    }

    // Return the total number of recipe items
    override fun getItemCount(): Int = recipes.size
}
