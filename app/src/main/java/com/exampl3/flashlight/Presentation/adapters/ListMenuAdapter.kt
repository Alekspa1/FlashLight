package com.exampl3.flashlight.Presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Domain.Room.ListCategory
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.CategoryBinding


class ListMenuAdapter(
    private val onClickListener: onClick
): ListAdapter<ListCategory, ListMenuAdapter.ViewHolder>(DiffCallbackListCategory()) {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = CategoryBinding.bind(view)
        fun bind(item: ListCategory, onClick: onClick) {
            binding.textItem.text = item.name
            binding.cardView.setOnClickListener {
                onClick.onClick(item, Const.change)
            }
            binding.imDeleteList.setOnClickListener {
                onClick.onClick(item, Const.delete)
            }
            binding.cardView.setOnLongClickListener {
                onClick.onClick(item, Const.changeItem)
                true
            }
    }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onClickListener)
    }

    interface onClick {
        fun onClick(item: ListCategory, action: Int)
    }

}