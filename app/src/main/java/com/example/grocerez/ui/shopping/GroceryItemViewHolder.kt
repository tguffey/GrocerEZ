package com.example.grocerez.ui.shopping

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.databinding.GroceryItemCellBinding

class GroceryItemViewHolder (
    private val context: Context,
    private val binding: GroceryItemCellBinding
):RecyclerView.ViewHolder(binding.root)
{

    fun bindGroceryItem(groceryItem: GroceryItem){
        binding.name.text = groceryItem.name
        binding.category.text = groceryItem.category
    }
}