package com.exampl3.flashlight.Domain.model

import android.app.Application
import android.content.Context
import android.hardware.camera2.CameraManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TurnFlashLightImpl @Inject constructor(private val con: Application) {
    private lateinit var camManager: CameraManager
    private lateinit var cameraId: String
    fun turnFlashLight( flag: Boolean) {
        camManager = (con.getSystemService(Context.CAMERA_SERVICE) as CameraManager?)!!
        cameraId = camManager.cameraIdList[0]
        camManager.setTorchMode(cameraId, flag)
    }

}