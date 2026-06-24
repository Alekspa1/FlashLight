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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.ALARM
import com.exampl3.flashlight.Const.THEME_ZABOR
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.ThemeImp
import com.exampl3.flashlight.Data.sharedPreference.SettingsSharedPreference
import com.exampl3.flashlight.Domain.ItemClickHandler
import com.exampl3.flashlight.Domain.LogText
import com.exampl3.flashlight.Domain.useCase.PermissionUseCase
import com.exampl3.flashlight.Presentation.adapters.SimpleItem
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.FragmentCalendarBinding
import com.exampl3.flashlight.databinding.FragmentCalendarZaborBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject


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

    @Inject
    lateinit var permissionUseCase: PermissionUseCase

    private lateinit var calendar: Calendar
    private lateinit var calendarZero: Calendar
    private lateinit var calendarDay: CalendarDay
    private lateinit var calendarDayB: Calendar
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var itemClickHandler: ItemClickHandler
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
        if (pref.getTheme() == THEME_ZABOR) {
            bindingZabor = FragmentCalendarZaborBinding.inflate(inflater, container, false)
            theme()
            return bindingZabor.root
        } else binding = FragmentCalendarBinding.inflate(inflater, container, false)
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
            pLauncher = pLauncher,
            )
        calendar = Calendar.getInstance()
        calendarDayB = Calendar.getInstance()
        initRcView()

         if (pref.getTheme() == THEME_ZABOR) {
            bindingZabor.imBAddCalendar.setOnClickListener {
                if (modelFlashLight.getPremium())
                    if (modelFlashLight.getDateNow(calendarDayB) >= modelFlashLight.getDateNow(calendarZero)) DialogItemList.alertItem(
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
                                // 1. Подготавливаем картинку
                                var permanentFile = ""
                                if (uri != null && uri.isNotEmpty()) {
                                    permanentFile = modelFlashLight.saveImagePermanently(
                                        requireContext(),
                                        uri.toUri()
                                    ).toString()
                                }

                                // 2. Проверяем разрешение на уведомления
                                val hasPermission = Const.isPermissionGranted(
                                    requireContext(),
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                                val isAlarm = (action == ALARM)

                                // 3. Просто отдаем всё во ViewModel! Она сделает всё сама, без фризов и задержек
                                modelFlashLight.insertItem(
                                    name = name,
                                    category = category.toString(),
                                    desc = desc,
                                    alarmText = permanentFile,
                                    hasAlarmPermission = hasPermission,
                                    isAlarmAction = isAlarm,
                                    context = requireContext(),
                                    calendarDay = calendarDayB,
                                )

                                // 4. Если пользователь хотел будильник, но разрешения нет — показываем системный запрос
                                if (isAlarm && !hasPermission) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        pLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                            }
                        },
                        null,
                        model = modelFlashLight,
                        lifecycleOwner = this,
                        pick = pickImageLauncher,
                        true
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
             createAlarmInCalendarnZabor()
        }
         else {
            binding.imBAddCalendar.setOnClickListener {

                if (modelFlashLight.getPremium())
                    if (modelFlashLight.getDateNow(calendarDayB) >= modelFlashLight.getDateNow(calendarZero)) DialogItemList.alertItem(
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
                                // 1. Подготавливаем картинку
                                var permanentFile = ""
                                if (uri != null && uri.isNotEmpty()) {
                                    permanentFile = modelFlashLight.saveImagePermanently(
                                        requireContext(),
                                        uri.toUri()
                                    ).toString()
                                }

                                // 2. Проверяем разрешение на уведомления
                                val hasPermission = Const.isPermissionGranted(
                                    requireContext(),
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                                val isAlarm = (action == ALARM)

                                // 3. Просто отдаем всё во ViewModel! Она сделает всё сама, без фризов и задержек
                                modelFlashLight.insertItem(
                                    name = name,
                                    category = category.toString(),
                                    desc = desc,
                                    alarmText = permanentFile,
                                    hasAlarmPermission = hasPermission,
                                    isAlarmAction = isAlarm,
                                    context = requireContext(),
                                    calendarDay = calendarDayB,
                                )

                                // 4. Если пользователь хотел будильник, но разрешения нет — показываем системный запрос
                                if (isAlarm && !hasPermission) {
                                    pLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                        },
                        null,
                        model = modelFlashLight,
                        lifecycleOwner = this,
                        pick = pickImageLauncher,
                        true
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
             createAlarmInCalendarnNeon()
            
        }


    }
    private fun createAlarmInCalendarnNeon() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                modelFlashLight.getItemCalendarCombine.collect { listItems->
                    val calendarDays = mutableListOf<CalendarDay>()
                    listItems.forEach { item->
                            calendar.timeInMillis = item.alarmTime
                            val clonedCalendar = calendar.clone() as Calendar
                            calendarDay = CalendarDay(clonedCalendar)
                            calendarDay.imageResource = R.drawable.ic_work
                            calendarDays.add(calendarDay)
                    }
                    binding.calendarView.setCalendarDays(calendarDays)
                }


            }
        }



                binding.calendarView.setOnCalendarDayClickListener(object :
                    OnCalendarDayClickListener {
                    override fun onClick(calendarDay: CalendarDay) {
                        calendarDayB = calendarDay.calendar
                        modelFlashLight.insetTimeIncalendar(modelFlashLight.getDateNow(calendarDayB))

                    }


                })

            }
    private fun createAlarmInCalendarnZabor(){


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                modelFlashLight.getItemCalendarCombine.collect { listItems->
                    val calendarDays = mutableListOf<CalendarDay>()
                    listItems.forEach { item->
                        calendar.timeInMillis = item.alarmTime
                        val clonedCalendar = calendar.clone() as Calendar
                        calendarDay = CalendarDay(clonedCalendar)
                        calendarDay.imageResource = R.drawable.ic_work
                        calendarDays.add(calendarDay)
                    }
                    bindingZabor.calendarView.setCalendarDays(calendarDays)
                }


            }
        }
        bindingZabor.calendarView.setOnCalendarDayClickListener(object :
            OnCalendarDayClickListener {
            override fun onClick(calendarDay: CalendarDay) {
                calendarDayB = calendarDay.calendar
                modelFlashLight.insetTimeIncalendar(modelFlashLight.getDateNow(calendarDayB))

            }


        })

    }


    override fun onResume() {
        super.onResume()
            calendarZero = Calendar.getInstance()

            if (!modelFlashLight.getPremium()) Toast.makeText(
                view?.context,
                "Отображение дел в календаре доступно в PREMIUM версии",
                Toast.LENGTH_SHORT
            ).show()

    }




    private fun initRcView() {
        if (pref.getTheme() == THEME_ZABOR) {
            val itemAdapter = ItemAdapter<SimpleItem>()
            val fastAdapter = FastAdapter.with(itemAdapter)
            bindingZabor.rcViewItem.layoutManager = LinearLayoutManager(requireContext())
            bindingZabor.rcViewItem.adapter = fastAdapter


            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    modelFlashLight.listItemCalendarflow.collect {rawDataList->

                        val items = rawDataList.map { data ->
                            SimpleItem(data,pref,itemClickHandler,themeImp)
                        }

                        FastAdapterDiffUtil.set(itemAdapter, items, object : com.mikepenz.fastadapter.diff.DiffCallback<SimpleItem> {

                            override fun areItemsTheSame(oldItem: SimpleItem, newItem: SimpleItem): Boolean {

                                return oldItem.item.id == newItem.item.id
                            }

                            override fun areContentsTheSame(oldItem: SimpleItem, newItem: SimpleItem): Boolean {
                                return oldItem.item == newItem.item
                            }

                            override fun getChangePayload(oldItem: SimpleItem, oldItemPosition: Int, newItem: SimpleItem, newItemPosition: Int): Any? {
                                return null
                            }
                        })

                        if (rawDataList.isNotEmpty()) bindingZabor.tvDela.visibility = View.GONE
                        else bindingZabor.tvDela.visibility = View.VISIBLE
                    }


                }
            }
        }
        else  {
            val itemAdapter = ItemAdapter<SimpleItem>()
            val fastAdapter = FastAdapter.with(itemAdapter)
            binding.rcViewItem.layoutManager = LinearLayoutManager(requireContext())
            binding.rcViewItem.adapter = fastAdapter


            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    modelFlashLight.listItemCalendarflow.collect {rawDataList->

                        val currentItems = itemAdapter.adapterItems.map { it.item }

                        // Если данные абсолютно идентичны (и статус, и порядок), только тогда игнорируем
                        if (currentItems == rawDataList) return@collect

                        val items = rawDataList.map { data -> SimpleItem(data,pref,itemClickHandler,themeImp) }
                        FastAdapterDiffUtil.set(itemAdapter, items, object : com.mikepenz.fastadapter.diff.DiffCallback<SimpleItem> {

                            // 1. Проверяем, та же ли это самая карточка (по ID)
                            override fun areItemsTheSame(oldItem: SimpleItem, newItem: SimpleItem): Boolean {
                                return oldItem.identifier == newItem.identifier
                            }

                            // 2. КРИТИЧЕСКИЙ МОМЕНТ: Проверяем, изменились ли данные внутри (например, цвет/статус)
                            override fun areContentsTheSame(oldItem: SimpleItem, newItem: SimpleItem): Boolean {
                                // Так как Item — это data class, равенство '==' проверит все поля сразу.
                                // Если статус изменился, метод вернет false, и FastAdapter ПЕРЕРИСУЕТ карточку!
                                return oldItem.item == newItem.item
                            }

                            override fun getChangePayload(oldItem: SimpleItem, oldItemPosition: Int, newItem: SimpleItem, newItemPosition: Int): Any? {
                                return null
                            }
                        })

                        if (rawDataList.isNotEmpty()) binding.tvDela.visibility = View.GONE
                        else binding.tvDela.visibility = View.VISIBLE
                    }


                }
            }
        }


    }


    private fun theme() {
        with(modelFlashLight) {
            with(bindingZabor) {

                val list = mapOf<Const.Action, Map<View, Int>>(
                    Const.Action.IMAGE_RESOURCE to mapOf(
                        imBAddCalendar to R.drawable.ic_add_zabor
                    ),
                    Const.Action.TEXT_COLOR to mapOf(tvDela to R.color.black)
                )

                modelFlashLight.setView(list)
                setSize(list)
            }
        }

    }


}


