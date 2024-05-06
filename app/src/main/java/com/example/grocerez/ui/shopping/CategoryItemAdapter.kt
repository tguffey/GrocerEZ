package com.example.grocerez.ui.shopping

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.data.model.Category
import com.example.grocerez.databinding.ShoppingCategoryItemBinding

class CategoryItemAdapter(
    private val categoryItems: List<CategoryItem>,
    private val clickListener: ShoppingItemClickListener
) : RecyclerView.Adapter<CategoryItemAdapter.CategoryItemViewHolder>() {

//    private lateinit var categoryItems: List<CategoryItem>
    private val viewPool = RecyclerView.RecycledViewPool()

    inner class CategoryItemViewHolder(
        private val context: Context,
        private val binding: ShoppingCategoryItemBinding
    ) :RecyclerView.ViewHolder(binding.root) {

        fun bindCategoryItem(categoryItem: CategoryItem){
            binding.category.text = categoryItem.category.name

            val layoutManager = LinearLayoutManager(
                binding.groceryListRecyclerView.context,
                LinearLayoutManager.VERTICAL,
                false
            )
            layoutManager.initialPrefetchItemCount = categoryItem.shoppingListItems.size

            val groceryItemAdapter = GroceryItemAdapter(categoryItem.shoppingListItems, clickListener)
            binding.groceryListRecyclerView.layoutManager = layoutManager
            binding.groceryListRecyclerView.adapter = groceryItemAdapter
            binding.groceryListRecyclerView.setRecycledViewPool(viewPool)
            groceryItemAdapter.notifyDataSetChanged()
        }
    }

    /*fun setCategories(list: List<CategoryItem>) {
        categoryItems = list
        notifyDataSetChanged()
    }*/

    // Called when RecyclerView needs a new ViewHolder to represent a category
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemAdapter.CategoryItemViewHolder {
        // Inflate the layout of the cell
        val inflater = LayoutInflater.from(parent.context)
        val binding = ShoppingCategoryItemBinding.inflate(inflater, parent, false)

        // Create and return a new View Holder
        return CategoryItemViewHolder(parent.context, binding)
    }

    // Called by RecyclerView to display the data at the specified position
    override fun onBindViewHolder(holder: CategoryItemAdapter.CategoryItemViewHolder, position: Int) {
        holder.bindCategoryItem(categoryItems[position])
    }

    // Returns the total number of categories in shopping list
    override fun getItemCount(): Int = categoryItems.size
}
