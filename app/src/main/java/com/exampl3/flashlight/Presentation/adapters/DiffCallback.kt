package com.exampl3.flashlight.Presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.exampl3.flashlight.Data.Room.Item

class DiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id // Сравниваем строго по ID
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        // Игнорируем поле sort при сравнении контента ячейки.
        // Если текст/картинки те же, то перерисовывать (делать bind) элемент не нужно!
        return oldItem.copy(sort = newItem.sort) == newItem
    }
}
