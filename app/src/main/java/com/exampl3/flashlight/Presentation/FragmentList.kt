package com.exampl3.flashlight.Presentation


import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Presentation.adapters.ItemListAdapter
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.databinding.FragmentListBinding
import com.exampl3.flashlight.Domain.InsertAlarm
import com.exampl3.flashlight.Domain.InsertTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
open class FragmentList : Fragment(), ItemListAdapter.onLongClick, ItemListAdapter.onClick {
    private lateinit var binding: FragmentListBinding
    private lateinit var adapter: ItemListAdapter

    @Inject
    lateinit var db: Database

    @Inject
    lateinit var voiceIntent: Intent

    @Inject
    lateinit var insertAlarm: InsertAlarm

    @Inject
    lateinit var insertTime: InsertTime
    private val modelFlashLight: ViewModelFlashLight by activityViewModels()
    private lateinit var pLauncher: ActivityResultLauncher<String>


    private lateinit var calendarZero: Calendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()

        modelFlashLight.categoryItemLD.observe(viewLifecycleOwner) { value ->
            binding.tvCategory.text = value

        }

        modelFlashLight.listItemLD.observe(viewLifecycleOwner) { list ->

            adapter.submitList(list.sortedBy { it.id }.reversed().sortedBy { it.alarmTime }
                .reversed().sortedBy { it.change }
                .reversed().sortedBy { it.changeAlarm }
                .reversed())


        }

        db.CourseDao().getAll().asLiveData().observe(viewLifecycleOwner) {
            modelFlashLight.categoryItemLD.value?.let { it1 ->
                modelFlashLight.updateCategory(it1)
            }
        }
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

        val launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val text = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    if (text != null) {
                        modelFlashLight.insertAlarm(
                            Item(
                                null,
                                text[0],
                                category = modelFlashLight.categoryItemLD.value!!
                            )
                        )

                    }


                }

            }

        binding.imBAddFrag.setOnClickListener {
            DialogItemList.alertItem(requireContext(), object : DialogItemList.Listener {
                override fun onClickItem(name: String, action: Int?, id: Int?, desc: String?) {
                    var item: Item
                    modelFlashLight.insertAlarm(
                        Item(
                            null,
                            name,
                            category = modelFlashLight.categoryItemLD.value!!,
                            desc = desc
                        )
                    )


                    if (action == Const.alarm) {
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
                                        insertTime.datePickerDialog(requireContext(), item)
                                    }
                                } else {
                                    delay(1000)
                                    item = db.CourseDao().getAllList().last()
                                    withContext(Dispatchers.Main) {
                                        insertTime.datePickerDialog(requireContext(), item)
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
            }, null, null, null)

        }

        binding.imVoiceFrag.setOnClickListener {
            try {
                launcher.launch(voiceIntent)
            } catch (e: Exception) {

                Toast.makeText(
                    view.context,
                    "Голосовой ввод пока недоступен для вашего устройства",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    }

    private fun initRcView() {
        val rcView = binding.rcView
        adapter = ItemListAdapter(this, this)
        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = adapter

    } // инициализировал ресайклер

    override fun onLongClick(item: Item, action: Int) {

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

                Const.alarmYear -> {
                    insertAlarm.insertAlarm(
                        item,
                        item.interval,
                        "и через год",
                        addOneYear(item.alarmTime)
                    )
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

                if (view?.let {
                        Const.isPermissionGranted(
                            it.context,
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    } == true) {
                    insertTime.datePickerDialog(requireContext(), item)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        pLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
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

    override fun onResume() {
        super.onResume()
        calendarZero = Calendar.getInstance()
    }

    private fun addOneYear(dateInMillis: Long): Long {
        calendarZero.timeInMillis = dateInMillis
        calendarZero.add(Calendar.YEAR, 1) // Добавляем один год
        return calendarZero.timeInMillis
    }


    companion object {
        fun newInstance() = FragmentList()
    }
}

