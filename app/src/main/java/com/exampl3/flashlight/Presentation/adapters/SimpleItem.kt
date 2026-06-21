package com.exampl3.flashlight.Presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.THEME_FUTURE
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Data.ThemeImp
import com.exampl3.flashlight.Data.sharedPreference.SettingsSharedPreference
import com.exampl3.flashlight.Domain.ItemClickHandler
import com.exampl3.flashlight.databinding.ItemBinding

import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.binding.R
import com.mikepenz.fastadapter.drag.IDraggable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.recyclerview.widget.RecyclerView
import android.view.MotionEvent


class SimpleItem (
    val item: Item,
    val settingPref: SettingsSharedPreference,
    val itemClickHandler: ItemClickHandler,
    val theme: ThemeImp
) : AbstractBindingItem<ItemBinding>(), IDraggable {


    override val type: Int = R.id.fastadapter_item
    override var isDraggable: Boolean = true
    override var identifier: Long = item.id?.toLong() ?: 0L
    var onStartDragListener: (RecyclerView.ViewHolder) -> Unit = { }


    // 5. Создание binding
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemBinding {
        return ItemBinding.inflate(inflater, parent, false)
    }

    // 6. Привязка данных
    override fun bindView(binding: ItemBinding, payloads: List<Any>) {
        with(binding) {

             cardView.setOnTouchListener { _, event ->
        // Если пользователь только что опустил палец на иконку
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            // Ищем ViewHolder этой карточки. В FastAdapter Binding-версии он доступен через тег или binding.root
            val recyclerView = binding.root.parent as? RecyclerView
            val viewHolder = recyclerView?.getChildViewHolder(binding.root)
            
            if (viewHolder != null) {
                // Передаем ViewHolder во фрагмент, чтобы запустить drag
                onStartDragListener?.invoke(viewHolder)
            }
        }
        false // Возвращаем false, чтобы стандартные клики (если они есть) тоже могли работать
    }

            tvTextItem.text = item.name
            tvAlarm.text = alarmText(item) ?: "".trim()
            tvDesc.text = item.desc
            if (tvDesc.text !== "") tvDesc.visibility = View.VISIBLE
            if (item.alarmText.isNotEmpty()) imPhotoView.visibility = View.VISIBLE
            else imPhotoView.visibility = View.GONE
            //настройка темы
            if (settingPref.getTheme() == THEME_FUTURE){
                tvTextItem.setTextAppearance(com.exampl3.flashlight.R.style.StyleItem)
                tvDesc.setTextAppearance(com.exampl3.flashlight.R.style.StyleItemDesc)
                tvAlarm.setTextAppearance(com.exampl3.flashlight.R.style.StyleItem_Alarm)
                when (item.changeAlarm) {
                    true -> {
                        cardView.setBackgroundResource(com.exampl3.flashlight.R.drawable.button_background_alarm)
                        tvAlarm.visibility = View.VISIBLE
                        imAlarm.setImageResource(com.exampl3.flashlight.R.drawable.ic_alarm_on)
                    }

                    false -> {
                        tvAlarm.visibility = View.GONE
                        imAlarm.setImageResource(com.exampl3.flashlight.R.drawable.ic_alarm_off)
                    }
                }
                when (item.change) {
                    true -> {
                        cardView.setBackgroundResource(com.exampl3.flashlight.R.drawable.button_background_true)
                        imStatus.setImageResource(com.exampl3.flashlight.R.drawable.ic_item_true)
                    }

                    false -> {
                        if (!item.changeAlarm) cardView.setBackgroundResource(com.exampl3.flashlight.R.drawable.button_background_false)
                        imStatus.setImageResource(com.exampl3.flashlight.R.drawable.ic_item_false)
                    }
                }
            }
            else {
                imDeleteList.setImageResource(com.exampl3.flashlight.R.drawable.ic_de_zabor)
                tvTextItem.setTextAppearance(com.exampl3.flashlight.R.style.StyleItemZabor)
                tvDesc.setTextAppearance(com.exampl3.flashlight.R.style.StyleItemDescZabor)
                tvAlarm.setTextAppearance(com.exampl3.flashlight.R.style.StyleItem_AlarmZabor)
                imPhotoView.setImageResource(com.exampl3.flashlight.R.drawable.ic_image_zabor)
                when (item.change) {
                    true -> {
                        imStatus.setImageResource(com.exampl3.flashlight.R.drawable.ic_item_true_zabor)
                        cardView
                            .setBackgroundResource(com.exampl3.flashlight.R.drawable.button_background_item_category_zabor_true)
                    }
                    false -> {
                        imStatus.setImageResource(com.exampl3.flashlight.R.drawable.ic_item_false_zabor)
                        cardView
                            .setBackgroundResource(com.exampl3.flashlight.R.drawable.button_background_item_category_zabor_false)
                    }
                }
                when(item.changeAlarm){
                    true-> {
                        tvAlarm.visibility = View.VISIBLE
                        cardView
                            .setBackgroundResource(com.exampl3.flashlight.R.drawable.button_background_item_category_zabor_alarm)
                        imAlarm.setImageResource(com.exampl3.flashlight.R.drawable.ic_alarm_on)
                    }
                    false->{
                        tvAlarm.visibility = View.GONE
                        imAlarm.setImageResource(com.exampl3.flashlight.R.drawable.ic_alarm_zabor)
                    }
                }
            }
            //настройка шрифта
            val listTextView = listOf(tvTextItem,tvAlarm,tvDesc)
            theme.setSizeTextIsList(listTextView)

            cardView.setOnClickListener {
                itemClickHandler.onClick(item, Const.CHANGE_ITEM)
            }
            imStatus.setOnClickListener {
                itemClickHandler.onClick(item, Const.CHANGE)
            }
            imDeleteList.setOnClickListener {
                itemClickHandler.onClick(item, Const.DELETE)
            }
            imAlarm.setOnClickListener {
                itemClickHandler.onClick(item, Const.ALARM)
            }
            imAlarm.setOnLongClickListener {
                itemClickHandler.onLongClick(item)
                true
            }
            imPhotoView.setOnClickListener {
                itemClickHandler.onClick(item, Const.IMAGE )
            }
        }
    }

    // 7. Очистка (опционально)
    override fun unbindView(binding: ItemBinding) {
        binding.tvTextItem.text = null
        binding.tvDesc.text = null
    }

    private fun alarmText(item: Item): String? {
        val timeFormat = "HH:mm"
        val time = SimpleDateFormat(timeFormat, Locale.US)
        val resultTime = time.format(item.alarmTime)
        val resultDate = getFormattedDate(item.alarmTime)
        val alarmText = "Напомнит $resultDate в $resultTime"

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

    private fun getFormattedDate(millis: Long): String {
        val locale = Locale("ru")

        // Получаем начало текущего дня
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // Вычисляем разницу в днях
        val daysDiff = ((millis - todayStart) / (24 * 60 * 60 * 1000)).toInt()

        return when (daysDiff) {
            0 -> "сегодня"
            1 -> "завтра"
            2 -> "послезавтра"
            in 3..6 -> {
                getDayOfWeekWithPreposition(millis)
            }
            else -> SimpleDateFormat("dd.MM.yyyy", locale).format(Date(millis))
        }
    }

    private fun getDayOfWeekWithPreposition(millis: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        return when (dayOfWeek) {
            Calendar.MONDAY -> "в понедельник"
            Calendar.TUESDAY -> "во вторник"
            Calendar.WEDNESDAY -> "в среду"
            Calendar.THURSDAY -> "в четверг"
            Calendar.FRIDAY -> "в пятницу"
            Calendar.SATURDAY -> "в субботу"
            Calendar.SUNDAY -> "в воскресенье"
            else -> {
                val dayName = SimpleDateFormat("EEEE", Locale("ru")).format(Date(millis)).lowercase()
                "в $dayName"
            }
        }
    }
}
