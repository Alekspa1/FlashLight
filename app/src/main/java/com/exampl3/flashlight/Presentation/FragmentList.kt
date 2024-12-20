package com.exampl3.flashlight.Presentation


import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
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
import com.exampl3.flashlight.Domain.Room.GfgDatabase
import com.exampl3.flashlight.Domain.Room.Item
import com.exampl3.flashlight.databinding.FragmentListBinding
import com.exampl3.flashlight.Domain.model.InsertTime
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
    lateinit var db: GfgDatabase
    @Inject
    lateinit var voiceIntent: Intent
    @Inject
    lateinit var insertTime: InsertTime
    private val modelFlashLight: ViewModelFlashLight by activityViewModels()
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var timePickerDialog: TimePickerDialog
    private lateinit var datePickerDialog: DatePickerDialog
    lateinit var calendar: Calendar
    private lateinit var calendarZero: Calendar

    private var listDel = mutableListOf<Item>()

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

        modelFlashLight.edit.observe(viewLifecycleOwner){
            if (it) binding.imageView.visibility = View.VISIBLE
            else binding.imageView.visibility = View.GONE
        }

        modelFlashLight.categoryItemLD.observe(viewLifecycleOwner){value->
            binding.tvCategory.text = value

        }

        modelFlashLight.categoryItemLDNew.observe(viewLifecycleOwner){list->

//            adapter.submitList(list.sortedWith { o1, o2 ->
//                o2.changeAlarm.compareTo(true) - o1.changeAlarm.compareTo(
//                    true
//                )
//            }.sortedWith { o1, o2 ->
//                o1.change.compareTo(true) - o2.change.compareTo(
//                    true
//                )
//            })


            adapter.submitList(list.sortedBy { it.id }.reversed().sortedBy { it.alarmTime }
                .reversed().sortedBy { it.change }
                .reversed().sortedBy { it.changeAlarm }
                .reversed())


        }

        db.CourseDao().getAll().asLiveData().observe(viewLifecycleOwner){
            modelFlashLight.categoryItemLD.value?.let { it1 -> modelFlashLight.updateCategory(it1)
            }
        }
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

        val launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val text = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    CoroutineScope(Dispatchers.IO).launch {
                        if (text != null) {
                            db.CourseDao().insertAll(Item(null, text[0], category = modelFlashLight.categoryItemLD.value!!))
                        }
                    }

                }

            }

