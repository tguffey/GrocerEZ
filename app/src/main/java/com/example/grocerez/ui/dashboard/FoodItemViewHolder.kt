//package com.example.grocerez.ui.dashboard
//
//import android.animation.ObjectAnimator
//import android.content.Context
//import androidx.recyclerview.widget.RecyclerView
//import com.example.grocerez.databinding.FoodItemCellBinding
//
//class FoodItemViewHolder(
//    private val context: Context,
//    private val binding: FoodItemCellBinding,
//    private val clickListener: FoodItemClickListener
//):RecyclerView.ViewHolder(binding.root)
//{
////    fun bindFoodItem(foodItem: PantryItem){
////        binding.name.text = foodItem.name
////        val intValue: Int =  foodItem.calculateProgress(foodItem.startingDate, foodItem.expirationDate) // Default value if newValue is null or not an Int
////
////        // sets food cell as a clickable button to edit the food item data
////        binding.foodCellContainer.setOnClickListener{
////            clickListener.editFoodItem(foodItem)
////        }
////
////        // Animate progress bar if necessary
////        animateProgressBar(intValue)
////    }
//
//
//    // Method to animate progress bar
//    // Method to animate progress bar
//    private fun animateProgressBar(newValue: Int) {
//        // Animate the progress using ObjectAnimator
//        val objectAnimator = ObjectAnimator.ofInt(
//            binding.itemProgressBar,
//            "progress",
//            binding.itemProgressBar.progress,
//            newValue
//        )
//
//        // Set the animation duration
//        objectAnimator.duration = 1000 // 1000 milliseconds (1 second)
//
//        // Start the animation
//        objectAnimator.start()
//    }
//}