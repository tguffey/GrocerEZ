package com.example.grocerez.ui.recipes

import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.databinding.RecipeIngredientBinding

class IngredientItemViewHolder (

    private val binding: RecipeIngredientBinding
):RecyclerView.ViewHolder(binding.root)
{
    fun bindRecipeIngredient(recipeIngredient: IngredientItem){
        binding.ingredientName.text = recipeIngredient.name
    }
}