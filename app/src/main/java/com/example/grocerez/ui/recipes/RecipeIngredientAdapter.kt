package com.example.grocerez.ui.recipes

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.grocerez.databinding.RecipeIngredientBinding

class RecipeIngredientAdapter (
    private var ingredientItems: MutableList<IngredientItem>, // List of recipe items to be displayed
    private val clickListener: IngredentItemClickListener
): RecyclerView.Adapter<IngredientItemViewHolder>()
{
    fun updateIngredientItems(newIngredientItems: MutableList<IngredientItem>){
        val diffCallback = IngredientDiffCallback(ingredientItems, newIngredientItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        ingredientItems = newIngredientItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientItemViewHolder{
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeIngredientBinding.inflate(inflater, parent, false)
        return IngredientItemViewHolder(binding)
    }

    override fun getItemCount(): Int = ingredientItems.size

    override fun onBindViewHolder(holder: IngredientItemViewHolder, position: Int) {
        val ingredientItem = ingredientItems[position]
        holder.bindRecipeIngredient(ingredientItem)

        // Set click listener for each item
        holder.itemView.setOnClickListener {
            clickListener.editIngredientItem(ingredientItem) // Invoke the method in the click listener
        }
    }

    fun addIngredients(ingredientItem: IngredientItem) {
        ingredientItems.add(ingredientItem)
        notifyDataSetChanged()
    }

    fun getIngredients(): List<IngredientItem> {
        return ingredientItems
    }
}
