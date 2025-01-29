package com.exampl3.flashlight.Presentation


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Domain.AlarmManagerImp
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Data.sharedPreference.SharedPreferenceImpl
import com.exampl3.flashlight.Domain.InsertTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelFlashLight @Inject constructor(
    private val pref: SharedPreferenceImpl,
    private val alarmInsert: AlarmManagerImp,
    private val db: Database,
    private val insertTime: InsertTime,
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


}