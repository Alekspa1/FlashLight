package com.exampl3.flashlight.Presentation

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.exampl3.flashlight.Data.Const
import com.exampl3.flashlight.Domain.Adapter.VpAdapter
import com.exampl3.flashlight.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest

class MainActivity : AppCompatActivity() {
    private var bannerAd: BannerAdView? = null
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

        initYaBaner(this)
        initVp()
    }
    fun initVp(){
        vpAdapter = VpAdapter(this, listFrag)
        binding.placeHolder.adapter = vpAdapter
        TabLayoutMediator(binding.tabLayout, binding.placeHolder){
            tab, pos ->
            tab.text = listName[pos]
        }.attach()
    } // инициализирую ViewPager
    private fun initYaBaner(contex: Context){
        bannerAd = BannerAdView(contex)
        binding.yaBaner.setAdUnitId(Const.baner)
        binding.yaBaner.setAdSize(BannerAdSize.stickySize(contex, 350))
        val adRequest = AdRequest.Builder().build()
        binding.yaBaner.loadAd(adRequest)

    } // Инициализирую Яндекс Рекламу
}