package com.exampl3.flashlight.Domain

import android.content.Context
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.CHANGE
import com.exampl3.flashlight.Const.CHANGE_ITEM
import com.exampl3.flashlight.Const.DELETE
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.Room.ListCategory
import com.exampl3.flashlight.Presentation.DialogItemList
import com.exampl3.flashlight.Presentation.ViewModelFlashLight
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.FragmentMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ItemListClickHandler(
    private val context: Context,
    private val modelFlashLight: ViewModelFlashLight,
    private val binding: FragmentMainBinding,
    private val db: Database
) {

  fun onClick(item: ListCategory, action: Int) {
        when (action) {
            DELETE -> {
                DialogItemList.AlertDelete(
                    context,
                    object : DialogItemList.ActionTrueOrFalse {
                        override fun onClick(flag: Boolean) {
                            if (flag) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    db.CourseDao().getAllNewNoFlow(item.name).forEach { itemList ->
                                        modelFlashLight.changeAlarm(itemList, Const.DELETE_ALARM)
                                    }
                                    db.CourseDao()
                                        .deleteItemInCategory(item.name) // удаляю все из бд
                                    db.CourseDao().deleteCategoryMenu(item) // удаляю из меню
                                }
                                modelFlashLight.updateCategory(context.getString(R.string.everyday))

                            }
                        }
                    })
            } // Удаление элемента
            CHANGE_ITEM -> {
                DialogItemList.AlertList(
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
                            modelFlashLight.upgrateCategory(item, name, context)
                        }
                    },
                    item.name
                )

            } // Изменение имени элемента
            CHANGE -> {
                if (modelFlashLight.getPremium()) {
                    modelFlashLight.updateCategory(item.name)
                    binding.drawer.closeDrawer(GravityCompat.START)
                    binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1))
                } else
                    Toast.makeText(
                        context,
                        "Категории доступны в PREMIUM версии",
                        Toast.LENGTH_SHORT
                    ).show()
            } // Простое нажатие
        }


    }

}