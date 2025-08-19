package com.exampl3.flashlight.Presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.THEME_ZABOR
import com.exampl3.flashlight.Data.Room.ListCategory
import com.exampl3.flashlight.Data.ThemeImp
import com.exampl3.flashlight.Data.sharedPreference.SettingsSharedPreference
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.ItemCategoryBinding


class ListMenuAdapter(
    private val onClickListener: onClick,
    val settingPref: SettingsSharedPreference,
    val theme: ThemeImp
) : ListAdapter<ListCategory, ListMenuAdapter.ViewHolder>(DiffCallbackListCategory()) {

    class ViewHolder(val view: View,val settingPref: SettingsSharedPreference, val theme: ThemeImp) : RecyclerView.ViewHolder(view) {

        private val binding = ItemCategoryBinding.bind(view)

        fun bind(item: ListCategory, onClick: onClick) {
            if (settingPref.getTheme() == THEME_ZABOR) {
                binding.imDeleteList.setImageResource(R.drawable.ic_de_zabor)
                binding.textItem.setBackgroundResource(R.drawable.button_background_item_category_zabor)
                binding.textItem.setTextAppearance(R.style.StyleItemZabor)
            }
            val listText = listOf(binding.textItem)
            theme.setSizeTextIsList(listText)
            binding.textItem.text = item.name

            binding.textItem.setOnClickListener {
                onClick.onClick(item, Const.CHANGE)
            }
            binding.imDeleteList.setOnClickListener {
                onClick.onClick(item, Const.DELETE)
            }
            binding.textItem.setOnLongClickListener {
                onClick.onClick(item, Const.CHANGE_ITEM)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(view,settingPref, theme)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onClickListener)
    }

    interface onClick {
        fun onClick(item: ListCategory, action: Int)
    }

}