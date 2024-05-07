package com.exampl3.flashlight.Presentation

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.exampl3.flashlight.model.alarmReceiwer.AlarmManagerImp
import com.exampl3.flashlight.model.TurnFlashLightImpl
import com.exampl3.flashlight.Domain.Room.Item
import com.exampl3.flashlight.Domain.sharedPreference.SharedPreferenceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModelFlashLight @Inject constructor(
    private val pref: SharedPreferenceImpl,
    private val alarmInsert: AlarmManagerImp,
    private val turnFlashLight: TurnFlashLightImpl,
    private val application: Application
): AndroidViewModel(application) {
    //private val repository = TurnFlashLightImpl
   // private val turnFlashLight = TurnFlashLight(repository)
    private val _premium = MutableLiveData<Boolean>()
    var premium: LiveData<Boolean> = _premium

    fun saveSP(flag: Boolean) {
        pref.saveSP(flag)
        _premium.value = flag
    }
    private fun getSP() = pref.getSP()

    init {
       _premium.postValue(getSP())
    }



    fun turnFlasLigh(flag: Boolean){
            turnFlashLight.turnFlashLight(application, flag)
    }
//    fun alarmInsert(item: Item, time: Long, context: Context,alarmManager: AlarmManager, action: Int){
//        alarmInsert.alarmManagerInsert(item, time, context,alarmManager, action)
//    }
fun alarmInsert(item: Item, time: Long, context: Context,alarmManager: AlarmManager, action: Int){
    alarmInsert.alarmInsert(item, action)
}


}