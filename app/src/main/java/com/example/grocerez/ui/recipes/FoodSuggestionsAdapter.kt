package com.example.grocerez.ui.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.R
import com.example.grocerez.ui.myplate.RecommendedAmountModel

class FoodSuggestionsAdapter(private val suggestedFoods: List<RecommendedAmountModel>) :
    RecyclerView.Adapter<FoodSuggestionsAdapter.FoodSuggestionViewHolder>() {

    class FoodSuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val suggestedAmountTextView: TextView = itemView.findViewById(R.id.suggested_amount)
        val actualAmountTextView: TextView = itemView.findViewById(R.id.actual_amount)
        val image: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodSuggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dialog_food_suggestions, parent, false)
        return FoodSuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodSuggestionViewHolder, position: Int) {
        val suggestedFood = suggestedFoods[position]
        // Set data to views
        holder.suggestedAmountTextView.text = suggestedFood.suggestedAmount.toString()
        holder.actualAmountTextView.text = suggestedFood.actualAmount.toString()
        holder.image.setImageResource(suggestedFood.image)
    }

    override fun getItemCount(): Int {
        return suggestedFoods.size
    }
}
