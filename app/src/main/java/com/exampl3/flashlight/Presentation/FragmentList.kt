package com.exampl3.flashlight.Presentation


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.ALARM
import com.exampl3.flashlight.Const.SORT_STANDART
import com.exampl3.flashlight.Const.SORT_USER
import com.exampl3.flashlight.Const.THEME_ZABOR
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Data.ThemeImp
import com.exampl3.flashlight.Data.sharedPreference.SettingsSharedPreference
import com.exampl3.flashlight.Domain.ItemClickHandler
import com.exampl3.flashlight.Domain.useCase.PermissionUseCase
import com.exampl3.flashlight.Presentation.adapters.ItemListAdapter
import com.exampl3.flashlight.Presentation.adapters.draganddrop.DragItemTouchHelperCallback
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.FragmentListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

@AndroidEntryPoint
open class FragmentList : Fragment() {
    private lateinit var binding: FragmentListBinding
    private lateinit var adapter: ItemListAdapter

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
        val pLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                // Юзер ответил на запрос уведомлений (неважно, разрешил или отказал)

                // Плавный переход к батарее — теперь они не столкнутся лбами!
                if (permissionUseCase.isBatteryOptimizationEnabled(requireContext())) {
                    try {
                        startActivity(permissionUseCase.getBatteryOptimizationIntent(requireContext()))
                    } catch (e: Exception) {
                        // Резервный вариант на случай косяков с интентом
                        val fallbackIntent =
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", requireContext().packageName, null)
                            }
                        startActivity(fallbackIntent)
                    }
                }


            }
        itemClickHandler = ItemClickHandler(
            context = requireContext(),
            modelFlashLight = modelFlashLight,
            lifecycleOwner = viewLifecycleOwner,
            pickImageLauncher = pickImageLauncher,
            pLauncher = pLauncher
        )
        theme()
        initRcView()



        modelFlashLight.categoryItemLD.observe(viewLifecycleOwner) { value ->
            binding.tvCategory.text = value

        }


        db.CourseDao().getAll().observe(viewLifecycleOwner) {
            modelFlashLight.categoryItemLD.value?.let { it1 ->
                modelFlashLight.updateCategory(it1)

            }
        }

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


       // binding.imBAddFrag.setOnClickListener {
           // modelFlashLight.getItemMaxSort()
           // DialogItemList.alertItem(
              //  requireContext(),
              //  object : DialogItemList.Listener {
               //     override fun onClickItem(
                //        name: String,
                //        action: Int?,
                //        id: Int?,
                //       desc: String?,
                //        uri: String?,
                //      category: String?
               //     ) {
               //         var item: Item
               //         var permanentFile = ""
               //         if (uri!!.isNotEmpty()) {
               //             permanentFile =
               //                 modelFlashLight.saveImagePermanently(requireContext(), uri.toUri())
               //                     .toString()
               //         }
               //         modelFlashLight.insertItem(
               //             Item(
               //                 null,
                //                name,
               //                 category = category.toString(),
               //                 desc = desc,
               //                 alarmTime = 0,
               //                 alarmText = permanentFile,
               //                 sort = modelFlashLight.maxSorted.value ?: 0
               //             )
               //         )
//
//
//                        if (action == ALARM) {
//                           if (view.let {
//                                    Const.isPermissionGranted(
//                                        it.context,
 //                                       Manifest.permission.POST_NOTIFICATIONS
//                                    )
//                                }) {
//                                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
//                                    delay(500)
//                                    item = db.CourseDao().getAllList().last()
  //                                  if (item.name == name) {
    //                                    withContext(Dispatchers.Main) {
      //                                      modelFlashLight.insertDateAndAlarm(
        //                                        item,
          //                                      null,
            //                                    requireContext()
              //                              )
                //                        }
                  //                  } else {
                    //                    delay(1000)
                      //                  item = db.CourseDao().getAllList().last()
                        //                withContext(Dispatchers.Main) {
                          //                  modelFlashLight.insertDateAndAlarm(
                            //                    item,
//                                                null,
  //                                              requireContext()
    //                                        )
      //                                  }
        //                            }
//
  //                              }
//
  //                          } else {
    //                            DialogItemList.permissonAlert(requireContext(),permissionUseCase, pLauncher)
//
  //                          }
//
//
  //                      }
    //                }
      //          },
        //        null,
          //      model = modelFlashLight,
//                lifecycleOwner = this,
  //              pick = pickImageLauncher,
    //            false
      //      )
//
  //      }

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

    // private fun initRcView() {
    //     val rcView = binding.rcView

    //     adapter = ItemListAdapter(
    //         itemClickHandler = itemClickHandler,
    //         onOrderChanged = { updatedList ->
    //             modelFlashLight.updateItemsOrder(updatedList)
    //         },
    //         touchHelper = null,
    //         pref,
    //         themeImp
    //     )
    //     rcView.layoutManager = LinearLayoutManager(requireContext())
    //     rcView.adapter = adapter
    //     val touchHelper = ItemTouchHelper(DragItemTouchHelperCallback(adapter))
    //     if (modelFlashLight.getSort() == SORT_USER) {
    //         touchHelper.attachToRecyclerView(rcView)
    //         adapter.touchHelper = touchHelper
    //     }

    //     modelFlashLight.listItemLD.observe(viewLifecycleOwner) { list ->
    //         scrollInStartAdapter() // это чтобы при создании жлемента, был скролл наверх
    //         if (modelFlashLight.getSort() == SORT_STANDART) {
    //             adapter.submitList(list.sortedBy { it.id }.reversed().sortedBy { it.alarmTime }
    //                 .reversed().sortedBy { it.change }
    //                 .reversed().sortedBy { it.changeAlarm }
    //                 .reversed())
    //         } else {
    //             adapter.submitList(list.sortedBy { it.sort })
    //         }


    //     }

    // } // инициализировал ресайклер

    // private fun scrollInStartAdapter() {
    //     adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
    //         override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
    //             if (positionStart == 0) {  // Элементы добавились в начало (верх списка)
    //                 binding.rcView.scrollToPosition(0)
    //                 adapter.unregisterAdapterDataObserver(this)
    //             }
    //         }
    //     })
    // }

    private fun initRcView() {
    val rcView = binding.rcView

    adapter = ItemListAdapter(
        itemClickHandler = itemClickHandler,
        onOrderChanged = { updatedList ->
            modelFlashLight.updateItemsOrder(updatedList)
        },
        touchHelper = null,
        pref,
        themeImp
    )
    rcView.layoutManager = LinearLayoutManager(requireContext())
    rcView.adapter = adapter

    val touchHelper = ItemTouchHelper(DragItemTouchHelperCallback(adapter))

        if (modelFlashLight.getSort() == SORT_USER) {
            touchHelper.attachToRecyclerView(rcView)
            adapter.touchHelper = touchHelper
        }


    // 1. ПОДПИСКА НА ТИП СОРТИРОВКИ (Включаем/выключаем Drag-and-Drop на лету)
//    viewLifecycleOwner.lifecycleScope.launch {
//        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//            modelFlashLight.sortType.collect { currentSort ->
//                if (currentSort == SORT_USER) {
//                    touchHelper.attachToRecyclerView(rcView)
//                    adapter.touchHelper = touchHelper
//                }
////                else {
////                    touchHelper.attachToRecyclerView(null)
////                    adapter.touchHelper = null
////                }
//            }
//        }
//    }

    // 2. ПОДПИСКА НА ОТСОРТИРОВАННЫЙ СПИСОК (Flow)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                modelFlashLight.sortedItemsFlow.collect { readyList ->
                    adapter.submitList(readyList)
                }
            }
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

