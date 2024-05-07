package com.exampl3.flashlight.model

import android.content.Context
import android.hardware.camera2.CameraManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TurnFlashLightImpl @Inject constructor() {
    private lateinit var camManager: CameraManager
    private lateinit var cameraId: String
    fun turnFlashLight(con: Context, flag: Boolean) {
        camManager = (con.getSystemService(Context.CAMERA_SERVICE) as CameraManager?)!!
        cameraId = camManager.cameraIdList[0]
        camManager.setTorchMode(cameraId, flag)
    }

}