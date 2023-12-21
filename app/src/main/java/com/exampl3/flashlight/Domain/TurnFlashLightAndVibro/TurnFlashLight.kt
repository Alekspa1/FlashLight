package com.exampl3.flashlight.Domain.TurnFlashLightAndVibro

import android.content.Context
import android.hardware.camera2.CameraManager

class TurnFlashLight(private val turnFlashLightAndVibro: TurnFlashLightAndVibro) {
    fun turnFlashLight(con: Context, flag: Boolean){
        turnFlashLightAndVibro.turnFlashLight(con, flag)

    }
}