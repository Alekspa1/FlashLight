package com.exampl3.flashlight.Presentation


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Domain.AlarmManagerImp
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Data.sharedPreference.SharedPreferenceImpl
import com.exampl3.flashlight.Domain.InsertAlarm
import com.exampl3.flashlight.Domain.InsertTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ViewModelFlashLight @Inject constructor(
    private val pref: SharedPreferenceImpl,
    private val alarmInsert: AlarmManagerImp,
    private val db: Database,
    private val insertAlarm: InsertAlarm,
) : ViewModel() {


    fun savePremium(flag: Boolean) = pref.savePremium(flag)
    fun getPremium() = pref.getPremium()

    fun saveNoteBook(value: String) = pref.saveStringNoteBook(value)
    fun getNotebook() = pref.getStringNoteBook()

    val categoryItemLD = MutableLiveData<String>()

    val listItemLD = MutableLiveData<List<Item>>()

    fun alarmInsert(item: Item, action: Int) {
        alarmInsert.alarmInsert(item, action)

    }

    fun updateCategory(value: String) {

        categoryItemLD.value = value
        viewModelScope.launch {
            listItemLD.value = db.CourseDao().getAllNewNoFlow(value)
        }


    }

    fun updateItem(item: Item) {
        viewModelScope.launch { db.CourseDao().updateItem(item) }
    }

    fun insertAlarm(item: Item) {
        viewModelScope.launch { db.CourseDao().insertItem(item) }
    }

    private suspend fun listItem(calendaZero: Long): List<Item> {
        return db.CourseDao().getUpdateItemRestartPhone(calendaZero).filter { it.changeAlarm }
    }

    fun updateAlarm(calendaZero: Long) {
        viewModelScope.launch {
            listItem(calendaZero).forEach { item ->
                when (item.interval) {
                    Const.alarmOne -> {
                        alarmInsert(
                            item,
                            Const.alarmOne
                        )

                    }

                    else -> {
                        if (!getPremium()) {
                            alarmInsert(
                                item,
                                Const.deleteAlarm
                            )
                            updateItem(item.copy(changeAlarm = false))
                        } else {
                            alarmInsert(
                                item,
                                item.interval
                            )

                        }

                    }
                }
            }
        }
    }

}

