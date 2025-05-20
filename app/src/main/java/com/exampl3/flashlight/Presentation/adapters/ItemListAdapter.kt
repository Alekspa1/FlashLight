package com.exampl3.flashlight.Presentation.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.ItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ItemListAdapter(
    private val onLongClickListener: onLongClick,
    private val onClickListener: onClick
) : ListAdapter<Item, ItemListAdapter.ViewHolder>(DiffCallback()) {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemBinding.bind(view)

        fun bind(item: Item, onLongClickListener: onLongClick, onClick: onClick) {
            with(binding) {

                textItem.text = item.name
                tvAlarm.text = alarmText(item) ?: "".trim()
                tvDesc.text = item.desc
                if (tvDesc.text !== "") tvDesc.visibility = View.VISIBLE
                if (item.alarmText.isNotEmpty()) imPhotoView.visibility = View.VISIBLE
                else imPhotoView.visibility = View.GONE
                when (item.changeAlarm) {
                    true -> {
                        cardView.setBackgroundResource(R.drawable.button_background_alarm)
                        tvAlarm.visibility = View.VISIBLE
                        imAlarm.setImageResource(R.drawable.ic_alarm_on)
                    }

                    false -> {
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
                        if (!item.changeAlarm) cardView.setBackgroundResource(R.drawable.button_background_false)
                        imStatus.setImageResource(R.drawable.ic_item_false)
                    }
                }

                cardView.setOnClickListener {
                    onClick.onClick(item, Const.CHANGE_ITEM)
                }
                imStatus.setOnClickListener {
                    onClick.onClick(item, Const.CHANGE)
                }
                imDeleteList.setOnClickListener {
                    onClick.onClick(item, Const.DELETE)
                }
                imAlarm.setOnClickListener {
                    onClick.onClick(item, Const.ALARM)
                }
                imAlarm.setOnLongClickListener {
                    onLongClickListener.onLongClick(item, Const.ALARM)
                    true
                }
            }
        }

        private fun alarmText(item: Item): String? {
            val dateFormat = "dd.MM.yyyy"
            val timeFormat = "HH:mm"
            val date = SimpleDateFormat(dateFormat, Locale.US)
            val time = SimpleDateFormat(timeFormat, Locale.US)
            val resultDate = date.format(item.alarmTime)
            val resutTime = time.format(item.alarmTime)
            val alarmText = "Напомнит: $resultDate в $resutTime"

            when (item.interval) {
                Const.ALARM_ONE -> {
                    return alarmText
                }

                Const.ALARM_DAY -> {
                    return "$alarmText и через день"

                }

                Const.ALARM_WEEK -> {
                    return "$alarmText и через неделю"
                }

                Const.ALARM_MONTH -> {
                    return "$alarmText и через месяц"
                }

                Const.ALARM_YEAR -> {
                    return "$alarmText и через год"
                }


            }
            return null
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
