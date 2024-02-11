package com.exampl3.flashlight.Presentation


import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.room.Room
import com.exampl3.flashlight.Data.Const
import com.exampl3.flashlight.Domain.Adapter.ItemListAdapter
import com.exampl3.flashlight.Domain.Adapter.VpAdapter
import com.exampl3.flashlight.Domain.Room.GfgDatabase
import com.exampl3.flashlight.Domain.Room.Item
import com.exampl3.flashlight.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import ru.rustore.sdk.billingclient.RuStoreBillingClient

import ru.rustore.sdk.billingclient.RuStoreBillingClientFactory
import ru.rustore.sdk.billingclient.model.product.Product
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult
import ru.rustore.sdk.billingclient.model.purchase.Purchase
import ru.rustore.sdk.billingclient.model.purchase.PurchaseState
import ru.rustore.sdk.billingclient.usecase.ProductsUseCase
import ru.rustore.sdk.billingclient.usecase.PurchasesUseCase
import ru.rustore.sdk.billingclient.utils.pub.checkPurchasesAvailability
import ru.rustore.sdk.core.feature.model.FeatureAvailabilityResult

import java.util.Calendar
import java.util.UUID


class MainActivity : AppCompatActivity() {
    private var bannerAd: BannerAdView? = null
    private lateinit var db: GfgDatabase
    private lateinit var binding: ActivityMainBinding
    private lateinit var vpAdapter: VpAdapter
    private lateinit var calendarZero: Calendar
    private lateinit var modelFlashLight: ViewModelFlashLight
    private lateinit var alarmManager: AlarmManager
    private lateinit var pref: SharedPreferences
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

    private lateinit var billingClient: RuStoreBillingClient
    private lateinit var productsUseCase: ProductsUseCase
    private lateinit var purchasesUseCase: PurchasesUseCase
















    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        billingClient = RuStoreBillingClientFactory.create(
            context = this,
            consoleApplicationId = "2063541058",
            deeplinkScheme = "https://apps.rustore.ru/app/com.exampl3.flashlight"
        )
        proverka(this)

        productsUseCase = billingClient.products
        purchasesUseCase = billingClient.purchases
        purchasesUseCase.confirmPurchase(purchaseId = "premium_version_flash_light", developerPayload = null)
            .addOnSuccessListener {
               Log.d("MyLog", "Проверка покупки: да")
            }.addOnFailureListener { throwable: Throwable ->
                Log.d("MyLog", "Проверка покупки: $throwable")
            }
























        calendarZero = Calendar.getInstance()
        modelFlashLight = ViewModelFlashLight()
        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        pref = this.getSharedPreferences("PREMIUM", Context.MODE_PRIVATE)
        setContentView(binding.root)

        initVp()
        initDb()
        initYaBaner()

        updateAlarm()
        Const.premium = pref.getBoolean("premium", false)
        Toast.makeText(this, "${Const.premium}", Toast.LENGTH_SHORT).show()


        binding.imMenu.setOnClickListener {
//            binding.drawer.openDrawer(GravityCompat.START)
//            val edit = pref.edit()
//            edit.putBoolean("premium", false)
//            edit.apply()
            purchasesUseCase.purchaseProduct(
                productId = "premium_version_flash_light",
                orderId = UUID.randomUUID().toString(),
                quantity = 1,
                developerPayload = null,
            ).addOnSuccessListener { paymentResult: PaymentResult ->
                when (paymentResult) {
                    // Process PaymentResult
                    else -> {}
                }
            }.addOnFailureListener { throwable: Throwable ->
                Log.d("MyLog", "Нажал на кнопку: $throwable")
            }



        }
        binding.button6.setOnClickListener {
            val edit = pref.edit()
            edit.putBoolean("premium", true)
            edit.apply()
            Toast.makeText(
                this,
                "Поздравляю! Теперь вам доступны премиум функции",
                Toast.LENGTH_SHORT
            ).show()
            binding.drawer.closeDrawer(GravityCompat.START)


        }
        binding.button.setOnClickListener {
            Const.premium = false
            binding.drawer.closeDrawer(GravityCompat.START)
        }

    }

    private fun proverka(context: Context){
        RuStoreBillingClient.checkPurchasesAvailability(context)
            .addOnSuccessListener { result ->
                when (result) {
                    FeatureAvailabilityResult.Available -> {
                    }

                    is FeatureAvailabilityResult.Unavailable -> {
                    }
                }
            }.addOnFailureListener { throwable ->
                Log.d("MyLog", "Проверка возможной покупки: $throwable")
            }
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
                    modelFlashLight.alarmInsert(item, item.alarmTime, this, alarmManager, item.interval)
                }

            }
        }.start()
    } // обновляю будильники


}