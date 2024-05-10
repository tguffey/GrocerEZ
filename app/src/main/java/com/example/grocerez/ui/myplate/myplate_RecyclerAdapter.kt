package com.example.grocerez.ui.myplate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.R

class MyPlateRecyclerAdapter(private val context: Context, private val foodAmountsModelList: ArrayList<FoodAmountModel>) :
    RecyclerView.Adapter<MyPlateRecyclerAdapter.MyViewHolder>() {

    // ViewHolder inner class
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val recommendedAmount: TextView = itemView.findViewById(R.id.recommended_amount)
        val categoryDescription: TextView = itemView.findViewById(R.id.category_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.myplate_infocells, parent, false) // Replace R.layout.item_layout with your actual layout resource
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        /// Bind data to views here
        val foodAmountModel = foodAmountsModelList[position]
        holder.recommendedAmount.text = foodAmountModel.foodAmount.toString()
        holder.categoryDescription.text = foodAmountModel.categoryDescription
        holder.imageView.setImageResource(foodAmountModel.image)
    }

    override fun getItemCount(): Int {
        return foodAmountsModelList.size
    }
}
