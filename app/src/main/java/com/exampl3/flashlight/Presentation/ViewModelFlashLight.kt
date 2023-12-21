package com.exampl3.flashlight.Presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import com.exampl3.flashlight.Data.TurnFlashLightImpl
import com.exampl3.flashlight.Domain.TurnFlashLightAndVibro.TurnFlashLight
import com.exampl3.flashlight.Domain.TurnFlashLightAndVibro.TurnVibro

class ViewModelFlashLight: ViewModel() {
    private val repository = TurnFlashLightImpl
    private val turnFlashLight = TurnFlashLight(repository)
    private val turnVibro = TurnVibro(repository)


    fun turnFlasLigh(con: Context, flag: Boolean){
        turnFlashLight.turnFlashLight(con, flag)
    }
    fun turnVibro(con: Context, time: Long){
        turnVibro.turnVibro(con, time)
    }
}