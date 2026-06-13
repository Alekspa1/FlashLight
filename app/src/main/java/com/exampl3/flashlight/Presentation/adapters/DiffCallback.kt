package com.exampl3.flashlight.Presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.exampl3.flashlight.Data.Room.Item

class DiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id || oldItem.sort == newItem.sort
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Item, newItem: Item): Any? {
        // Делаем трюк: берем старую карточку и мысленно меняем ей только поле sort на новое
        val oldWithNewSort = oldItem.copy(sort = newItem.sort)

        // Теперь проверяем: если после этого старая карточка стала АБСОЛЮТНО равна новой,
        // значит, текст, картинка и будильник НЕ МЕНЯЛИСЬ. Изменился ТОЛЬКО порядок (sort).
        if (oldWithNewSort == newItem) {
            // Возвращаем любой не-null объект (например, true).
            // Это сигнал для адаптера: "Обнови данные в памяти, но НЕ запускай анимацию перемещения!"
            return true
        }

        // Если изменилось что-то еще (например, текст дела), возвращаем стандартное поведение
        return super.getChangePayload(oldItem, newItem)
    }


}
