package com.exampl3.flashlight.Presentation



import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Domain.Room.GfgDatabase
import com.exampl3.flashlight.Domain.Room.Item
import com.exampl3.flashlight.Domain.model.InsertTime

import com.exampl3.flashlight.Presentation.adapters.ItemListAdapter
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.FragmentBlankFlashLightBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject


@AndroidEntryPoint
class FragmentFlashLight : Fragment(), ItemListAdapter.onLongClick, ItemListAdapter.onClick {
    private lateinit var binding: FragmentBlankFlashLightBinding
    private val modelFlashLight: ViewModelFlashLight by activityViewModels()
    @Inject
    lateinit var db: GfgDatabase
    @Inject
    lateinit var insertTime: InsertTime
    private lateinit var timePickerDialog: TimePickerDialog
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var calendar: Calendar
    private lateinit var calendarZero: Calendar
    private lateinit var calendarDay: CalendarDay
    private lateinit var adapter: ItemListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlankFlashLightBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()

//        db.CourseDao().getAll().asLiveData().observe(viewLifecycleOwner){list->
//            val calendarDays = mutableListOf<CalendarDay>()
//
//            list.forEach {item->
//                if (item.changeAlarm) {
//                    calendar = Calendar.getInstance()
//                    calendar.timeInMillis = item.alarmTime
//                    calendarDay = CalendarDay(calendar)
//                    calendarDay.imageResource = R.drawable.ic_alarm_on
//                    calendarDays.add(calendarDay)
//                }
//            }
//            binding.calendarView.setCalendarDays(calendarDays)
//        }
//        if (modelFlashLight.getPremium()) {
//            binding.calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
//                override fun onClick(calendarDay: CalendarDay) {
//
//                    db.CourseDao().getAllListCalendarRcView(calendarDay.calendar.timeInMillis)
//                        .asLiveData()
//                        .observe(viewLifecycleOwner) { list ->
//                            val listItemCalendar = mutableListOf<Item>()
//
//                            list.forEach { item ->
//                                if (item.changeAlarm) listItemCalendar.add(item)
//                            }
//                            if (listItemCalendar.isEmpty()) binding.tvDela.visibility = View.VISIBLE
//                            else binding.tvDela.visibility = View.GONE
//                            adapter.submitList(listItemCalendar)
//                        }
//                }
//            })
//        }
    }

    override fun onResume() {
        super.onResume()
        if (modelFlashLight.getPremium()){
            db.CourseDao().getAll().asLiveData().observe(viewLifecycleOwner){list->
                val calendarDays = mutableListOf<CalendarDay>()

                list.forEach {item->
                    if (item.changeAlarm) {
                        calendar = Calendar.getInstance()
                        calendar.timeInMillis = item.alarmTime
                        calendarDay = CalendarDay(calendar)
                        calendarDay.imageResource = R.drawable.ic_alarm_on
                        calendarDays.add(calendarDay)
                    }
                }
                binding.calendarView.setCalendarDays(calendarDays)
            }
            binding.calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
                override fun onClick(calendarDay: CalendarDay) {

                    db.CourseDao().getAllListCalendarRcView(calendarDay.calendar.timeInMillis)
                        .asLiveData()
                        .observe(viewLifecycleOwner) { list ->
                            val listItemCalendar = mutableListOf<Item>()

                            list.forEach { item ->
                                if (item.changeAlarm) listItemCalendar.add(item)
                            }
                            if (listItemCalendar.isEmpty()) binding.tvDela.visibility = View.VISIBLE
                            else binding.tvDela.visibility = View.GONE
                            adapter.submitList(listItemCalendar)
                        }
                }
            })
        } else Toast.makeText(view?.context, "Отображение дел в календаре доступно в PREMIUM версии", Toast.LENGTH_SHORT).show()
    }
    private fun initRcView() {
        val rcView = binding.rcViewItem
        adapter = ItemListAdapter(this, this)
        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = adapter
    } // инициализировал ресайклер


    companion object {
        fun newInstance() = FragmentFlashLight()
    }

    override fun onLongClick(item: Item, action: Int) {
        when(action){
            Const.alarm -> {
                CoroutineScope(Dispatchers.IO).launch {
                    db.CourseDao().update(item.copy(changeAlarm = !item.changeAlarm))
                }
                if (item.changeAlarm) {
                    insertTime.
                    changeAlarmItem(item, Const.deleteAlarm)
                }
                if ((item.change || !item.changeAlarm) && item.alarmTime > calendarZero.timeInMillis) {
                    insertTime.
                    changeAlarmItem(
                        item.copy(change = false, changeAlarm = !item.changeAlarm),
                        item.interval
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        db.CourseDao().update(item.copy(change = false, changeAlarm = !item.changeAlarm))
                    }
                }
                if (!item.changeAlarm && item.alarmTime < calendarZero.timeInMillis) {
                    when (item.interval) {
                        Const.alarmOne -> {
                            Toast.makeText(requireContext(), "Вы выбрали время которое уже прошло", Toast.LENGTH_SHORT).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                db.CourseDao().update(item.copy(changeAlarm = false))
                            }
                        }
                        Const.alarmDay -> {
                            insertTime.
                            insertAlarm(item, item.interval, "и через день", item.alarmTime+ AlarmManager.INTERVAL_DAY)
                        }
                        Const.alarmWeek -> {
                            insertTime.
                            insertAlarm(item,item.interval, "и через неделю", item.alarmTime+ AlarmManager.INTERVAL_DAY * 7)
                        }
                        Const.alarmMonth -> {
                            insertTime.
                            insertAlarm(item,item.interval, "и через месяц", item.alarmTime+ Const.MONTH)
                        }
                    }
                }
            }
        }

    }

    override fun onClick(item: Item, action: Int) {
            when (action) {
                Const.change -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        db.CourseDao().update(item.copy(change = !item.change))
                        if (item.changeAlarm) {
                            db.CourseDao()
                                .update(item.copy(changeAlarm = false, change = !item.change))
                        }
                        insertTime.changeAlarmItem(item, Const.deleteAlarm)

                    }
                } // Изменение состояния элемента(активный/неактивный)

                Const.delete -> {
                    if (item.change) {
                        CoroutineScope(Dispatchers.IO).launch { db.CourseDao().delete(item) }
                        insertTime.changeAlarmItem(item, Const.deleteAlarm)
                    } else {
                        insertTime.deleteAlertDialog(requireContext(), item)
                    }

                } // Удаления элемента

                Const.alarm -> {
                    calendar = Calendar.getInstance()
                    calendarZero = Calendar.getInstance()
                    datePickerDialog(item)
                } // Установка будильника

                Const.changeItem -> {
                    DialogItemList.alertItem(
                        requireContext(),
                        object : DialogItemList.Listener {
                            override fun onClickItem(name: String, action: Int?, id: Int?, desc: String?) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val newitem = item.copy(name = name, desc = desc)
                                    if (item.changeAlarm) insertTime.changeAlarmItem(
                                        newitem,
                                        newitem.interval
                                    ) // если у item был установлен будильник то, тут мы перезаписываем будильник
                                    db.CourseDao().update(newitem)
                                    if (action == Const.alarm) {
                                        if (view.let {
                                                it?.let { it1 ->
                                                    Const.isPermissionGranted(
                                                        it1.context,
                                                        Manifest.permission.POST_NOTIFICATIONS
                                                    )
                                                } == true
                                            }) {
                                            withContext(Dispatchers.Main){datePickerDialog(newitem)}
                                        }
                                    } // это если из окна изменения нажал установка будильника
                                }
                            }
                        },
                        item.name,item.id,item.desc
                    )

                } // Изменение имени элемента
            }
    }
    private fun datePickerDialog(item: Item) {
        calendar = Calendar.getInstance()
        calendarZero = Calendar.getInstance()
        datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                timePickerDialog(item)
            },
            calendarZero.get(Calendar.YEAR),
            calendarZero.get(Calendar.MONTH),
            calendarZero.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    } // Установрка даты

    private fun timePickerDialog(item: Item) {
        timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                DialogItemList.insertAlarm(
                    requireView().context,
                    object : DialogItemList.ActionInt {
                        override fun onClick(result: Int) {
                            if (calendar.timeInMillis >= calendarZero.timeInMillis) {
                                proverkaFree(item, result, calendar.timeInMillis)
                            } else Toast.makeText(
                                view?.context,
                                "Вы выбрали время которое уже прошло",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

            },
            calendarZero.get(Calendar.HOUR_OF_DAY),
            calendarZero.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    } // Установка времени


    private fun proverkaFree(item: Item, result: Int, timeCal: Long) {
        when (result) {
            Const.alarmOne -> {
                insertTime.insertAlarm(item, result, "", timeCal)
            }
            Const.alarmDay -> {
                insertTime.insertAlarm(item, result, "и через день", timeCal)
                if (!modelFlashLight.getPremium()) advertising()

            }

            Const.alarmWeek -> {
                insertTime.insertAlarm(item, result, "и через неделю", timeCal)
                if (!modelFlashLight.getPremium()) advertising()
            }
            Const.alarmMonth -> {
                insertTime.insertAlarm(item, result, "и через месяц", timeCal)
                if (!modelFlashLight.getPremium()) advertising()
            }
            Const.alarmYear -> {
                insertTime.insertAlarm(item, result, "и через год", timeCal)
                if (!modelFlashLight.getPremium()) advertising()
            }
        }
    }
    private fun advertising(){
        (activity as MainActivity).showAd()
    } // запуск рекламы

}