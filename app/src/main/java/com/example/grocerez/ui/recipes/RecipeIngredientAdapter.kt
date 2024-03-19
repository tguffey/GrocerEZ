package com.example.grocerez.ui.recipes

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.grocerez.databinding.RecipeIngredientBinding

class RecipeIngredientAdapter (
    private val ingredientItems: MutableList<IngredientItem> // List of recipe items to be displayed
): RecyclerView.Adapter<IngredientItemViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientItemViewHolder{
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeIngredientBinding.inflate(inflater, parent, false)
        return IngredientItemViewHolder(binding)
    }

    override fun getItemCount(): Int = ingredientItems.size

    override fun onBindViewHolder(holder: IngredientItemViewHolder, position: Int) {
        holder.bindRecipeIngredient(ingredientItems[position])
    }

    fun addIngredients(ingredientItem: IngredientItem) {
        ingredientItems.add(ingredientItem)
        notifyDataSetChanged()
    }

    fun getIngredients(): List<IngredientItem> {
        return ingredientItems
    }
}