//        binding.imBAddFrag.setOnClickListener {
//            DialogItemList.AlertList(requireContext(), object : DialogItemList.Listener {
//                override fun onClick(name: String) {
//                    CoroutineScope(Dispatchers.IO).launch {
//                        db.CourseDao().insertAll(Item(null, name, category = modelFlashLight.categoryItemLD.value!!))
//                    }
//                }
//            }, null)
//
//        }
        binding.imBAddFrag.setOnClickListener {
            DialogItemList.alertItem(requireContext(), object : DialogItemList.Listener {
                override fun onClickItem(name: String, action: Int?, id: Int?, desc: String?) {
                    CoroutineScope(Dispatchers.IO).launch {
                        db.CourseDao().insertAll(Item(null, name, category = modelFlashLight.categoryItemLD.value!!, desc = desc))

                    }
                    if (action == Const.alarm) {
                        if (view.let {
                                Const.isPermissionGranted(
                                    it.context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            }) {
                            CoroutineScope(Dispatchers.IO).launch {
                                delay(500)
                                val item = if(id == null)db.CourseDao().getAllList().last()
                                else db.CourseDao().getItemId(id)
                                withContext(Dispatchers.Main){
                                        datePickerDialog(item)
                                    }
                                }

                            }


                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                pLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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

        binding.imageView.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                deleteAlarmAll(listDel)
                db.CourseDao().deleteList(listDel)
                listDel.removeAll(listDel)
            }
            modelFlashLight.edit.value = false
        }

    }
    private suspend fun deleteAlarmAll(list: List<Item>) = withContext(Dispatchers.IO){
        list.forEach { item ->
            insertTime.changeAlarmItem(item, Const.deleteAlarm)
        }
    }

    private fun initRcView() {
        val rcView = binding.rcView
        adapter = ItemListAdapter(this, this)
        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = adapter

    } // инициализировал ресайклер
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
                            insertAlarm(item, item.interval, "и через день", item.alarmTime+AlarmManager.INTERVAL_DAY)
                        }
                        Const.alarmWeek -> {
                            insertTime.
                            insertAlarm(item,item.interval, "и через неделю", item.alarmTime+AlarmManager.INTERVAL_DAY * 7)
                        }
                        Const.alarmMonth -> {
                            insertTime.
                            insertAlarm(item,item.interval, "и через месяц", item.alarmTime+ Const.MONTH)
                        }
                        Const.alarmYear-> {
                            val calendarNextYear = Calendar.getInstance()
                            calendarNextYear.set(calendarNextYear.get(Calendar.YEAR)+1,Calendar.JANUARY,1)
                            val nowYear = calendarZero.getActualMaximum(Calendar.DAY_OF_YEAR)
                            val nextYear = calendarNextYear.getActualMaximum(Calendar.DAY_OF_YEAR)
                            var year:Long
                            if (nowYear == 366) {
                                year = if (item.alarmTime <  february()) AlarmManager.INTERVAL_DAY * 366
                                else AlarmManager.INTERVAL_DAY * 365
                                insertTime.insertAlarm(item,item.interval,"и через год", item.alarmTime+ year)
                            } else {
                                year = AlarmManager.INTERVAL_DAY * 365
                                insertTime.insertAlarm(item,item.interval,"и через год", item.alarmTime+ year)
                            }

                            if (nextYear == 366) {
                                year = if (item.alarmTime >  february()) AlarmManager.INTERVAL_DAY * 366
                                else AlarmManager.INTERVAL_DAY * 365
                                insertTime.insertAlarm(item,item.interval,"и через год", item.alarmTime+ year)
                            }

                        }

                    }
                }
            }
            Const.delete -> {
                modelFlashLight.edit.value = true
                val newItem = item.copy(changeDelItem = !item.changeDelItem)
                CoroutineScope(Dispatchers.IO).launch {
                    db.CourseDao().update(newItem)
                }
                when(item.changeDelItem){
                    true -> listDel.remove(item)
                    false -> listDel.add(newItem)
                }
            }
        }

    }
    override fun onClick(item: Item, action: Int) {
        if (listDel.isEmpty()) {
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

//                Const.changeItem -> {
//                        DialogItemList.AlertList(
//                            requireContext(),
//                            object : DialogItemList.Listener {
//                                override fun onClickItem(name: String, action: Int?, id: Int?, desc: String?) {
//                                    CoroutineScope(Dispatchers.IO).launch {
//                                        val newitem = item.copy(name = name)
//                                        if (item.changeAlarm) insertTime.changeAlarmItem(
//                                            newitem,
//                                            newitem.interval
//                                        ) // если у item был установлен будильник то, тут мы перезаписываем будильник
//                                        db.CourseDao().update(newitem)
//                                    }
//                                }
//                            },
//                            item.name
//                        )
//
//                } // Изменение имени элемента
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
            modelFlashLight.edit.value = false
        }  else {
            val newItem = item.copy(changeDelItem = !item.changeDelItem)
            CoroutineScope(Dispatchers.IO).launch {
                db.CourseDao().update(newItem)
            }
            when (item.changeDelItem) {
                true -> listDel.remove(item)
                false -> listDel.add(newItem)
            }
            if (listDel.isEmpty()) modelFlashLight.edit.value = false

        }
    }
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
//    private fun proverkaFree(item: Item, result: Int, timeCal: Long) {
//        if (result == Const.alarmOne ) insertTime.insertAlarm(item, result, "", timeCal)
//        else if (modelFlashLight.getPremium()) {
//            when (result) {
//
//                Const.alarmDay -> {
//                    insertTime.insertAlarm(item, result, "и через день", timeCal)
//
//                }
//
//                Const.alarmWeek -> {
//                    insertTime.insertAlarm(item, result, "и через неделю", timeCal)
//                }
//                Const.alarmMonth -> {
//                    insertTime.insertAlarm(item, result, "и через месяц", timeCal)
//                }
//            }
//        }
//        else
//            Toast.makeText(view?.context, "Повторяющиеся напоминания доступны в PREMIUM версии", Toast.LENGTH_SHORT).show()
//
//
//    } ЭТОТ КОНЕЧНЫЙ



    override fun onResume() {
        super.onResume()
        calendarZero = Calendar.getInstance()
        CoroutineScope(Dispatchers.IO).launch {
            db.CourseDao().getAllList().forEach {
                if (it.changeDelItem) {
                    db.CourseDao().update(it.copy(changeDelItem = false))
                    listDel.remove(it)
                }
            }
        }
        modelFlashLight.edit.value = false
    }
    private fun advertising(){
        (activity as MainActivity).showAd()
    } // запуск рекламы
    private fun february(): Long{
        calendarZero.set(Calendar.YEAR, calendarZero.get(Calendar.YEAR))
        calendarZero.set(Calendar.MONTH, Calendar.FEBRUARY)
        calendarZero.set(Calendar.DAY_OF_MONTH, 29)
        return calendarZero.timeInMillis
    } // дней в феврале


    companion object {
        fun newInstance() = FragmentList()
    }
}

