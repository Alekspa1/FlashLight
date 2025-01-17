package com.exampl3.flashlight.Presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.exampl3.flashlight.Data.Room.Item

class DiffCallback: DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }


}
