package com.example.grocerez.ui.shopping

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.databinding.GroceryItemCellBinding

class GroceryItemAdapter(
    private val groceryItems: List<GroceryItem> // List of grocery items to display
) : RecyclerView.Adapter<GroceryItemViewHolder>() {

    // Called when RecyclerView needs a new ViewHolder to represent an item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryItemViewHolder {
        // Inflate the layout of the cell
        val inflater = LayoutInflater.from(parent.context)
        val binding = GroceryItemCellBinding.inflate(inflater, parent, false)

        // Create and return a new View Holder
        return GroceryItemViewHolder(parent.context, binding)
    }

    // Called by RecyclerView to display the data at the specified position
    override fun onBindViewHolder(holder: GroceryItemViewHolder, position: Int) {
        holder.bindGroceryItem(groceryItems[position])
    }

    // Returns the total number of items in shopping list
    override fun getItemCount(): Int = groceryItems.size
}
