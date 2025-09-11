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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.ALARM
import com.exampl3.flashlight.Const.THEME_ZABOR
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Data.ThemeImp
import com.exampl3.flashlight.Data.sharedPreference.SettingsSharedPreference
import com.exampl3.flashlight.Domain.ItemClickHandler

import com.exampl3.flashlight.Presentation.adapters.ItemListAdapter
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.FragmentCalendarBinding
import com.exampl3.flashlight.databinding.FragmentCalendarZaborBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import kotlin.Int
import kotlin.collections.Map


@AndroidEntryPoint
class FragmentCalendar : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var bindingZabor: FragmentCalendarZaborBinding
    private val modelFlashLight: ViewModelFlashLight by activityViewModels()

    @Inject
    lateinit var db: Database
    @Inject
    lateinit var pref: SettingsSharedPreference
    @Inject
    lateinit var themeImp: ThemeImp

    private lateinit var calendar: Calendar
    private lateinit var calendarZero: Calendar
    private lateinit var calendarDay: CalendarDay
    private lateinit var calendarDayB: Calendar
    private lateinit var adapter: ItemListAdapter
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var itemClickHandler : ItemClickHandler
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
        if (pref.getTheme() == THEME_ZABOR ) {
            bindingZabor = FragmentCalendarZaborBinding.inflate(inflater, container, false)
            theme()
            return bindingZabor.root
        }
        else binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
        itemClickHandler = ItemClickHandler(
            context = requireContext(),
            modelFlashLight = modelFlashLight,
            lifecycleOwner = viewLifecycleOwner,
            pickImageLauncher = pickImageLauncher,
            pLauncher = pLauncher
        )
        initRcView()
        calendarDayB = Calendar.getInstance()

        if (pref.getTheme() == THEME_ZABOR){
            bindingZabor.imBAddCalendar.setOnClickListener {
                modelFlashLight.getItemMaxSort()
                if (modelFlashLight.getPremium())
                    if (getDateNow(calendarDayB) >= getDateNow(calendarZero)) DialogItemList.alertItem(
                        requireContext(),
                        object : DialogItemList.Listener {
                            override fun onClickItem(
                                name: String,
                                action: Int?,
                                id: Int?,
                                desc: String?,
                                uri: String?,
                                category: String?
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
                                        category = category.toString(),
                                        desc = desc,
                                        alarmTime = calendarDayB.timeInMillis,
                                        alarmText = permanentFile,
                                        sort = modelFlashLight.maxSorted.value?:0
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
                        model = modelFlashLight, lifecycleOwner = this,pickImageLauncher,true
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
        else {
            binding.imBAddCalendar.setOnClickListener {
                modelFlashLight.getItemMaxSort()
                if (modelFlashLight.getPremium())
                    if (getDateNow(calendarDayB) >= getDateNow(calendarZero)) DialogItemList.alertItem(
                        requireContext(),
                        object : DialogItemList.Listener {
                            override fun onClickItem(
                                name: String,
                                action: Int?,
                                id: Int?,
                                desc: String?,
                                uri: String?,
                                category: String?
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
                                        category = category.toString(),
                                        desc = desc,
                                        alarmTime = calendarDayB.timeInMillis,
                                        alarmText = permanentFile,
                                        sort = modelFlashLight.maxSorted.value?:0
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
                        model = modelFlashLight, lifecycleOwner = this,pickImageLauncher,true
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



    }

    override fun onResume() {
        super.onResume()
        if (pref.getTheme() == THEME_ZABOR) {
            calendarZero = Calendar.getInstance()
            if (modelFlashLight.getPremium()) {

                modelFlashLight.getListItemByCalendar(getDateNow(calendarDayB))

                modelFlashLight.listItemLDCalendar.observe(viewLifecycleOwner) {
                    scrollInStartAdapter()
                    adapter.submitList(it)
                    if (it.isNotEmpty()) bindingZabor.tvDela.visibility = View.GONE
                    else bindingZabor.tvDela.visibility = View.VISIBLE
                }
                db.CourseDao().getAll().observe(viewLifecycleOwner) { list ->
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
                    bindingZabor.calendarView.setCalendarDays(calendarDays)
                }
                bindingZabor.calendarView.setOnCalendarDayClickListener(object :
                    OnCalendarDayClickListener {
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
        else {
            calendarZero = Calendar.getInstance()
            if (modelFlashLight.getPremium()) {

                modelFlashLight.getListItemByCalendar(getDateNow(calendarDayB))

                modelFlashLight.listItemLDCalendar.observe(viewLifecycleOwner) {
                    scrollInStartAdapter()
                    adapter.submitList(it)
                    if (it.isNotEmpty()) binding.tvDela.visibility = View.GONE
                    else binding.tvDela.visibility = View.VISIBLE
                }
                db.CourseDao().getAll().observe(viewLifecycleOwner) { list ->
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
                binding.calendarView.setOnCalendarDayClickListener(object :
                    OnCalendarDayClickListener {
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
    }

    private fun initRcView() {
        if (pref.getTheme() == THEME_ZABOR){
            val rcView = bindingZabor.rcViewItem

            adapter = ItemListAdapter(
                onClickListener = itemClickHandler,
                onOrderChanged = null,
                touchHelper = null,
                pref,
                themeImp
            )
            rcView.layoutManager = LinearLayoutManager(requireContext())
            rcView.adapter = adapter
        }
        else {
            val rcView = binding.rcViewItem

            adapter = ItemListAdapter(
                onClickListener = itemClickHandler,
                onOrderChanged = null,
                touchHelper = null,
                pref,
                themeImp
            )
            rcView.layoutManager = LinearLayoutManager(requireContext())
            rcView.adapter = adapter

        }


    } // инициализировал ресайклер
    private fun scrollInStartAdapter(){
        if (pref.getTheme() == THEME_ZABOR){
            adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    if (positionStart == 0) {  // Элементы добавились в начало (верх списка)
                        bindingZabor.rcViewItem.scrollToPosition(0)
                        adapter.unregisterAdapterDataObserver(this)
                    }
                }
            })
        }
        else{adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {  // Элементы добавились в начало (верх списка)
                    binding.rcViewItem.scrollToPosition(0)
                    adapter.unregisterAdapterDataObserver(this)
                }
            }
        })}

    }




    private fun getDateNow(calendar: Calendar): Long {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun theme(){
        with(modelFlashLight){
            with(bindingZabor) {
                val list = mapOf<Const.Action, Map<View, Int>>(
                    Const.Action.IMAGE_RESOURCE to mapOf(
                        imBAddCalendar to R.drawable.ic_add_zabor
                    ),
                    Const.Action.TEXT_COLOR to mapOf(tvDela to R.color.black )
                )



                    modelFlashLight.setView(list)
                    setSize(list)

            }
        }

    }


}


