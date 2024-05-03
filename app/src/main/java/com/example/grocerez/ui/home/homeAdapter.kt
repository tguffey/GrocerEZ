package com.example.grocerez.ui.home


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.R

class NewsAdapter(private val listDomains: ArrayList<ListDomain>) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.viewholder, parent, false)
        return ViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.feederName.text = listDomains[position].title

        val drawableResourceId = holder.itemView.context.resources.getIdentifier(listDomains[position].url, "drawable", holder.itemView.context.packageName)
        if (drawableResourceId != 0) { // Check if drawable resource is found
            holder.removeItem.setImageResource(drawableResourceId)
        } else {
            // Handle case where drawable resource is not found, for example, set a default image
            holder.removeItem.setImageResource(R.drawable.carrots)
        }
    }

    override fun getItemCount(): Int {
        return listDomains.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val feederName: TextView = itemView.findViewById(R.id.feederName)
        val removeItem: ImageView = itemView.findViewById(R.id.removeFeeder)
    }
}
