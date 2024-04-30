package com.example.grocerez.ui.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.databinding.ShoppingCategoryItemBinding


class CategoryPantryItemAdapter (
    private val categoryPantryItem: List<CategoryPantryItem>,
    private val clickListener: FoodItemClickListener
) : RecyclerView.Adapter<CategoryPantryItemAdapter.CategoryPantryItemViewHolder>(){

    private val viewPool = RecyclerView.RecycledViewPool()

    inner class CategoryPantryItemViewHolder(
        private val context: Context,
        private val binding: ShoppingCategoryItemBinding
    ) :RecyclerView.ViewHolder(binding.root){

        fun bindingCategoryPantryItem(categoryPantryItem: CategoryPantryItem){
            binding.category.text = categoryPantryItem.category.name

            val layoutManager = LinearLayoutManager(
                binding.groceryListRecyclerView.context,
                LinearLayoutManager.VERTICAL,
                false
            )
            layoutManager.initialPrefetchItemCount = categoryPantryItem.pantryItems.size

            val pantryItemAdapter = FoodItemAdapter(categoryPantryItem.pantryItems, clickListener)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryPantryItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ShoppingCategoryItemBinding.inflate(inflater, parent,false)

        return CategoryPantryItemViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(holder: CategoryPantryItemAdapter.CategoryPantryItemViewHolder, position: Int) {
        holder.bindingCategoryPantryItem(categoryPantryItem[position])
    }

    override fun getItemCount(): Int = categoryPantryItem.size

}
