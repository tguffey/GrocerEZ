package com.example.grocerez.ui.shopping

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerez.databinding.ShoppingHistoryItemBinding

class HistoryItemAdapter (
    private val historyItems: List<HistoryItem>,
    private val clickListener: HistoryItemClickListener
) : RecyclerView.Adapter<HistoryItemAdapter.HistoryItemViewHolder>() {
    inner class HistoryItemViewHolder (
        private val context: Context,
        private val binding: ShoppingHistoryItemBinding,
        private val clickListener: HistoryItemClickListener
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bindHistoryItem(historyItem: HistoryItem) {
                binding.name.text = historyItem.name
                binding.quantity.text = "Qty: " +historyItem.quantity+" "+historyItem.unit

                binding.checkbox.setOnClickListener {
//                    clickListener.checkItem(historyItem)
                    if (historyItem.checkbox) {
                        historyItem.checkbox = false
                    }
                    else {
                        historyItem.checkbox = true
                    }
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ShoppingHistoryItemBinding.inflate(inflater, parent, false)

        return HistoryItemViewHolder(parent.context,binding, clickListener)
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        holder.bindHistoryItem(historyItems[position])
    }

    override fun getItemCount(): Int = historyItems.size

}