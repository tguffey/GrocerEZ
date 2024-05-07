package com.example.grocerez.ui.recipes

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.databinding.RecipeIngredientBinding

class RecipeIngredientAdapter(
    private var ingredientItems: List<IngredientItem>, // List of recipe items to be displayed
    private val clickListener: IngredentItemClickListener
): RecyclerView.Adapter<RecipeIngredientAdapter.IngredientViewHolder>()
{
    inner class IngredientViewHolder(
        private val context: Context,
        private val binding: RecipeIngredientBinding,
        private val clickListener: IngredentItemClickListener
    ) :RecyclerView.ViewHolder(binding.root){
        fun bindIngredients(ingredientItem: IngredientItem){
            val ingredientAdapter = RecipeIngredientAdapter(ingredientItems, clickListener)
            binding.ingredientName.text = ingredientItem.name
            binding.ingredientQuantity.text = ingredientItem.quantity.toString()
            binding.ingredientUnit.text = ingredientItem.ingredientUnit
            ingredientAdapter.notifyDataSetChanged()
        }
    }

    // Constructor for NewRecipeSheet fragment
    constructor(ingredientItems: MutableList<IngredientItem>, clickListener: NewRecipeSheet) : this(ingredientItems, clickListener as IngredentItemClickListener)

    // Constructor for RecipeView fragment
    constructor(ingredientItems: MutableList<IngredientItem>, clickListener: RecipeView) : this(ingredientItems, clickListener as IngredentItemClickListener)


    fun updateIngredientItems(newIngredientItems: MutableList<IngredientItem>){
        val diffCallback = IngredientDiffCallback(ingredientItems, newIngredientItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        ingredientItems = newIngredientItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder{
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeIngredientBinding.inflate(inflater, parent, false)
        return IngredientViewHolder(parent.context, binding, clickListener)
    }

    override fun getItemCount(): Int = ingredientItems.size

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredientItem = ingredientItems[position]
        holder.bindIngredients(ingredientItem)

        // Set click listener for each item
        holder.itemView.setOnClickListener {
            clickListener.editIngredientItem(ingredientItem) // Invoke the method in the click listener
        }
    }

    fun addIngredients(ingredientItem: IngredientItem) {
        ingredientItems
        notifyDataSetChanged()
    }

    fun getIngredients(): List<IngredientItem> {
        return ingredientItems
    }
}
