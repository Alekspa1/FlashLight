package com.exampl3.flashlight.Presentation.adapters

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.exampl3.flashlight.Data.Room.Item

class DiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }

    // 👇 ДОБАВЬ ЭТОТ МЕТОД
    override fun getChangePayload(oldItem: Item, newItem: Item): Any? {
        return if (oldItem.id == newItem.id && oldItem.sort != newItem.sort) {
            Bundle() // Пустой bundle = не обновлять UI
        } else {
            super.getChangePayload(oldItem, newItem)
        }
    }
}
