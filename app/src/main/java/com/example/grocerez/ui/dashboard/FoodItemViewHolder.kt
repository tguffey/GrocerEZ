package com.example.grocerez.ui.dashboard

import android.animation.ObjectAnimator
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.databinding.FoodItemCellBinding

class FoodItemViewHolder(
    private val context: Context,
    private val binding: FoodItemCellBinding,
    private val clickListener: FoodItemClickListener
):RecyclerView.ViewHolder(binding.root)
{
    fun bindFoodItem(foodItem: FoodItem){
        binding.name.text = foodItem.name
        val intValue: Int =  foodItem.prog // Default value if newValue is null or not an Int

        // Animate the progress using ObjectAnimator
        val objectAnimator = ObjectAnimator.ofInt(
            binding.itemProgressBar,
            "progress",
            binding.itemProgressBar.progress,
            intValue
        )

        // Set the animation duration
        objectAnimator.duration = 1000 // 1000 milliseconds (1 second)

        // Start the animation
        objectAnimator.start()

        // sets food cell as a clickable button to edit the food item data
        binding.foodCellContainer.setOnClickListener{
            clickListener.editFoodItem(foodItem)
        }
    }
}