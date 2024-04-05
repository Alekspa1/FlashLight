package com.exampl3.flashlight.Presentation

import android.app.AlarmManager
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exampl3.flashlight.Data.AlarmManagerImp
import com.exampl3.flashlight.Data.TurnFlashLightImpl
import com.exampl3.flashlight.Domain.Alarm.AlarmManagerInsert
import com.exampl3.flashlight.Domain.Room.Item
import com.exampl3.flashlight.Domain.TurnFlashLightAndVibro.TurnFlashLight

class ViewModelFlashLight: ViewModel() {
    private val repository = TurnFlashLightImpl
    private val repositoryAlarm = AlarmManagerImp
    private val turnFlashLight = TurnFlashLight(repository)
    private val alarmInsert = AlarmManagerInsert(repositoryAlarm)

    var premium = MutableLiveData<Boolean>()




    fun turnFlasLigh(con: Context, flag: Boolean){
            turnFlashLight.turnFlashLight(con, flag)
    }
    fun alarmInsert(item: Item, time: Long, context: Context,alarmManager: AlarmManager, action: Int){
        alarmInsert.alarmManagerInsert(item, time, context,alarmManager, action)
    }


}