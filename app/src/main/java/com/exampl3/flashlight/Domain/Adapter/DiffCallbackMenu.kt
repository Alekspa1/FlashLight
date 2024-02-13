package com.exampl3.flashlight.Domain.Adapter

import androidx.recyclerview.widget.DiffUtil
import com.exampl3.flashlight.Domain.Room.ItemMenu

class DiffCallbackMenu: DiffUtil.ItemCallback<ItemMenu>() {
    override fun areItemsTheSame(oldItem: ItemMenu, newItem: ItemMenu): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ItemMenu, newItem: ItemMenu): Boolean {
        return oldItem == newItem
    }


}