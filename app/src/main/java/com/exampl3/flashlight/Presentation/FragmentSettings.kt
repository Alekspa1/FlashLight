package com.exampl3.flashlight.Presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import com.exampl3.flashlight.Const.DONATE
import com.exampl3.flashlight.Const.FOREVER
import com.exampl3.flashlight.Const.ONE_MONTH
import com.exampl3.flashlight.Const.ONE_YEAR
import com.exampl3.flashlight.Const.SIX_MONTH
import com.exampl3.flashlight.Const.SORT_STANDART
import com.exampl3.flashlight.Const.SORT_USER
import com.exampl3.flashlight.Const.THEME_FUTURE
import com.exampl3.flashlight.Const.THEME_ZABOR
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.FragmentSettingsBinding
import kotlin.getValue
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.yandex.mobile.ads.impl.li

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
        val listTextViewSettings = listOf(binding.bSort,binding.bTextSize,binding.bAlarmSound,binding.bTheme,binding.bDonate,binding.bCallback)
        theme(listTextViewSettings)

        with(binding){
            bCallbackCard.setOnClickListener {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:".toUri()
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("apereverzev47@gmail.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "FOCUS")
                }
                try {
                    startActivity(emailIntent)

                } catch (e: Exception) {
                    Toast.makeText(requireActivity(), "Ошибка", Toast.LENGTH_SHORT).show()
                }
            } // Обратная связь
            bDonateCard.setOnClickListener {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, DONATE.toUri()))
                } catch (e: Exception) {
                    Toast.makeText(requireActivity(), "Ошибка", Toast.LENGTH_SHORT).show()
                }
            } // Донат
            imBack.setOnClickListener {
                findNavController().popBackStack()
            }
            bSetSort.setOnClickListener {
                DialogItemList.settingSort(requireActivity(), object : DialogItemList.ActionInt {
                    override fun onClick(action: Int) {
                        when (action) {
                            0 -> modelFlashLight.saveSort(SORT_STANDART)
                            1 -> modelFlashLight.saveSort(SORT_USER)
                        }
                        Toast.makeText(requireContext(), "Изменения вступят в силу после перезапуска приложения", Toast.LENGTH_SHORT).show()
                    }

                })
            }

            bSetTheme.setOnClickListener {
                DialogItemList.settingTheme(requireActivity(), object : DialogItemList.ActionInt {
                    override fun onClick(action: Int) {
                        when (action) {
                            0 -> modelFlashLight.saveTheme(THEME_FUTURE)
                            1 -> modelFlashLight.saveTheme(THEME_ZABOR)
                        }
                        Toast.makeText(requireContext(), "Изменения вступят в силу после перезапуска приложения", Toast.LENGTH_SHORT).show()
                    }

                })
            }
            bSize.setOnClickListener { size(listTextViewSettings) }
        }


    }
    private fun theme(listTextViewSettings: List<TextView>){
        with(binding){
        if (modelFlashLight.getTheme() == THEME_ZABOR) {
                parentSettings.setBackgroundResource(R.drawable.zabor)
                tvSettings.setTextAppearance(R.style.StyleMenuZabor)
                listTextViewSettings.forEach {
                    it.setTextAppearance(R.style.StyleButtonZabor)
                }
            }
        }
    }
    private fun size(listTextViewSettings: List<TextView>){
        listTextViewSettings.forEach { it.textSize = 25f }
    }
}
