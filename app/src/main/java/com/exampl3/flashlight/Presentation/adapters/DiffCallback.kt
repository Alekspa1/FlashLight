package com.exampl3.flashlight.Presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.exampl3.flashlight.Data.Room.Item

class DiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id // Сравниваем строго по ID
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        // Жесткое сравнение ВСЕХ полей, включая sort.
        // Теперь DiffUtil сразу заметит, что sort изменился, и принудительно 
        // заставит ячейку обновить свой текст через bind()!
        return oldItem == newItem 
    }
}
