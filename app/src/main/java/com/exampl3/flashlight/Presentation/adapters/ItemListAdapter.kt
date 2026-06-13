package com.exampl3.flashlight.Presentation.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.THEME_FUTURE
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Data.ThemeImp
import com.exampl3.flashlight.Data.sharedPreference.SettingsSharedPreference
import com.exampl3.flashlight.Domain.ItemClickHandler
import com.exampl3.flashlight.Presentation.adapters.draganddrop.ItemTouchHelperAdapter
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.ItemBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale

class ItemListAdapter(
    private val itemClickHandler: ItemClickHandler,
    private val onOrderChanged: ((List<Item>) -> Unit)?,
    var touchHelper: ItemTouchHelper?,
    val settingPref: SettingsSharedPreference,
    val theme: ThemeImp
) : ListAdapter<Item, ItemListAdapter.ViewHolder>(DiffCallback()), ItemTouchHelperAdapter {

    class ViewHolder(view: View,private val touchHelper: ItemTouchHelper?,val settingPref: SettingsSharedPreference, val theme: ThemeImp) : RecyclerView.ViewHolder(view) {
        private val binding = ItemBinding.bind(view)


        init {
            binding.cardView.setOnLongClickListener {
                touchHelper?.startDrag(this)
                true
            }
        }

        fun bind(item: Item, itemClickHandler: ItemClickHandler) {
            with(binding) {

                tvTextItem.text = item.name
                tvAlarm.text = alarmText(item) ?: "".trim()
                tvDesc.text = item.desc
                if (tvDesc.text !== "") tvDesc.visibility = View.VISIBLE
                if (item.alarmText.isNotEmpty()) imPhotoView.visibility = View.VISIBLE
                else imPhotoView.visibility = View.GONE
                //настройка темы
                if (settingPref.getTheme() == THEME_FUTURE){
                    tvTextItem.setTextAppearance(R.style.StyleItem)
                    tvDesc.setTextAppearance(R.style.StyleItemDesc)
                    tvAlarm.setTextAppearance(R.style.StyleItem_Alarm)
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
                    tvTextItem.setTextAppearance(R.style.StyleItemZabor)
                    tvDesc.setTextAppearance(R.style.StyleItemDescZabor)
                    tvAlarm.setTextAppearance(R.style.StyleItem_AlarmZabor)
                    imPhotoView.setImageResource(R.drawable.ic_image_zabor)
                    when (item.change) {
                        true -> {
                            imStatus.setImageResource(R.drawable.ic_item_true_zabor)
                            cardView
                                .setBackgroundResource(R.drawable.button_background_item_category_zabor_true)
                        }
                        false -> {
                            imStatus.setImageResource(R.drawable.ic_item_false_zabor)
                            cardView
                                .setBackgroundResource(R.drawable.button_background_item_category_zabor_false)
                        }
                    }
                    when(item.changeAlarm){
                        true-> {
                            tvAlarm.visibility = View.VISIBLE
                            cardView
                                .setBackgroundResource(R.drawable.button_background_item_category_zabor_alarm)
                            imAlarm.setImageResource(R.drawable.ic_alarm_on)
                        }
                        false->{
                            tvAlarm.visibility = View.GONE
                            imAlarm.setImageResource(R.drawable.ic_alarm_zabor)
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

        fun getFormattedDate(millis: Long): String {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ViewHolder(view, touchHelper,settingPref, theme)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), itemClickHandler)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            // Прилетел сигнал, что изменился только порядок (sort).
            // Мы ОБЯЗАНЫ вызвать bind, чтобы обновить текст и исключить дубликаты переиспользования,
            // но благодаря payload система сделает это тихо, без перезапуска анимации полета!
           // holder.bind(getItem(position), itemClickHandler)
        } else {
            // Обычное полное обновление карточки
            super.onBindViewHolder(holder, position, payloads)
        }
    }

      // Локальная копия списка для плавного и мгновенного перемещения элементов на экране
    private var localList: MutableList<Item> = mutableListOf()

    // Переопределяем отправку списка, чтобы синхронизировать данные из БД с локальным списком
    override fun submitList(list: List<Item>?) {
        super.submitList(list)
        localList = list?.toMutableList() ?: mutableListOf()
    }

    // Переопределяем отправку списка с коллбэком (для скролла наверх во фрагменте)
    override fun submitList(list: List<Item>?, commitCallback: Runnable?) {
        super.submitList(list, commitCallback)
        localList = list?.toMutableList() ?: mutableListOf()
    }

       override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < 0 || toPosition < 0) return
        
        // 1. Меняем элементы местами ТОЛЬКО в локальной копии в памяти
        Collections.swap(localList, fromPosition, toPosition)
        
        // 2. Мгновенно и плавно двигаем карточку на экране БЕЗ вызова submitList() и DiffUtil
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onMoveComplete() {
        val totalCount = localList.size

        // 1. Рассчитываем новые индексы sort на основе локального списка
        val itemsWithNewOrder = localList.mapIndexed { index, item ->
            val safeSortIndex = index - totalCount
            item.copy(sort = safeSortIndex)
        }


        // 3. Отправляем отсортированный список во ViewModel для записи в БД
        onOrderChanged?.invoke(itemsWithNewOrder)
    }

    // override fun onItemMove(fromPosition: Int, toPosition: Int) {
    //     val currentList = currentList.toMutableList()
    //     Collections.swap(currentList, fromPosition, toPosition)
    //     submitList(currentList)
    // }

    // override fun onMoveComplete() {
    //     val itemsWithNewOrder = currentList.mapIndexed { index, item ->
    //         item.copy(sort = index)
    //     }
    //     submitList(itemsWithNewOrder)
    //     onOrderChanged?.invoke(itemsWithNewOrder)
    // }


}
