package com.exampl3.flashlight.Presentation


import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.exampl3.flashlight.Data.Const
import com.exampl3.flashlight.Domain.Adapter.ItemListAdapter
import com.exampl3.flashlight.Domain.Room.GfgDatabase
import com.exampl3.flashlight.Domain.Room.Item
import com.exampl3.flashlight.databinding.FragmentListBinding

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class FragmentList : Fragment(), ItemListAdapter.onLongClick, ItemListAdapter.onClick {
    private lateinit var binding: FragmentListBinding
    private lateinit var adapter: ItemListAdapter
    private lateinit var db: GfgDatabase
    private lateinit var modelFlashLight: ViewModelFlashLight
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var timePickerDialog: TimePickerDialog
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var alarmManager: AlarmManager
    private lateinit var calendar: Calendar
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
        calendarZero = Calendar.getInstance()
        modelFlashLight = ViewModelFlashLight()
        alarmManager = view.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val text = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    Thread {
                        if (text != null) {
                            db.CourseDao().insertAll(Item(null, text[0]))
                        }
                    }.start()

                }

            }
        val voiceIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        voiceIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )


        initDb(view.context)
        initRcView()
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
        binding.imButton.setOnClickListener {
            DialogItemList.AlertList(requireContext(), object : DialogItemList.Listener {
                override fun onClick(name: String) {
                    Thread {
                        db.CourseDao().insertAll(Item(null, name))
                    }.start()
                }
            }, null)

        }
        binding.imageView.setOnClickListener {
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


    override fun onResume() {
        super.onResume()
        updateRcView()
    }

    private fun initRcView() {
        adapter = ItemListAdapter(this, this)
        val rcView = binding.rcView
        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = adapter

    } // инициализировал ресайклер

    private fun initDb(context: Context) {
        db = Room.databaseBuilder(
            context,
            GfgDatabase::class.java, "db"
        ).build()
    } // инициализировал БД

    private fun updateRcView() {
        db.CourseDao().getAll().asLiveData().observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    } // Обновил список в ресайклере

    private fun delete(context: Context, item: Item) {
        DialogItemList.AlertDelete(context, object : DialogItemList.Delete {
            override fun onClick(flag: Boolean) {
                if (flag) {
                    Thread {
                        modelFlashLight.alarmInsert(
                            item,
                            item.alarmTime,
                            requireContext(),
                            alarmManager,
                            Const.deleteAlarmRepeat
                        )
                        db.CourseDao().delete(item)
                    }.start()

                }
            }
        })
    } // удаляю заметки

    private fun timePickerDialog(item: Item) {
        timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                DialogItemList.insertAlarm(
                    requireView().context,
                    object : DialogItemList.InsertAlarm {
                        override fun onClick(result: Int) {
                            if (calendar.timeInMillis >= calendarZero.timeInMillis) {
                                when (result) {
                                    Const.alarmOne -> {
                                        insertAlarm(item,result,"")
                                    }
                                    Const.alarmDay -> {
                                        insertAlarm(item,result,"и через день")
                                    }
                                    Const.alarmWeek -> {
                                        insertAlarm(item,result,"и через неделю")
                                    }
                                    Const.alarmMonth -> {
                                        insertAlarm(item,result,"и через месяц")
                                    }
                                }
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
    }

    private fun datePickerDialog(item: Item) {

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
    }

    override fun onLongClick(item: Item) {
        Thread {
            db.CourseDao().update(item.copy(changeAlarm = !item.changeAlarm))
        }.start()
        if (item.changeAlarm) {
            modelFlashLight.alarmInsert(
                item,
                item.alarmTime,
                requireContext(),
                alarmManager,
                Const.deleteAlarmRepeat
            )
        }
        if (!item.changeAlarm && item.alarmTime > calendarZero.timeInMillis) {
            modelFlashLight.alarmInsert(
                item,
                item.alarmTime,
                requireContext(),
                alarmManager,
                item.interval
            )
        }
    }

    override fun onClick(item: Item, action: Int) {
        when (action) {
            Const.change -> {
                Thread {
                    view?.let { modelFlashLight.turnVibro(it.context, 100) }
                    db.CourseDao().update(item.copy(change = !item.change))
                    if (item.changeAlarm) {
                        db.CourseDao().update(item.copy(changeAlarm = false, change = !item.change))
                    }
                    modelFlashLight.alarmInsert(
                        item,
                        item.alarmTime,
                        requireContext(),
                        alarmManager,
                        Const.deleteAlarmRepeat
                    )
                }.start()
            }

            Const.delete -> {
                view?.let { modelFlashLight.turnVibro(it.context, 100) }
                if (item.change) {
                    Thread {
                        db.CourseDao().delete(item)
                    }.start()
                    modelFlashLight.alarmInsert(
                        item,
                        item.alarmTime,
                        requireContext(),
                        alarmManager,
                        Const.deleteAlarmRepeat
                    )
                } else {
                    delete(requireContext(), item)
                }

            }

            Const.alarm -> {
                calendar = Calendar.getInstance()
                view?.let { modelFlashLight.turnVibro(it.context, 100) }
                if (view?.let {
                        Const.isPermissionGranted(
                            it.context,
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    } == true) {
                    datePickerDialog(item)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        pLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            Const.changeItem -> {
                DialogItemList.AlertList(requireContext(), object : DialogItemList.Listener {
                    override fun onClick(name: String) {
                        Thread {
                            val newitem = item.copy(name = name)
                            if (item.changeAlarm) modelFlashLight.alarmInsert(
                                newitem,
                                newitem.alarmTime,
                                requireContext(),
                                alarmManager,
                                item.interval
                            )
                            db.CourseDao().update(newitem)
                        }.start()
                    }
                }, item.name)
            }
        }

    }
    private fun insertAlarm(item: Item, result: Int,intervalText: String){
        val dateFormat = "dd.MM"
        val timeFormat = "HH:mm"
        val date = SimpleDateFormat(dateFormat, Locale.US)
        val time = SimpleDateFormat(timeFormat, Locale.US)
        val resultDate = date.format(calendar.time)
        val resutTime = time.format(calendar.time)
        val newAlarmText = "Напомнит: $resultDate в $resutTime"
        val newitem = item.copy(
            changeAlarm = true,
            alarmText = "$newAlarmText $intervalText",
            alarmTime = calendar.timeInMillis,
            change = false,
            name = item.name,
            interval = result
        )
        Thread {
            db.CourseDao().update(newitem)
        }.start()
        modelFlashLight.alarmInsert(
            newitem,
            calendar.timeInMillis,
            requireView().context,
            alarmManager, result
        )

    } // установка будильника



    companion object {
        fun newInstance() = FragmentList()
    }
}

