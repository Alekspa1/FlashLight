package com.exampl3.flashlight.Domain.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exampl3.flashlight.Domain.Item
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.ItemBinding

class ItemListAdapter(private val onLongClickListener: onLongClick,
                      private val onClickListener: onClick
): ListAdapter<Item, ItemListAdapter.ViewHolder>(DiffCallback()) {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = ItemBinding.bind(view)
       private val context = view.context
        @SuppressLint("ResourceAsColor")
        fun bind(item: Item, onClickListener: onLongClick, onClick: onClick){
            binding.textItem.text = item.name
            binding.root.setOnLongClickListener {
                onClickListener.onLongClick(item)
                true
            }
            binding.root.setOnClickListener {
                onClick.onClick(item)
            }
            when(item.change){
                true-> binding.cardView
                    .setCardBackgroundColor(ContextCompat.getColor(context, R.color.Active))
                false-> binding.cardView
                    .setCardBackgroundColor(ContextCompat.getColor(context, R.color.NoActive))
            }


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onLongClickListener, onClickListener)
    }
    interface onLongClick {
        fun onLongClick(item: Item)

    }
    interface onClick {
        fun onClick(item: Item)

    }
}