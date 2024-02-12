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
        initDb(view.context)
        initRcView()
        db.CourseDao().getAll().asLiveData().observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

        val launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val text = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    Thread {
                        if (text != null) {
                            db.CourseDao().insertAll(Item(null, text[0]))
                        }
                    }.start()

                }

            }

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
                launcher.launch(initVoiceIntent())
            } catch (e: Exception) {
                Toast.makeText(
                    view.context,
                    "Голосовой ввод пока недоступен для вашего устройства",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    }


    private fun initVoiceIntent(): Intent {
        val voiceIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        voiceIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        return voiceIntent
    } // Функция для создания интента голосового ввода

    private fun initRcView() {
        val rcView = binding.rcView
        adapter = ItemListAdapter(this, this)
        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = adapter

    } // инициализировал ресайклер

    private fun initDb(context: Context) {
        db = Room.databaseBuilder(
            context,
            GfgDatabase::class.java, "db"
        ).build()
    } // инициализировал БД

    private fun deleteAlertDialog(context: Context, item: Item) {
        DialogItemList.AlertDelete(context, object : DialogItemList.Delete {
            override fun onClick(flag: Boolean) {
                if (flag) {
                    Thread {
                        changeAlarmItem(item, Const.deleteAlarmRepeat)
                        db.CourseDao().delete(item)
                    }.start()

                }
            }
        })
    } // Подтверждение на удаление

    private fun changeAlarmItem(item: Item, action: Int) {
        modelFlashLight.alarmInsert(
            item,
            item.alarmTime,
            requireContext(),
            alarmManager,
            action
        )

    } // Удаление заметки

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
    } // Установрка даты

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
                                proverkaFree(item, result)
                            }
                            else Toast.makeText(
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

    override fun onLongClick(item: Item) {
        Thread {
            db.CourseDao().update(item.copy(changeAlarm = !item.changeAlarm))
        }.start()
        if (item.changeAlarm) {
            changeAlarmItem(item, Const.deleteAlarmRepeat)
        }
        if (!item.changeAlarm && item.alarmTime > calendarZero.timeInMillis) {
            changeAlarmItem(item, item.interval)
        }
        if (!item.changeAlarm && item.alarmTime < calendarZero.timeInMillis) {
            when (item.interval) {
                Const.alarmDay -> {
                    insertAlarmRepeat(item, AlarmManager.INTERVAL_DAY, "и через день")
                }
                Const.alarmWeek -> {
                    insertAlarmRepeat(item, AlarmManager.INTERVAL_DAY * 7, "и через неделю")
                }
                Const.alarmMonth -> {
                    insertAlarmRepeat(item, Const.MONTH, "и через месяц")
                }
            }
        }


    }

    override fun onClick(item: Item, action: Int) {

        view?.let { modelFlashLight.turnVibro(it.context, 100) }
        when (action) {
            Const.change -> {
                Thread {
                    db.CourseDao().update(item.copy(change = !item.change))
                    if (item.changeAlarm) {
                        db.CourseDao().update(item.copy(changeAlarm = false, change = !item.change))
                    }
                    changeAlarmItem(item, Const.deleteAlarmRepeat)
                }.start()
            } // Изменение состояния элемента(активный/неактивный)

            Const.delete -> {
                if (item.change) {
                    Thread {
                        db.CourseDao().delete(item)
                    }.start()
                    changeAlarmItem(item, Const.deleteAlarmRepeat)
                } else {
                    deleteAlertDialog(requireContext(), item)
                }

            } // Удаления элемента

            Const.alarm -> {
                calendar = Calendar.getInstance()
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
            } // Установка будильника

            Const.changeItem -> {
                DialogItemList.AlertList(requireContext(), object : DialogItemList.Listener {
                    override fun onClick(name: String) {
                        Thread {
                            val newitem = item.copy(name = name)
                            if (item.changeAlarm) changeAlarmItem(newitem, newitem.interval)
                            db.CourseDao().update(newitem)
                        }.start()
                    }
                }, item.name)
            } // Изменение имени элемента
        }

    }

    private fun insertAlarm(item: Item, result: Int, intervalText: String) {
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
        changeAlarmItem(newitem, result)

    } // установка будильника

    private fun insertAlarmRepeat(item: Item, intervalTime: Long, intervalString: String) {
        val time = item.alarmTime + intervalTime
        val dateFormat = "dd.MM"
        val timeFormat = "HH:mm"
        val dateFormate = SimpleDateFormat(dateFormat, Locale.US)
        val timeFormate = SimpleDateFormat(timeFormat, Locale.US)
        val resultDate = dateFormate.format(time)
        val resutTime = timeFormate.format(time)
        val result = "Напомнит: $resultDate в $resutTime $intervalString"
        val newItem = item.copy(alarmTime = time, alarmText = result,changeAlarm = !item.changeAlarm)
        Thread {
            db.CourseDao().update(newItem)
        }.start()
        changeAlarmItem(newItem, newItem.interval)

    } // установка повторяющегося будильника
    private fun proverkaFree(item: Item, result: Int){
        if (result == Const.alarmOne) insertAlarm(item, result, "")
        else if(Const.premium){
            when(result){
                Const.alarmDay -> {
                    insertAlarm(item, result, "и через день")
                }

                Const.alarmWeek -> {
                    insertAlarm(item, result, "и через неделю")
                }

                Const.alarmMonth -> {
                    insertAlarm(item, result, "и через месяц")
                }


            }
        }else Toast.makeText(view?.context, "Повторяющиеся напоминания доступны в PREMIUM версии", Toast.LENGTH_SHORT).show()

    }


    companion object {
        fun newInstance() = FragmentList()
    }
}

