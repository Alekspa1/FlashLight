package com.exampl3.flashlight.Presentation

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.exampl3.flashlight.databinding.FragmentBlankFlashLightBinding


class FragmentFlashLight : Fragment() {
    private lateinit var binding: FragmentBlankFlashLightBinding
    private var flag = false
    private lateinit var camManager: CameraManager
    private lateinit var vbManager: VibratorManager
    private lateinit var cameraId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlankFlashLightBinding.inflate(inflater, container, false)
        return binding.root

    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFlashLight(view.context)
        vbManager = view.context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        val vibro = vbManager.defaultVibrator
        binding.toggleButton.setOnClickListener {
            flag = !flag

            camManager.setTorchMode(cameraId, flag)
            vibro.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    companion object {

        fun newInstance() = FragmentFlashLight()
    }
    private fun initFlashLight(con: Context){
        camManager = (con.getSystemService(Context.CAMERA_SERVICE) as CameraManager?)!!
        cameraId = camManager.cameraIdList[0]
    }

}