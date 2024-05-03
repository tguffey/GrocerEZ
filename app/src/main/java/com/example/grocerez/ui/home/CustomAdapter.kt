package com.example.grocerez.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anychart.data.View
import com.example.grocerez.R

class CustomAdapter(private val listDomains: ArrayList<ListDomain>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listDomains[position])
    }

    override fun getItemCount(): Int {
        return listDomains.size
    }

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val feederName: TextView = itemView.findViewById(R.id.feederName)
        private val removeItem: ImageView = itemView.findViewById(R.id.removeFeeder)

        fun bind(listDomain: ListDomain) {
            feederName.text = listDomain.title
            removeItem.setImageResource(R.drawable.carrots) // Assuming fruits_icon is the default image
        }
    }
}
