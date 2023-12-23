package com.exampl3.flashlight.Presentation

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.VibratorManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.FragmentBlankFlashLightBinding


class FragmentFlashLight : Fragment() {
    private lateinit var binding: FragmentBlankFlashLightBinding
    private lateinit var model: ViewModelFlashLight


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
        model = ViewModelFlashLight()


        binding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            model.turnFlasLigh(view.context, isChecked)
            model.turnVibro(view.context, 150)
            if (isChecked) binding.toggleButton.setButtonDrawable(R.drawable.turn_on)
            else binding.toggleButton.setButtonDrawable(R.drawable.turn_of)
        }
    }

    companion object {
        fun newInstance() = FragmentFlashLight()
    }

}