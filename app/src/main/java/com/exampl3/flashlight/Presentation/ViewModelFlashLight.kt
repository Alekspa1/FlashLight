package com.exampl3.flashlight.Presentation


import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Data.Room.ListCategory
import com.exampl3.flashlight.Data.sharedPreference.SharedPreferenceImpl
import com.exampl3.flashlight.Domain.insertDateAndTime.InsertDateAndTimeUseCase
import com.exampl3.flashlight.Domain.insertDateAndTime.InsertDateUseCase
import com.exampl3.flashlight.Domain.insertOrDeleteAlarm.ChangeAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ViewModelFlashLight @Inject constructor(
    private val pref: SharedPreferenceImpl,
    private val db: Database,
    private val insertDateUseCase: InsertDateUseCase,
    private val insertDateAndTimeUseCase: InsertDateAndTimeUseCase,
    private val changeAlarm: ChangeAlarmUseCase,
) : ViewModel() {


    fun savePremium(flag: Boolean) = pref.savePremium(flag)
    fun getPremium() = pref.getPremium()

    fun saveNoteBook(value: String) = pref.saveStringNoteBook(value)
    fun getNotebook() = pref.getStringNoteBook()

    val categoryItemLD = MutableLiveData<String>()

    val listItemLD = MutableLiveData<List<Item>>()

    val listItemLDCalendar = MutableLiveData<List<Item>>()

    fun getAllListCategory(): Flow<List<ListCategory>> {
        return db.CourseDao().getAllListCategory()
    }


    fun updateCategory(value: String) {

        categoryItemLD.value = value
        viewModelScope.launch {
            listItemLD.value = db.CourseDao().getAllNewNoFlow(value)
        }


    }

    fun insertItem(item: Item) {
        viewModelScope.launch { db.CourseDao().insertItem(item) }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch { db.CourseDao().updateItem(item) }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch { db.CourseDao().delete(item) }
    }

    fun changeAlarm(item: Item, action: Int) {
        changeAlarm.exum(item, action)

    }


    private suspend fun listItem(calendaZero: Long): List<Item> {
        return db.CourseDao().getUpdateItemRestartPhone(calendaZero).filter { it.changeAlarm }
    }

    fun updateAlarm(calendaZero: Long) {
        viewModelScope.launch {
            listItem(calendaZero).forEach { item ->
                when (item.interval) {
                    Const.alarmOne -> {
                        changeAlarm(
                            item,
                            Const.alarmOne
                        )

                    }

                    else -> {
                        if (!getPremium()) {
                            changeAlarm(
                                item,
                                Const.deleteAlarm
                            )
                            updateItem(item.copy(changeAlarm = false))
                        } else {
                            changeAlarm(
                                item,
                                item.interval
                            )

                        }

                    }
                }
            }
        }
    }

    fun insertDateAndTimeitemInAlarm(item: Item, context: Context) {
        viewModelScope.launch {
            val dateDialog = insertDateUseCase.exum(context)
            val filledItem: Item =
                insertDateAndTimeUseCase.exum(getPremium(), item, dateDialog, context)


            updateItem(filledItem)
            changeAlarm(filledItem, filledItem.interval)

        }
    }


    fun insertAlarmByCalendar(item: Item, date: Calendar, context: Context) {
        viewModelScope.launch {
            val filledItem: Item =
                insertDateAndTimeUseCase.exum(getPremium(), item, date, context)
            updateItem(filledItem)
            changeAlarm(filledItem, filledItem.interval)
        }
    }

    fun getListItemByCalendar(time: Long){
        viewModelScope.launch { listItemLDCalendar.value = db.CourseDao().getAllListCalendarRcView(time).filter { item-> item.changeAlarm || !item.change } }
    }

}


