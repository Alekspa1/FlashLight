package com.exampl3.flashlight.Presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.exampl3.flashlight.Domain.Adapter.VpAdapter

import com.exampl3.flashlight.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var vpAdapter: VpAdapter
    private val listFrag = listOf(
        FragmentList.newInstance(),
        FragmentFlashLight.newInstance()
    )
    private val listName = listOf(
        "Список дел",
        "Фонарик"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initVp()



    }
    fun initVp(){
        vpAdapter = VpAdapter(this, listFrag)
        binding.placeHolder.adapter = vpAdapter
        TabLayoutMediator(binding.tabLayout, binding.placeHolder){
            tab, pos ->
            tab.text = listName[pos]
        }.attach()
    }
}