package com.exampl3.flashlight.Presentation


import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import androidx.fragment.app.Fragment

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = ViewModelFlashLight()


        binding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            model.turnFlasLigh(view.context, isChecked)
            model.turnVibro(view.context, 150)
            if (isChecked) binding.toggleButton.setButtonDrawable(R.drawable.ic_on)
            else binding.toggleButton.setButtonDrawable(R.drawable.ic_of)
        }
    }

    companion object {
        fun newInstance() = FragmentFlashLight()
    }

}