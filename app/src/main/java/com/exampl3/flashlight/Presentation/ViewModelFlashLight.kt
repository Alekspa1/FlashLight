package com.exampl3.flashlight.Presentation


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampl3.flashlight.Domain.Room.GfgDatabase
import com.exampl3.flashlight.Domain.model.AlarmManagerImp
import com.exampl3.flashlight.Domain.Room.Item
import com.exampl3.flashlight.Domain.model.sharedPreference.SharedPreferenceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelFlashLight @Inject constructor(
    private val pref: SharedPreferenceImpl,
    private val alarmInsert: AlarmManagerImp,
    private val db: GfgDatabase
): ViewModel() {



    fun savePremium(flag: Boolean) = pref.savePremium(flag)
    fun getPremium() = pref.getPremium()

    fun saveNoteBook(value: String) = pref.saveStringNoteBook(value)
    fun getNotebook() = pref.getStringNoteBook()

    val edit: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val categoryItemLD = MutableLiveData<String>()

    val categoryItemLDNew = MutableLiveData<List<Item>>()

fun alarmInsert(item: Item, action: Int){
    alarmInsert.alarmInsert(item, action)
}

     fun updateCategory(value: String){
         categoryItemLD.value = value
         viewModelScope.launch {
             categoryItemLDNew.value = db.CourseDao().getAllNewNoFlow(value)
         }


    }


}