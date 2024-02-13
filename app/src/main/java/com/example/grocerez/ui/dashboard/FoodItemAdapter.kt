package com.example.grocerez.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.databinding.FoodItemCellBinding

class FoodItemAdapter(
    private val foodItems: List<FoodItem>
) : RecyclerView.Adapter<FoodItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = FoodItemCellBinding.inflate(from, parent, false)
        return FoodItemViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        holder.bindFoodItem(foodItems[position])
    }

    override fun getItemCount(): Int = foodItems.size



}