package com.exampl3.flashlight.Presentation


import android.app.AlarmManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.exampl3.flashlight.Data.Const
import com.exampl3.flashlight.Domain.Adapter.VpAdapter
import com.exampl3.flashlight.Domain.Room.GfgDatabase
import com.exampl3.flashlight.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    private var bannerAd: BannerAdView? = null
    private lateinit var db: GfgDatabase
    private lateinit var binding: ActivityMainBinding
    private lateinit var vpAdapter: VpAdapter
    private lateinit var calendarZero: Calendar
    private lateinit var modelFlashLight: ViewModelFlashLight
    private lateinit var alarmManager: AlarmManager
    private val listFrag = listOf(
        FragmentNotebook.newInstance(),
        FragmentList.newInstance(),
        FragmentFlashLight.newInstance()
    )
    private val listName = listOf(
        "Блокнот",
        "Список дел",
        "Фонарик"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        calendarZero = Calendar.getInstance()
        modelFlashLight = ViewModelFlashLight()
        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        setContentView(binding.root)
        initVp()
        initDb()
        initYaBaner()
        updateAlarm()


    }


    fun initVp(){
        vpAdapter = VpAdapter(this, listFrag)
        binding.placeHolder.adapter = vpAdapter
        TabLayoutMediator(binding.tabLayout, binding.placeHolder){
            tab, pos ->
            tab.text = listName[pos]
        }.attach()
    } // инициализирую ViewPager
    private fun initDb() {
        db = Room.databaseBuilder(
            this,
            GfgDatabase::class.java, "db"
        ).build()
    } // инициализирую БД
    private fun initYaBaner(){
        bannerAd = BannerAdView(this)
        binding.yaBaner.setAdUnitId(Const.BANER)
        binding.yaBaner.setAdSize(BannerAdSize.stickySize(this, 350))
        val adRequest = AdRequest.Builder().build()
        binding.yaBaner.loadAd(adRequest)

    } // Инициализирую Яндекс Рекламу

    private fun updateAlarm(){
        Thread {
            db.CourseDao().getAllList().forEach { item ->
                if (item.changeAlarm && item.alarmTime > calendarZero.timeInMillis) {
                    modelFlashLight.alarmInsert(item, item.alarmTime, this, alarmManager)
                }
            }
        }.start()
    }

}