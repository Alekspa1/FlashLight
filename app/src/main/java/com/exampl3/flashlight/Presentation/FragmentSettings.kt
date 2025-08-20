package com.exampl3.flashlight.Presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.exampl3.flashlight.Const.DONATE
import com.exampl3.flashlight.Const.SORT_STANDART
import com.exampl3.flashlight.Const.SORT_USER
import com.exampl3.flashlight.Const.THEME_FUTURE
import com.exampl3.flashlight.Const.THEME_ZABOR
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.FragmentSettingsBinding
import kotlin.getValue
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.SIZE_LARGE
import com.exampl3.flashlight.Const.SIZE_SMALL
import com.exampl3.flashlight.Const.SIZE_STANDART
import com.exampl3.flashlight.Domain.useCase.SoundPlayer
import com.yandex.mobile.ads.impl.st
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FragmentSettings : Fragment() {


    private lateinit var binding: FragmentSettingsBinding
    val modelFlashLight: ViewModelFlashLight by activityViewModels()
    private lateinit var pLauncher: ActivityResultLauncher<String>

    @Inject
    lateinit var soundPlayer: SoundPlayer

    lateinit var allSounds: Map<String, Uri>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        theme()
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
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

            bAlarm.setOnClickListener {
                val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_AUDIO
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }

                if (ContextCompat.checkSelfPermission(requireActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
                    val allSounds = modelFlashLight.getAllSound()
                    DialogItemList.insertAlarmSound(requireActivity(), object : DialogItemList.ActioinUri {
                        override fun onClick(uri: Uri) {
                            modelFlashLight.saveUriAlarm(uri)

                        }

                    }, allSounds, soundPlayer)
                } else {
                    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
                    } else {
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }

                    permissionsToRequest.forEach {
                        pLauncher.launch(it)
                    }
                }
            }
            bSize.setOnClickListener {
                DialogItemList.settingSize(requireActivity(), object : DialogItemList.ActionInt {
                    override fun onClick(action: Int) {
                        when (action) {
                            0 -> modelFlashLight.saveSize(SIZE_SMALL)
                            1 -> modelFlashLight.saveSize(SIZE_STANDART)
                            2 -> modelFlashLight.saveSize(SIZE_LARGE)
                        }
                        Toast.makeText(requireContext(), "Изменения вступят в силу после перезапуска приложения", Toast.LENGTH_SHORT).show()
                    }

                })
            }
            bFaq.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentSettings_to_fragmentFaq)
            }
        }


    }
    private fun theme(){
        with(binding){
            val listView = mapOf<Const.Action, Map<View, Int>>(
                Const.Action.BACKGROUND_RESOURCE to
                        mapOf(
                            parentSettings to R.drawable.zabor,
                            bSetSort to R.drawable.button_background_item_category_zabor,
                            bSize to R.drawable.button_background_item_category_zabor,
                            bAlarm to R.drawable.button_background_item_category_zabor,
                            bSetTheme to R.drawable.button_background_item_category_zabor,
                            bDonateCard to R.drawable.button_background_item_category_zabor,
                            bCallbackCard to R.drawable.button_background_item_category_zabor,
                            bFaq to R.drawable.button_background_item_category_zabor,
                        ),
                Const.Action.IMAGE_RESOURCE to mapOf(imBack to R.drawable.ic_back_zabor),
                Const.Action.TEXT_STYLE
                        to mapOf(
                    tvSettings to R.style.StyleMenuZabor,
                    bSetSort to R.style.StyleItemZabor,
                    bSize to R.style.StyleItemZabor,
                    bAlarm to R.style.StyleItemZabor,
                    bSetTheme to R.style.StyleItemZabor,
                    bDonateCard to R.style.StyleItemZabor,
                    bCallbackCard to R.style.StyleItemZabor,
                ),

                )
        if (modelFlashLight.getTheme() == THEME_ZABOR) {
            modelFlashLight.setView(listView)
            }
            modelFlashLight.setSize(listView)
        }
    }



}
