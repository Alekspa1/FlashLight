package com.exampl3.flashlight.Domain.TurnFlashLightAndVibro

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.VibratorManager

class TurnVibro(private val turnFlashLightAndVibro: TurnFlashLightAndVibro) {

    fun turnVibro(con: Context, time: Long){
        turnFlashLightAndVibro.turnVibro(con, time)
    }
}