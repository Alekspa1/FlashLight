package com.exampl3.flashlight.Domain.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exampl3.flashlight.Data.Const
import com.exampl3.flashlight.Domain.Room.Item
import com.exampl3.flashlight.Domain.Room.ItemMenu
import com.exampl3.flashlight.R

import com.exampl3.flashlight.databinding.ItemMenuBinding

class ItemMenuListAdapter(private val onClickListener: onClick): ListAdapter<ItemMenu, ItemMenuListAdapter.ViewHolder>(DiffCallbackMenu()) {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemMenuBinding.bind(view)

        fun bindMenu(itemMenu: ItemMenu, onClick: onClick){
            with(binding) {
                tvTitileMenu.text = itemMenu.name

                root.setOnClickListener {
                    onClick.onClick(itemMenu, Const.changeItem)
                }
                imDeleteListMenu.setOnClickListener {
                    onClick.onClick(itemMenu, Const.delete)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMenu(getItem(position), onClickListener)
    }

    interface onClick {
        fun onClick(item: ItemMenu, action: Int)
    }
}