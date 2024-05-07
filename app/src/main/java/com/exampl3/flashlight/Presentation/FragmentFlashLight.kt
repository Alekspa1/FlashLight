package com.exampl3.flashlight.Presentation



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.FragmentBlankFlashLightBinding
import dagger.hilt.android.AndroidEntryPoint

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
        binding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            model.turnFlasLigh(isChecked)
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