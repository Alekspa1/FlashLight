package com.exampl3.flashlight.Presentation


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.ALARM
import com.exampl3.flashlight.Const.SORT_USER
import com.exampl3.flashlight.Const.THEME_ZABOR
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.ThemeImp
import com.exampl3.flashlight.Data.sharedPreference.SettingsSharedPreference
import com.exampl3.flashlight.Domain.ItemClickHandler
import com.exampl3.flashlight.Domain.LogText
import com.exampl3.flashlight.Domain.useCase.PermissionUseCase
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.FragmentListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

import com.exampl3.flashlight.Presentation.adapters.SimpleItem
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.drag.SimpleDragCallback
import com.mikepenz.fastadapter.utils.DragDropUtil


@AndroidEntryPoint
open class FragmentList : Fragment() {
    private lateinit var binding: FragmentListBinding

    @Inject
    lateinit var db: Database

    @Inject
    lateinit var pref: SettingsSharedPreference

    @Inject
    lateinit var themeImp: ThemeImp

    @Inject
    lateinit var voiceIntent: Intent

    @Inject
    lateinit var permissionUseCase: PermissionUseCase
    private val modelFlashLight: ViewModelFlashLight by activityViewModels()
    private lateinit var pLauncher: ActivityResultLauncher<String>

    private lateinit var itemClickHandler: ItemClickHandler
    private lateinit var myItemTouchHelper: ItemTouchHelper


    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            modelFlashLight.uriPhoto.value = it.toString()
        }
    }

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
         pLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
        itemClickHandler = ItemClickHandler(
            context = requireContext(),
            modelFlashLight = modelFlashLight,
            lifecycleOwner = viewLifecycleOwner,
            pickImageLauncher = pickImageLauncher,
            pLauncher = pLauncher
        )
        theme()
        initRcView()



//        modelFlashLight.categoryItemLD.observe(viewLifecycleOwner) { value ->
//            binding.tvCategory.text = value
//
//        }



