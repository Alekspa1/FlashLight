package com.exampl3.flashlight.Presentation



import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.exampl3.flashlight.Data.Const
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.FragmentBlankFlashLightBinding
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FragmentFlashLight : Fragment()  {
    private lateinit var binding: FragmentBlankFlashLightBinding
    private val model: ViewModelFlashLight by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlankFlashLightBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.premium.observe(viewLifecycleOwner){
            Log.d("MyLog", it.toString())
        }


        binding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            model.turnFlasLigh(isChecked)
            model.saveSP(isChecked)
            if (isChecked) binding.toggleButton.setButtonDrawable(R.drawable.ic_on)
            else {
                binding.toggleButton.setButtonDrawable(R.drawable.ic_of)
               // if(!Const.premium)
                (activity as MainActivity).showAd()
            }
        }


    }


    companion object {
        fun newInstance() = FragmentFlashLight()
    }

}