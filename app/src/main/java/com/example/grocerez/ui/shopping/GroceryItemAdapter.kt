package com.example.grocerez.ui.shopping

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.databinding.GroceryItemCellBinding

class GroceryItemAdapter(
    private var groceryItems: List<GroceryItem> // List of grocery items to display
) : RecyclerView.Adapter<GroceryItemAdapter.GroceryItemViewHolder>() {

    inner class GroceryItemViewHolder(
        private val context: Context,
        private val binding: GroceryItemCellBinding
    ) :RecyclerView.ViewHolder(binding.root) {

        fun bindGroceryItem(groceryItem: GroceryItem){
            binding.name.text = groceryItem.name
            binding.quantity.text = "Qty: " + groceryItem.quantity.toString()
            binding.notes.text = groceryItem.note

            binding.checkbox.setOnClickListener {
                if (binding.name.getCurrentTextColor() == Color.BLACK) {
                    binding.name.setTextColor(Color.GRAY)
                    binding.name.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    binding.quantity.setTextColor(Color.GRAY)
                    binding.quantity.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    binding.notes.setTextColor(Color.GRAY)
                    binding.notes.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    groceryItem.isChecked = true
                }
                else {
                    binding.name.setTextColor(Color.BLACK)
                    binding.name.paintFlags = 0
                    binding.quantity.setTextColor(Color.BLACK)
                    binding.quantity.paintFlags = 0
                    binding.notes.setTextColor(Color.BLACK)
                    binding.notes.paintFlags = 0
                    groceryItem.isChecked = false
                }
            }
        }
    }
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
