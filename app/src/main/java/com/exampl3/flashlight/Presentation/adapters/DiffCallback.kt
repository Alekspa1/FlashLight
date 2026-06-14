package com.exampl3.flashlight.Presentation.adapters

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.exampl3.flashlight.Data.Room.Item
class DiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        // Игнорируем sort при сравнении
        return oldItem.name == newItem.name &&
               oldItem.change == newItem.change &&
               oldItem.alarmText == newItem.alarmText &&
               oldItem.alarmTime == newItem.alarmTime &&
               oldItem.changeAlarm == newItem.changeAlarm &&
               oldItem.changeAlarmRepeat == newItem.changeAlarmRepeat &&
               oldItem.interval == newItem.interval &&
               oldItem.category == newItem.category &&
               oldItem.desc == newItem.desc
    }
}
