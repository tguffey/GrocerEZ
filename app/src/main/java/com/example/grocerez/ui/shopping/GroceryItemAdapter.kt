package com.example.grocerez.ui.shopping

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.data.model.ShoppingListItem
import com.example.grocerez.databinding.GroceryItemCellBinding

class GroceryItemAdapter(
    private var groceryItems: List<ShoppingListItem>, // List of grocery items to display
    private val clickListener: ShoppingItemClickListener
) : RecyclerView.Adapter<GroceryItemAdapter.GroceryItemViewHolder>() {

    inner class GroceryItemViewHolder(
        private val context: Context,
        private val binding: GroceryItemCellBinding,
        private val clickListener: ShoppingItemClickListener
    ) :RecyclerView.ViewHolder(binding.root) {

        fun bindGroceryItem(groceryItem: ShoppingListItem){
            binding.name.text = groceryItem.itemName
            binding.quantity.text = "Qty: " + groceryItem.quantity.toString()
            binding.notes.text = groceryItem.notes

            binding.checkbox.setOnClickListener {
                if (binding.name.getCurrentTextColor() == Color.BLACK) {
                    binding.name.setTextColor(Color.GRAY)
                    binding.name.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    binding.quantity.setTextColor(Color.GRAY)
                    binding.quantity.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    binding.notes.setTextColor(Color.GRAY)
                    binding.notes.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    groceryItem.checkbox = true
                }
                else {
                    binding.name.setTextColor(Color.BLACK)
                    binding.name.paintFlags = 0
                    binding.quantity.setTextColor(Color.BLACK)
                    binding.quantity.paintFlags = 0
                    binding.notes.setTextColor(Color.BLACK)
                    binding.notes.paintFlags = 0
                    groceryItem.checkbox = false
                }
                clickListener.checkItem(groceryItem)
            }
        }
    }
    // Called when RecyclerView needs a new ViewHolder to represent an item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryItemViewHolder {
        // Inflate the layout of the cell
        val inflater = LayoutInflater.from(parent.context)
        val binding = GroceryItemCellBinding.inflate(inflater, parent, false)

        // Create and return a new View Holder
        return GroceryItemViewHolder(parent.context, binding, clickListener)
    }

    // Called by RecyclerView to display the data at the specified position
    override fun onBindViewHolder(holder: GroceryItemViewHolder, position: Int) {
        holder.bindGroceryItem(groceryItems[position])
    }

    // Returns the total number of items in shopping list
    override fun getItemCount(): Int = groceryItems.size
}
