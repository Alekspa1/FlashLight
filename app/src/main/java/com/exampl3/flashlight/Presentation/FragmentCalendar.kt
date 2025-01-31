package com.exampl3.flashlight.Presentation


import android.Manifest
import android.app.AlarmManager
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
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Domain.InsertAlarm
import com.exampl3.flashlight.Domain.InsertTime

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
class FragmentCalendar : Fragment(), ItemListAdapter.onLongClick, ItemListAdapter.onClick {
    private lateinit var binding: FragmentBlankFlashLightBinding
    private val modelFlashLight: ViewModelFlashLight by activityViewModels()

    @Inject
    lateinit var db: Database

    @Inject
    lateinit var insertAlarm: InsertAlarm

    @Inject
    lateinit var insertTime: InsertTime
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

    }

    override fun onResume() {
        super.onResume()
        if (modelFlashLight.getPremium()) {
            db.CourseDao().getAll().asLiveData().observe(viewLifecycleOwner) { list ->
                val calendarDays = mutableListOf<CalendarDay>()

                list.forEach { item ->
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
        } else Toast.makeText(
            view?.context,
            "Отображение дел в календаре доступно в PREMIUM версии",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun initRcView() {
        val rcView = binding.rcViewItem
        adapter = ItemListAdapter(this, this)
        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = adapter
    } // инициализировал ресайклер


    companion object {
        fun newInstance() = FragmentCalendar()
    }

    override fun onLongClick(item: Item, action: Int) {
        when (action) {
            Const.alarm -> {
                CoroutineScope(Dispatchers.IO).launch {
                    db.CourseDao().updateItem(item.copy(changeAlarm = !item.changeAlarm))
                }
                if (item.changeAlarm) {
                    insertAlarm.changeAlarmItem(item, Const.deleteAlarm)
                }
                if ((item.change || !item.changeAlarm) && item.alarmTime > calendarZero.timeInMillis) {
                    insertAlarm.changeAlarmItem(
                        item.copy(change = false, changeAlarm = !item.changeAlarm),
                        item.interval
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        db.CourseDao()
                            .updateItem(item.copy(change = false, changeAlarm = !item.changeAlarm))
                    }
                }
                if (!item.changeAlarm && item.alarmTime < calendarZero.timeInMillis) {
                    when (item.interval) {
                        Const.alarmOne -> {
                            Toast.makeText(
                                requireContext(),
                                "Вы выбрали время которое уже прошло",
                                Toast.LENGTH_SHORT
                            ).show()
                            CoroutineScope(Dispatchers.IO).launch {
                                db.CourseDao().updateItem(item.copy(changeAlarm = false))
                            }
                        }

                        Const.alarmDay -> {
                            insertAlarm.insertAlarm(
                                item,
                                item.interval,
                                "и через день",
                                item.alarmTime + AlarmManager.INTERVAL_DAY
                            )
                        }

                        Const.alarmWeek -> {
                            insertAlarm.insertAlarm(
                                item,
                                item.interval,
                                "и через неделю",
                                item.alarmTime + AlarmManager.INTERVAL_DAY * 7
                            )
                        }

                        Const.alarmMonth -> {
                            insertAlarm.insertAlarm(
                                item,
                                item.interval,
                                "и через месяц",
                                item.alarmTime + Const.MONTH
                            )
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
                    db.CourseDao().updateItem(item.copy(change = !item.change))
                    if (item.changeAlarm) {
                        db.CourseDao()
                            .updateItem(item.copy(changeAlarm = false, change = !item.change))
                    }
                    insertAlarm.changeAlarmItem(item, Const.deleteAlarm)

                }
            } // Изменение состояния элемента(активный/неактивный)

            Const.delete -> {
                if (item.change) {
                    CoroutineScope(Dispatchers.IO).launch { db.CourseDao().delete(item) }
                    insertAlarm.changeAlarmItem(item, Const.deleteAlarm)
                } else {
                    insertAlarm.deleteAlertDialog(requireContext(), item)
                }

            } // Удаления элемента

            Const.alarm -> {
                insertTime.datePickerDialog(requireContext(), item)
            } // Установка будильника

            Const.changeItem -> {
                DialogItemList.alertItem(
                    requireContext(),
                    object : DialogItemList.Listener {
                        override fun onClickItem(
                            name: String,
                            action: Int?,
                            id: Int?,
                            desc: String?
                        ) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val newitem = item.copy(name = name, desc = desc)
                                if (item.changeAlarm) insertAlarm.changeAlarmItem(
                                    newitem,
                                    newitem.interval
                                ) // если у item был установлен будильник то, тут мы перезаписываем будильник
                                db.CourseDao().updateItem(newitem)
                                if (action == Const.alarm) {
                                    if (view.let {
                                            it?.let { it1 ->
                                                Const.isPermissionGranted(
                                                    it1.context,
                                                    Manifest.permission.POST_NOTIFICATIONS
                                                )
                                            } == true
                                        }) {
                                        withContext(Dispatchers.Main) {
                                            insertTime.datePickerDialog(
                                                requireContext(),
                                                item
                                            )
                                        }
                                    }
                                } // это если из окна изменения нажал установка будильника
                            }
                        }
                    },
                    item.name, item.id, item.desc
                )

            } // Изменение имени элемента
        }
    }


}