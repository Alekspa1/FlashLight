package com.exampl3.flashlight.Presentation


import android.Manifest
import android.app.Activity
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
import com.exampl3.flashlight.Const.ALARM
import com.exampl3.flashlight.Const.CHANGE
import com.exampl3.flashlight.Const.CHANGE_ITEM
import com.exampl3.flashlight.Const.DELETE
import com.exampl3.flashlight.Presentation.adapters.ItemListAdapter
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.databinding.FragmentListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
open class FragmentList : Fragment(), ItemListAdapter.onClick, ItemListAdapter.onLongClick {
    private lateinit var binding: FragmentListBinding
    private lateinit var adapter: ItemListAdapter

    @Inject
    lateinit var db: Database

    @Inject
    lateinit var voiceIntent: Intent
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
                        modelFlashLight.insertItem(
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
                    modelFlashLight.insertItem(
                        Item(
                            null,
                            name,
                            category = modelFlashLight.categoryItemLD.value!!,
                            desc = desc,
                            alarmTime = 0
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
                                            null,
                                            requireContext()
                                        )
                                    }
                                } else {
                                    delay(1000)
                                    item = db.CourseDao().getAllList().last()
                                    withContext(Dispatchers.Main) {
                                        modelFlashLight.insertDateAndAlarm(
                                            item,
                                            null,
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
                            desc: String?
                        ) {
                            val newitem = item.copy(name = name, desc = desc)
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
                    item.name, item.id, item.desc
                )

            } // Изменение имени элемента
        }

    }

    override fun onResume() {
        super.onResume()
        calendarZero = Calendar.getInstance()
    }


    companion object {
        fun newInstance() = FragmentList()
    }
}

