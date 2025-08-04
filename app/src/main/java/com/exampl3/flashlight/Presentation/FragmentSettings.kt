package com.exampl3.flashlight.Presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.exampl3.flashlight.Const.FOREVER
import com.exampl3.flashlight.Const.ONE_MONTH
import com.exampl3.flashlight.Const.ONE_YEAR
import com.exampl3.flashlight.Const.SIX_MONTH
import com.exampl3.flashlight.Const.SORT_STANDART
import com.exampl3.flashlight.Const.SORT_USER
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.FragmentSettingsBinding
import kotlin.getValue

class FragmentSettings : Fragment() {


    private lateinit var binding: FragmentSettingsBinding
    val modelFlashLight: ViewModelFlashLight by activityViewModels()

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
        binding.bSetSort.setOnClickListener {
            DialogItemList.settingSort(requireActivity(), object : DialogItemList.ActionInt {
                override fun onClick(action: Int) {
                    Log.d("MyLog", action.toString())
                    when (action) {
                        0 -> modelFlashLight.saveSort(SORT_STANDART)
                        1 -> modelFlashLight.saveSort(SORT_USER)
                    }
                    Toast.makeText(requireContext(), "Изменения вступят в силу после перезапуска приложения", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }
}