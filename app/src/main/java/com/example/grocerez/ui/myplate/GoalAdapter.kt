package com.example.grocerez.ui.myplate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.R

class GoalAdapter(private val goals: List<MyPlateViewModel.Goal>) :
    RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.myplate_goal_cell, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        holder.bind(goal)
    }

    override fun getItemCount(): Int {
        return goals.size
    }

    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val goalDescriptionTextView: TextView = itemView.findViewById(R.id.goal_description)
        private val caloriesTextView: TextView = itemView.findViewById(R.id.calories_goal)

        fun bind(goal: MyPlateViewModel.Goal) {
            goalDescriptionTextView.text = goal.goalDescription
            caloriesTextView.text = goal.calories.toString()
        }
    }
}
