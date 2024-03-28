package com.example.grocerez.ui.myplate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.R

class GoalAdapter(private val goals: List<MyPlateViewModel.Goal>,
                  private val clickListener: GoalClickListener) :
    RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.myplate_goal_cell, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        holder.bind(goal, clickListener)
    }

    override fun getItemCount(): Int {
        return goals.size
    }

    interface GoalClickListener {
        fun onGoalClicked(goal: MyPlateViewModel.Goal)
    }
    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val goalDescriptionTextView: TextView = itemView.findViewById(R.id.goal_description)
        private val caloriesTextView: TextView = itemView.findViewById(R.id.calories_goal)

        fun bind(goal: MyPlateViewModel.Goal, clickListener: GoalClickListener) {
            goalDescriptionTextView.text = goal.goalDescription
            caloriesTextView.text = goal.calories.toString()

            // Set click listener for the item view
            itemView.setOnClickListener {
                clickListener.onGoalClicked(goal)
            }
        }
    }
}
