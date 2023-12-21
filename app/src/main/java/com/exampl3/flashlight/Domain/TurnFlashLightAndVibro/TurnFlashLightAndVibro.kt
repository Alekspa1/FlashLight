package com.exampl3.flashlight.Domain.TurnFlashLightAndVibro

import android.content.Context

interface TurnFlashLightAndVibro{
    fun turnFlashLight(con: Context, flag: Boolean)

    fun turnVibro(con: Context, time: Long)


}