package com.exampl3.flashlight.Presentation


import android.Manifest
import android.os.Build

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.ALARM
import com.exampl3.flashlight.Const.CHANGE
import com.exampl3.flashlight.Const.CHANGE_ITEM
import com.exampl3.flashlight.Const.DELETE
import com.exampl3.flashlight.Const.IMAGE
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.Room.Item

import com.exampl3.flashlight.Presentation.adapters.ItemListAdapter
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.FragmentCalendarBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject


@AndroidEntryPoint
class FragmentCalendar : Fragment(), ItemListAdapter.onClick, ItemListAdapter.onLongClick {
    private lateinit var binding: FragmentCalendarBinding
    private val modelFlashLight: ViewModelFlashLight by activityViewModels()

    @Inject
    lateinit var db: Database

    private lateinit var calendar: Calendar
    private lateinit var calendarZero: Calendar
    private lateinit var calendarDay: CalendarDay
    private lateinit var calendarDayB: Calendar
    private lateinit var adapter: ItemListAdapter
    private lateinit var pLauncher: ActivityResultLauncher<String>

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            modelFlashLight.uriPhoto.value = it.toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        calendarDayB = Calendar.getInstance()

        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

        binding.imBAddCalendar.setOnClickListener {
            if (modelFlashLight.getPremium())
                if (getDateNow(calendarDayB) >= getDateNow(calendarZero)) DialogItemList.alertItem(
                    requireContext(),
                    object : DialogItemList.Listener {
                        override fun onClickItem(
                            name: String,
                            action: Int?,
                            id: Int?,
                            desc: String?,
                            uri: String?
                        ) {
                            var item: Item
                            var permanentFile = ""
                            if(uri!!.isNotEmpty()){
                                permanentFile =
                                    modelFlashLight.saveImagePermanently(requireContext(), uri.toUri()).toString()
                            }
                            modelFlashLight.insertItem(
                                Item(
                                    null,
                                    name,
                                    category = requireContext().getString(R.string.everyday),
                                    desc = desc,
                                    alarmTime = calendarDayB.timeInMillis,
                                    alarmText = permanentFile
                                )
                            )


                            if (action == ALARM) {
                                if (view.let {
                                        Const.isPermissionGranted(
                                            it.context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        )
                                    }) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        delay(500)
                                        item = db.CourseDao().getAllList().last()
                                        if (item.name == name) {
                                            withContext(Dispatchers.Main) {
                                                modelFlashLight.insertDateAndAlarm(
                                                    item,
                                                    calendarDayB,
                                                    requireContext()
                                                )
                                            }
                                        } else {
                                            delay(1000)
                                            item = db.CourseDao().getAllList().last()
                                            withContext(Dispatchers.Main) {
                                                modelFlashLight.insertDateAndAlarm(
                                                    item,
                                                    calendarDayB,
                                                    requireContext()
                                                )
                                            }
                                        }

                                    }

                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        pLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }


                            }
                        }
                    },
                    null,
                      model = modelFlashLight, lifecycleOwner = this,pickImageLauncher
                )
                else Toast.makeText(
                    requireContext(),
                    "Вы выбрали время которое уже прошло",
                    Toast.LENGTH_SHORT
                ).show()
            else Toast.makeText(
                requireContext(),
                "Отображение дел в календаре доступно в PREMIUM версии",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    override fun onResume() {
        super.onResume()
        calendarZero = Calendar.getInstance()
        if (modelFlashLight.getPremium()) {

            modelFlashLight.getListItemByCalendar(getDateNow(calendarDayB))

            modelFlashLight.listItemLDCalendar.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                if (it.isNotEmpty()) binding.tvDela.visibility = View.GONE
                else binding.tvDela.visibility = View.VISIBLE
            }
            db.CourseDao().getAll().asLiveData().observe(viewLifecycleOwner) { list ->
                val calendarDays = mutableListOf<CalendarDay>()
                modelFlashLight.getListItemByCalendar(getDateNow(calendarDayB))

                list.forEach { item ->
                    if (item.changeAlarm || !item.change) {
                        calendar = Calendar.getInstance()
                        calendar.timeInMillis = item.alarmTime
                        calendarDay = CalendarDay(calendar)
                        calendarDay.imageResource = R.drawable.ic_work
                        calendarDays.add(calendarDay)
                    }
                }
                binding.calendarView.setCalendarDays(calendarDays)
            }
            binding.calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
                override fun onClick(calendarDay: CalendarDay) {
                    calendarDayB = calendarDay.calendar
                    modelFlashLight.getListItemByCalendar(getDateNow(calendarDayB))

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
        modelFlashLight.insertStringAndAlarm(item, requireContext(), false)


    }

    override fun onClick(item: Item, action: Int) {
        when (action) {
            CHANGE -> {
                modelFlashLight.updateItem(item.copy(change = !item.change))
                if (item.changeAlarm) {
                    modelFlashLight.updateItem(
                        item.copy(
                            changeAlarm = false,
                            change = !item.change
                        )
                    )
                }
                modelFlashLight.changeAlarm(item, Const.DELETE_ALARM)

            } // Изменение состояния элемента(активный/неактивный)

            DELETE -> {
                if (item.change) {
                    modelFlashLight.deleteItem(item)
                    modelFlashLight.changeAlarm(item, Const.DELETE_ALARM)

                } else {
                    DialogItemList.AlertDelete(
                        requireContext(),
                        object : DialogItemList.ActionTrueOrFalse {
                            override fun onClick(flag: Boolean) {
                                if (flag) {
                                    modelFlashLight.deleteItem(item)
                                    modelFlashLight.changeAlarm(item, Const.DELETE_ALARM)
                                }
                            }
                        })

                }

            } // Удаления элемента

            ALARM -> {

                if (view?.let {
                        Const.isPermissionGranted(
                            it.context,
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    } == true) {

                    modelFlashLight.insertDateAndAlarm(item, null, requireContext())
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        pLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            } // Установка будильника

            CHANGE_ITEM -> {
                DialogItemList.alertItem(
                    requireContext(),
                    object : DialogItemList.Listener {
                        override fun onClickItem(
                            name: String,
                            action: Int?,
                            id: Int?,
                            desc: String?,
                            uri: String?
                        ) {
                            var permanentFile = uri
                            if (item.alarmText != uri) {
                                modelFlashLight.deleteSavedImage(item.alarmText.toUri())
                                permanentFile = modelFlashLight.saveImagePermanently(requireContext(), uri!!.toUri()).toString()

                            }
                            val newitem = item.copy(name = name, desc = desc, alarmText = permanentFile.toString())
                            if (item.changeAlarm) modelFlashLight.changeAlarm(
                                newitem,
                                newitem.interval
                            )

                            modelFlashLight.updateItem(newitem)   // если у item был установлен будильник то, тут мы перезаписываем будильник
                            if (action == ALARM) {
                                if (view.let {
                                        it?.let { it1 ->
                                            Const.isPermissionGranted(
                                                it1.context,
                                                Manifest.permission.POST_NOTIFICATIONS
                                            )
                                        } == true
                                    }) {
                                    modelFlashLight.insertDateAndAlarm(
                                        newitem,
                                        null,
                                        requireContext()
                                    )
                                }
                            } // это если из окна изменения нажал установка будильника

                        }
                    },
                    item,  model = modelFlashLight, lifecycleOwner = this,pickImageLauncher
                )

            } // Изменение имени элемента

            IMAGE -> {
                DialogItemList.showExpandedImage(item.alarmText, requireContext())
            } // Открытие картинки
        }

    }

    private fun getDateNow(calendar: Calendar): Long {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }


}


