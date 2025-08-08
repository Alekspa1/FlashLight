package com.exampl3.flashlight.Presentation.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.THEME_FUTURE
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Data.sharedPreference.SettingsSharedPreference
import com.exampl3.flashlight.Presentation.adapters.draganddrop.ItemTouchHelperAdapter
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.ItemBinding
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Locale

class ItemListAdapter(
    private val onLongClickListener: onLongClick,
    private val onClickListener: onClick,
    private val onOrderChanged: ((List<Item>) -> Unit)?,
    var touchHelper: ItemTouchHelper?,
    val settingPref: SettingsSharedPreference
) : ListAdapter<Item, ItemListAdapter.ViewHolder>(DiffCallback()), ItemTouchHelperAdapter {

    class ViewHolder(view: View,private val touchHelper: ItemTouchHelper?,val settingPref: SettingsSharedPreference) : RecyclerView.ViewHolder(view) {
        private val binding = ItemBinding.bind(view)
        private val context = view.context


        init {
            binding.cardView.setOnLongClickListener {
                touchHelper?.startDrag(this)
                true
            }
        }

        fun bind(item: Item, onLongClickListener: onLongClick, onClick: onClick) {
            with(binding) {

                textItem.text = item.name
                tvAlarm.text = alarmText(item) ?: "".trim()
                tvDesc.text = item.desc
                if (tvDesc.text !== "") tvDesc.visibility = View.VISIBLE
                if (item.alarmText.isNotEmpty()) imPhotoView.visibility = View.VISIBLE
                else imPhotoView.visibility = View.GONE
                if (settingPref.getTheme() == THEME_FUTURE){
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
                }
                else {
                    imDeleteList.setImageResource(R.drawable.ic_de_zabor)
                    textItem.setTextColor(context.resources.getColor(R.color.black) )
                    tvDesc.setTextColor(context.resources.getColor(R.color.white) )
                    when (item.change) {
                        true -> {
                            imStatus.setImageResource(R.drawable.ic_item_true_zabor)
                            cardView
                                .setCardBackgroundColor(ContextCompat.getColor(context, R.color.Active))
                        }
                        false -> {
                            imStatus.setImageResource(R.drawable.ic_item_false_zabor)
                            cardView
                                .setCardBackgroundColor(ContextCompat.getColor(context, R.color.NoActive))
                        }
                    }
                    when(item.changeAlarm){
                        true-> {
                            tvAlarm.visibility = View.VISIBLE
                            imAlarm.setImageResource(R.drawable.ic_alarm_on)
                        }
                        false->{
                            tvAlarm.visibility = View.GONE
                            imAlarm.setImageResource(R.drawable.ic_alarm_zabor)
                        }
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
                imPhotoView.setOnClickListener {
                    onClick.onClick(item, Const.IMAGE )
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
        return ViewHolder(view, touchHelper,settingPref)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onLongClickListener, onClickListener)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        val currentList = currentList.toMutableList()
        Collections.swap(currentList, fromPosition, toPosition)
        submitList(currentList)
    }

    override fun onMoveComplete() {
        val itemsWithNewOrder = currentList.mapIndexed { index, item ->
            item.copy(sort = index)
        }
        submitList(itemsWithNewOrder)
        onOrderChanged?.invoke(itemsWithNewOrder)
    }

    interface onLongClick {
        fun onLongClick(item: Item, action: Int)

    }

    interface onClick {
        fun onClick(item: Item, action: Int)
    }

}
