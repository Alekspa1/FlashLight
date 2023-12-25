package com.exampl3.flashlight.Presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewTreeObserver

import com.exampl3.flashlight.Domain.Adapter.VpAdapter

import com.exampl3.flashlight.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import kotlin.math.roundToInt

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
    private val adSize: BannerAdSize
        get() {
            val screenHeight = resources.displayMetrics.run { heightPixels / density }.roundToInt()
            // Calculate the width of the ad, taking into account the padding in the ad container.
            var adWidthPixels = binding.yaBaner.width
            if (adWidthPixels == 0) {
                // If the ad hasn't been laid out, default to the full screen width
                adWidthPixels = resources.displayMetrics.widthPixels
            }
            val adWidth = (adWidthPixels / resources.displayMetrics.density).roundToInt()
            // Determine the maximum allowable ad height. The current value is given as an example.
            val maxAdHeight = screenHeight / 2

            return BannerAdSize.inlineSize(this, adWidth, maxAdHeight)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initVp()
        bannerAd = BannerAdView(this)
       binding.yaBaner.setAdSize(BannerAdSize.stickySize(this, 350))



        binding.yaBaner.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.yaBaner.viewTreeObserver.removeOnGlobalLayoutListener(this);
                bannerAd = loadBannerAd(adSize)
            }
        })
    }

    private fun loadBannerAd(adSize: BannerAdSize): BannerAdView {
        return binding.yaBaner.apply {
            setAdSize(adSize)
            setAdUnitId("demo-banner-yandex")
            setBannerAdEventListener(object : BannerAdEventListener {
                override fun onAdLoaded() {
                    // If this callback occurs after the activity is destroyed, you
                    // must call destroy and return or you may get a memory leak.
                    // Note `isDestroyed` is a method on Activity.
                    if (isDestroyed) {
                        bannerAd?.destroy()
                        return
                    }
                }

                override fun onAdFailedToLoad(adRequestError: AdRequestError) {
                    // Ad failed to load with AdRequestError.
                    // Attempting to load a new ad from the onAdFailedToLoad() method is strongly discouraged.
                }

                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                }

                override fun onLeftApplication() {
                    // Called when user is about to leave application (e.g., to go to the browser), as a result of clicking on the ad.
                }

                override fun onReturnedToApplication() {
                    // Called when user returned to application after click.
                }

                override fun onImpression(impressionData: ImpressionData?) {
                    // Called when an impression is recorded for an ad.
                }
            })
            loadAd(
                AdRequest.Builder()
                    // Methods in the AdRequest.Builder class can be used here to specify individual options settings.
                    .build()
            )
        }






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