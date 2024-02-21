package com.example.grocerez.ui.recipes

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.databinding.RecipeItemCellBinding

class RecipeItemAdapter (
    private var recipeItems: List<RecipeItem>,
    private val clickListener: RecipeItemClickListener
): RecyclerView.Adapter<RecipeItemViewHolder>()
{

    fun updateRecipeItems(newRecipeItems: List<RecipeItem>){
        val diffCallback = RecipeDiffCallback(recipeItems, newRecipeItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        recipeItems = newRecipeItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeItemCellBinding.inflate(inflater, parent, false)
        return RecipeItemViewHolder(parent.context, binding, clickListener)
    }

    override fun onBindViewHolder(holder: RecipeItemViewHolder, position: Int) {
        holder.bindRecipeItem(recipeItems[position])
    }

    override fun getItemCount(): Int = recipeItems.size

}