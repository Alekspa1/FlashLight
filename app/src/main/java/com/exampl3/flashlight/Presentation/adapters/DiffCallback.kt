package com.exampl3.flashlight.Presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.exampl3.flashlight.Data.Room.Item

class DiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id // Сравниваем по ID
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem // Проверяем все поля, включая sort
    }

    // ДОБАВЬТЕ ЭТОТ МЕТОД:
    override fun getChangePayload(oldItem: Item, newItem: Item): Any? {
        // Если старый и новый элемент отличаются ТОЛЬКО полем sort (текст, ID и будильники те же)
        if (oldItem.copy(sort = newItem.sort) == newItem) {
            return true // Выбрасываем флаг-сигнал: "Изменился только порядок!"
        }
        return super.getChangePayload(oldItem, newItem)
    }
}
