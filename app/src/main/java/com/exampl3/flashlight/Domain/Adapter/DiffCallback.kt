package com.exampl3.flashlight.Domain.Adapter

import androidx.recyclerview.widget.DiffUtil
import com.exampl3.flashlight.Domain.Item

class DiffCallback: DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }


}
