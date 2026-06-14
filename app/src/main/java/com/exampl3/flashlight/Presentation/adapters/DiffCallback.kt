package com.exampl3.flashlight.Presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.exampl3.flashlight.Data.Room.Item

class DiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        // Сравниваем строго по ID
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        // Проверяем, изменился ли текст или будильник. Поле sort тут проверять НЕ НАДО!
        // Это защитит карточки от повторной перерисовки и уберет появление дубликатов "15 и 15".
        return oldItem.name == newItem.name && 
               oldItem.desc == newItem.desc && 
               oldItem.alarmTime == newItem.alarmTime &&
               oldItem.category == newItem.category &&
               oldItem.change == newItem.change
    }
}
