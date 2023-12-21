package com.exampl3.flashlight.Data

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.VibrationEffect
import android.os.VibratorManager
import com.exampl3.flashlight.Domain.TurnFlashLightAndVibro.TurnFlashLightAndVibro

object TurnFlashLightImpl: TurnFlashLightAndVibro {
    private lateinit var camManager: CameraManager
    private lateinit var vbManager: VibratorManager
    private lateinit var cameraId: String
    override fun turnFlashLight(con: Context, flag: Boolean) {
        camManager = (con.getSystemService(Context.CAMERA_SERVICE) as CameraManager?)!!
        cameraId = camManager.cameraIdList[0]
        camManager.setTorchMode(cameraId, flag)
    }

    override fun turnVibro(con: Context, time: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vbManager = con.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        }
        val vibro = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vbManager.defaultVibrator
        } else {
            TODO("VERSION.SDK_INT < S")
        }
        vibro.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}