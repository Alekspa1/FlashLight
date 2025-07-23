package com.exampl3.flashlight.Presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.FragmentSettingsBinding

class FragmentSettings : Fragment() {


    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }
}