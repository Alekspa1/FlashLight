package com.exampl3.flashlight.Domain

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.ALARM
import com.exampl3.flashlight.Const.CHANGE
import com.exampl3.flashlight.Const.CHANGE_ITEM
import com.exampl3.flashlight.Const.DELETE
import com.exampl3.flashlight.Const.IMAGE
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Presentation.DialogItemList
import com.exampl3.flashlight.Presentation.ViewModelFlashLight



class ItemClickHandler (
    private val context: Context,
    private val modelFlashLight: ViewModelFlashLight,
    private val lifecycleOwner: LifecycleOwner,
    private val pickImageLauncher: ActivityResultLauncher<String>,
    private val pLauncher : ActivityResultLauncher<String>
) {



    fun onClick(item: Item, action: Int) {
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
            }

            DELETE -> {
                if (item.change) {
                    modelFlashLight.deleteItem(item)
                    modelFlashLight.changeAlarm(item, Const.DELETE_ALARM)
                } else {
                    DialogItemList.AlertDelete(
                        context,
                        object : DialogItemList.ActionTrueOrFalse {
                            override fun onClick(flag: Boolean) {
                                if (flag) {
                                    modelFlashLight.deleteItem(item)
                                    modelFlashLight.changeAlarm(item, Const.DELETE_ALARM)
                                }
                            }
                        })
                }
            }

            ALARM -> {
                if (Const.isPermissionGranted(context, Manifest.permission.POST_NOTIFICATIONS)) {
                    modelFlashLight.insertDateAndAlarm(item, null, context)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // Передайте launcher извне или сделайте его свойством
                        // например, через конструктор
                        // тут пример:
                         pLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            CHANGE_ITEM -> {
                DialogItemList.alertItem(
                    context,
                    object : DialogItemList.Listener {
                        override fun onClickItem(
                            name: String,
                            action: Int?,
                            id: Int?,
                            desc: String?,
                            uri: String?,
                            category: String?
                        ) {
                            var permanentFile = uri
                            if (item.alarmText != uri) {
                                modelFlashLight.deleteSavedImage(item.alarmText.toUri())
                                permanentFile =
                                    modelFlashLight.saveImagePermanently(context, uri!!.toUri())
                                        .toString()
                            }
                            val newitem = item.copy(
                                name = name,
                                desc = desc,
                                alarmText = permanentFile,
                                category = category.toString()
                            )
                            if (item.changeAlarm) modelFlashLight.changeAlarm(
                                newitem,
                                newitem.interval
                            )

                            modelFlashLight.updateItem(newitem)
                            if (action == ALARM && Const.isPermissionGranted(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            ) {
                                modelFlashLight.insertDateAndAlarm(newitem, null, context)
                            }
                        }
                    },
                    item,
                    model = modelFlashLight,
                    lifecycleOwner = lifecycleOwner,
                    pick = pickImageLauncher,
                    false
                )
            }

            IMAGE -> {
                DialogItemList.showExpandedImage(item.alarmText, context)
            }

        }
    }

    fun onLongClick(item: Item){
        modelFlashLight.insertStringAndAlarm(item, context, false)
    }
}