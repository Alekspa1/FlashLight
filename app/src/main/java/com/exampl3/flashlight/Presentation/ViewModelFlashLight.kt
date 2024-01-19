package com.exampl3.flashlight.Presentation

import android.app.AlarmManager
import android.content.Context
import androidx.lifecycle.ViewModel
import com.exampl3.flashlight.Data.AlarmManagerImp
import com.exampl3.flashlight.Data.TurnFlashLightImpl
import com.exampl3.flashlight.Domain.Alarm.AlarmManagerDelete
import com.exampl3.flashlight.Domain.Alarm.AlarmManagerInsert
import com.exampl3.flashlight.Domain.Room.Item
import com.exampl3.flashlight.Domain.TurnFlashLightAndVibro.TurnFlashLight
import com.exampl3.flashlight.Domain.TurnFlashLightAndVibro.TurnVibro

class ViewModelFlashLight: ViewModel() {
    private val repository = TurnFlashLightImpl
    private val repositoryAlarm = AlarmManagerImp
    private val turnFlashLight = TurnFlashLight(repository)
    private val turnVibro = TurnVibro(repository)
    private val alarmDel = AlarmManagerDelete(repositoryAlarm)
    private val alarmInsert = AlarmManagerInsert(repositoryAlarm)




    fun turnFlasLigh(con: Context, flag: Boolean){
            turnFlashLight.turnFlashLight(con, flag)


    }
    fun turnVibro(con: Context, time: Long){
            turnVibro.turnVibro(con, time)
    }
    fun alarmInsert(item: Item, time: Long, context: Context,alarmManager: AlarmManager){
        alarmInsert.alarmManagerInsert(item, time, context,alarmManager)
    }
    fun alarmDelete(id: Int, context: Context,alarmManager: AlarmManager){
        alarmDel.alarmManagerDelete(id, context,alarmManager)
    }


}