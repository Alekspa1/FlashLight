package com.exampl3.flashlight.Domain.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exampl3.flashlight.Domain.Item
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.ItemBinding

class ItemListAdapter: ListAdapter<Item, ItemListAdapter.ViewHolder>(DiffCallback()) {
    class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemBinding.bind(view)
        fun bind(item: Item){
            binding.textItem.text = item.name
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}