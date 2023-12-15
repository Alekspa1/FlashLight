package com.exampl3.flashlight.Presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.FragmentListBinding


class FragmentList : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentListBinding.inflate(inflater,container,false)
        return binding.root
    }

    companion object {

        fun newInstance() = FragmentList()
    }
}