//        db.CourseDao().getAll().observe(viewLifecycleOwner) {
//            modelFlashLight.categoryItemLD.value?.let { it1 ->
//                modelFlashLight.updateCategory(it1)
//
//            }
//        }

        val launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                   // modelFlashLight.getItemMaxSort()
                    val text = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    if (text != null) {
                      //  modelFlashLight.getItemMaxSort()
                        // modelFlashLight.insertItem(
                        //     Item(
                        //         null,
                        //         text[0],
                        //         category = modelFlashLight.categoryItemLD.value!!,
                        //         sort = modelFlashLight.maxSorted.value ?: 0,
                        //         alarmTime = 0,
                        //     )
                        // )

                        modelFlashLight.insertItem(       
                    name = text[0], // Текст из голосового ввода
                    
                    // БЕРЕМ КАТЕГОРИЮ ИЗ FLOW ЧЕРЕЗ .value (Прямо как раньше из LiveData!)
                    category = modelFlashLight.categoryItemFlow.value, 
                    
                    desc = null,          // У голосовой заметки нет описания
                    alarmText = "",       // У голосовой заметки нет картинки
                    hasAlarmPermission = false, // Будильник для голосового ввода выключен
                    isAlarmAction = false,      // Будильник для голосового ввода выключен
                    context = requireContext()
                )


                    }


                }

            }

            binding.imBAddFrag.setOnClickListener {
    DialogItemList.alertItem(
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
                    permanentFile = modelFlashLight.saveImagePermanently(requireContext(), uri.toUri()).toString()
                }

                // 2. Проверяем разрешение на уведомления
                val hasPermission = Const.isPermissionGranted(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                val isAlarm = (action == ALARM)

                // 3. Просто отдаем всё во ViewModel! Она сделает всё сама, без фризов и задержек
                modelFlashLight.insertItem(
                    name = name,
                    category = category.toString(),
                    desc = desc,
                    alarmText = permanentFile,
                    hasAlarmPermission = hasPermission,
                    isAlarmAction = isAlarm,
                    context = requireContext()
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
        false
    )
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
        val itemAdapter = ItemAdapter<SimpleItem>()
        val fastAdapter = FastAdapter.with(itemAdapter)
        fastAdapter.onClickListener = { _, _, item, _ ->
        // Вызываем вашу команду редактирования карточки
        itemClickHandler.onClick(item.item, Const.CHANGE_ITEM)
            true // Клик успешно обработан
        }
        binding.rcView.layoutManager = LinearLayoutManager(requireContext())
        binding.rcView.adapter = fastAdapter
        touchHelper(itemAdapter)



        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                modelFlashLight.sortedItemsFlow.collect { rawDataList ->
                    scrollInStartAdapter(fastAdapter)
                    val currentItems = itemAdapter.adapterItems.map { it.item }

                    // Если данные абсолютно идентичны (и статус, и порядок), только тогда игнорируем
                    if (currentItems == rawDataList) return@collect

                    val items = rawDataList.map { data -> 
                        SimpleItem(data, pref, itemClickHandler, themeImp).apply {
                            // Передаем ViewHolder во фрагмент при нажатии на иконку
                            onStartDragListener = { viewHolder ->
                            // Запускаем перетаскивание карточки вручную
                                if (modelFlashLight.getSort() == SORT_USER && ::myItemTouchHelper.isInitialized) {
                myItemTouchHelper.startDrag(viewHolder)
            }
                                        }
                                    }
                                }
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
                }

            }
        }




        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                modelFlashLight.categoryItemFlow.collect { category ->
                    binding.tvCategory.text = category
                }
            }
        }
}

    private fun scrollInStartAdapter(fastAdapter: FastAdapter<SimpleItem>) {
        fastAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)

                if (positionStart == 0) {  // Элементы добавились в начало (верх списка)
                    binding.rcView.scrollToPosition(0)

                    // ИСПРАВЛЕНО: отписываемся через fastAdapter
                    fastAdapter.unregisterAdapterDataObserver(this)
                }
            }
        })
    }

 private fun touchHelper(itemAdapter: ItemAdapter<SimpleItem>) {
    // Создаем объект SimpleDragCallback
    val dragCallback = object : SimpleDragCallback(
        object : ItemTouchCallback {
            override fun itemTouchOnMove(oldPosition: Int, newPosition: Int): Boolean {
                DragDropUtil.onMove(itemAdapter, oldPosition, newPosition)
                return true
            }

            override fun itemTouchDropped(oldPosition: Int, newPosition: Int) {
                super.itemTouchDropped(oldPosition, newPosition)
                if (oldPosition == newPosition) return
                val updatedList = itemAdapter.adapterItems.mapIndexed { index, item ->
                    item.item.copy(sort = index)
                }
                modelFlashLight.updateItemsOrder(updatedList)
            }
        }
    ) { // <-- Скобка ОТКРЫВАЕТ внутренности SimpleDragCallback
        
        override fun isLongPressDragEnabled(): Boolean {
            return false
        }

        // 1. СТРАБОТАЕТ, КОГДА КАРТОЧКУ ЗАЖАЛИ (Делаем прозрачной)
        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                viewHolder?.itemView?.alpha = 0.5f
            }
        }

        // 2. СРАБОТАЕТ, КОГДА КАРТОЧКУ ОТПУСТИЛИ (Возвращаем 100% яркость)
        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            
            // Если у вас там было изменение прозрачности (alpha = 1.0f), оставьте его тоже:
            viewHolder.itemView.alpha = 1.0f
        }
        
    } // <-- Эта скобка ЗАКРЫВАЕТ объект dragCallback! Она критически важна.

    // Код инициализации идет дальше в самом методе touchHelper
    myItemTouchHelper = ItemTouchHelper(dragCallback)
    

    if (modelFlashLight.getSort() == SORT_USER) {
        myItemTouchHelper.attachToRecyclerView(binding.rcView)
    }
}


    override fun onResume() {
        super.onResume()
        calendarZero = Calendar.getInstance()
    }

    private fun theme() {
        with(modelFlashLight) {
            if (getTheme() == THEME_ZABOR) {
                with(binding) {
                    val list = mapOf<Const.Action, Map<View, Int>>(
                        Const.Action.IMAGE_RESOURCE to mapOf(
                            imBAddFrag to R.drawable.ic_add_zabor,
                            imVoiceFrag to R.drawable.ic_micto_zabor
                        ),
                        Const.Action.TEXT_STYLE to mapOf(tvCategory to R.style.StyleMenuZabor)
                    )
                    modelFlashLight.setView(list)
                }

            }
        }

    }


}

