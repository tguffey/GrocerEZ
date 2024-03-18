package com.example.grocerez.ui.recipes

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.databinding.RecipeItemCellBinding

class RecipeItemViewHolder (
    private val context: Context,
    private val binding: RecipeItemCellBinding,
    private val clickListener: RecipeItemClickListener
):RecyclerView.ViewHolder(binding.root)
{
    fun bindRecipeItem(recipeItem: RecipeItem){
        binding.recipeName.text = recipeItem.name
        binding.recipeDescription.text =  recipeItem.description

        // sets food cell as a clickable button to edit the food item data
        binding.recipeCellContainer.setOnClickListener{
            clickListener.editRecipeItem(recipeItem)
        }
    }

}