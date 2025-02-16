package com.exampl3.flashlight.Presentation


import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Data.Room.ListCategory
import com.exampl3.flashlight.Data.sharedPreference.SharedPreferenceImpl
import com.exampl3.flashlight.Domain.InsertDateAndAlarm
import com.exampl3.flashlight.Domain.insertDateAndTime.InsertActionAlarmByItemUseCase
import com.exampl3.flashlight.Domain.insertDateAndTime.InsertDateUseCase
import com.exampl3.flashlight.Domain.insertDateAndTime.InsertStringAlarmByItemUseCase
import com.exampl3.flashlight.Domain.insertDateAndTime.InsertTimeUseCase
import com.exampl3.flashlight.Domain.insertOrDeleteAlarm.ChangeAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ViewModelFlashLight @Inject constructor(
    private val pref: SharedPreferenceImpl,
    private val db: Database,
    private val insertDateAndTime: InsertDateAndAlarm,
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

    fun insertDateAndAlarm(item: Item, date: Calendar?, context: Context){
        viewModelScope.launch {
            insertDateAndTime.exumDate(item, date, context)
        }
    }
    fun insertStringAndAlarm(item: Item, context: Context, first: Boolean){
        viewModelScope.launch {
            insertDateAndTime.exumString(item, context, first)
        }
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


    fun getListItemByCalendar(time: Long) {
        viewModelScope.launch {
            listItemLDCalendar.value =
                db.CourseDao().getAllListCalendarRcView(time)
                    .filter { item -> item.changeAlarm || !item.change }
        }
    }

}


