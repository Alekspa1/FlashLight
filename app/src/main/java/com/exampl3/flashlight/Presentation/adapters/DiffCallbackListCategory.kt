package com.exampl3.flashlight.Presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.exampl3.flashlight.Domain.Room.ListCategory

class DiffCallbackListCategory: DiffUtil.ItemCallback<ListCategory>() {
    override fun areItemsTheSame(oldItem: ListCategory, newItem: ListCategory): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ListCategory, newItem: ListCategory): Boolean {
        return oldItem == newItem
    }


}
