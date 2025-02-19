package com.exampl3.flashlight.Presentation.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.ItemBinding

class ItemListAdapter(private val onLongClickListener: onLongClick,
                      private val onClickListener: onClick
): ListAdapter<Item, ItemListAdapter.ViewHolder>(DiffCallback()) {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = ItemBinding.bind(view)
        fun bind(item: Item, onLongClickListener: onLongClick, onClick: onClick) {


            with(binding) {

                textItem.text = item.name
                tvAlarm.text = item.alarmText
                tvDesc.text = item.desc
                if (tvDesc.text !== "" ) tvDesc.visibility = View.VISIBLE
                when(item.changeAlarm){
                    true-> {
                        cardView.setBackgroundResource(R.drawable.button_background_alarm)
                        tvAlarm.visibility = View.VISIBLE
                        imAlarm.setImageResource(R.drawable.ic_alarm_on)
                    }
                    false->{
                        tvAlarm.visibility = View.GONE
                        imAlarm.setImageResource(R.drawable.ic_alarm_off)
                    }
                }
                    when (item.change) {
                        true -> {
                            cardView.setBackgroundResource(R.drawable.button_background_true)
                            imStatus.setImageResource(R.drawable.ic_item_true)
                        }
                        false -> {
                            if(!item.changeAlarm) cardView.setBackgroundResource(R.drawable.button_background_false)
                            imStatus.setImageResource(R.drawable.ic_item_false)
                        }
                    }

                cardView.setOnClickListener {
                    onClick.onClick(item, Const.changeItem)
                }
                imStatus.setOnClickListener {
                    onClick.onClick(item, Const.change)
                }
                imDeleteList.setOnClickListener {
                    onClick.onClick(item, Const.delete)
                }
                imAlarm.setOnClickListener {
                    onClick.onClick(item, Const.alarm)
                }
                imAlarm.setOnLongClickListener {
                    onLongClickListener.onLongClick(item, Const.alarm)
                    true
                }
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
        fun onLongClick(item: Item, action: Int)

    }
    interface onClick {
        fun onClick(item: Item, action: Int)
    }
}
