package com.exampl3.flashlight.Presentation


import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle

import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.RUSTORE
import com.exampl3.flashlight.Presentation.adapters.VpAdapter
import com.exampl3.flashlight.Domain.Room.GfgDatabase
import com.exampl3.flashlight.Domain.Room.ListCategory
import com.exampl3.flashlight.Presentation.adapters.ListMenuAdapter
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.rustore.sdk.billingclient.RuStoreBillingClient
import ru.rustore.sdk.billingclient.RuStoreBillingClientFactory
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult
import ru.rustore.sdk.billingclient.model.purchase.Purchase
import ru.rustore.sdk.billingclient.model.purchase.PurchaseState
import ru.rustore.sdk.billingclient.usecase.ProductsUseCase
import ru.rustore.sdk.billingclient.usecase.PurchasesUseCase
import ru.rustore.sdk.billingclient.utils.pub.checkPurchasesAvailability
import ru.rustore.sdk.core.feature.model.FeatureAvailabilityResult
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
open class MainActivity : AppCompatActivity(), ListMenuAdapter.onClick {

    private var interstitialAdLoader: InterstitialAdLoader? = null
    private var interstitialAd: InterstitialAd? = null
        private var bannerAd: BannerAdView? = null
    @Inject
    lateinit var db: GfgDatabase
    private lateinit var binding: ActivityMainBinding
    private lateinit var vpAdapter: VpAdapter
    private lateinit var calendarZero: Calendar
    private val modelFlashLight: ViewModelFlashLight by viewModels()
    private lateinit var alarmManager: AlarmManager
    private lateinit var billingClient: RuStoreBillingClient
    private lateinit var productsUseCase: ProductsUseCase
    private lateinit var purchasesUseCase: PurchasesUseCase
    private lateinit var adapter: ListMenuAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAll()
        setContentView(binding.root)
        initVp()
        updateAlarm()
        initRcView()
        if (!modelFlashLight.getPremium()) initYaBaner()
        if (savedInstanceState == null) {
            billingClient.onNewIntent(intent)
        }
        modelFlashLight.updateCategory("Повседневные")
        db.CourseDao().getAllListCategory().asLiveData().observe(this){
            adapter.submitList(it)
        }


        interstitialAdLoader = InterstitialAdLoader(this).apply {
            setAdLoadListener(object : InterstitialAdLoadListener {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    this@MainActivity.interstitialAd = interstitialAd
                    // The ad was loaded successfully. Now you can show loaded ad.
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    // Ad failed to load with AdRequestError.
                    // Attempting to load a new ad from the onAdFailedToLoad() method is strongly discouraged.
                }
            })
        }
        loadInterstitialAd()
        with(binding) {
            imMenu.setOnClickListener {
                drawer.openDrawer(GravityCompat.START)
            } //  Меню
            bBuyPremium.setOnClickListener {
                proverkaVozmoznoyOplaty(this@MainActivity)
                drawer.closeDrawer(GravityCompat.START)
            } // ПРЕМИУМ
            bUpdate.setOnClickListener {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse( RUSTORE )))
                }  catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Ошибка", Toast.LENGTH_SHORT).show()
                }
            } // Проверить обновления
            bCallback.setOnClickListener {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse( "mailto:apereverzev47@gmail.com" )))
                }  catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Ошибка", Toast.LENGTH_SHORT).show()
                }
            } // Обратная связь
            tvCardMenu.setOnClickListener {
                modelFlashLight.updateCategory("Повседневные")
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1))
                drawer.closeDrawer(GravityCompat.START)
            }

            imBAddMenu.setOnClickListener {
                if (modelFlashLight.getPremium()){
                    DialogItemList.AlertList(this@MainActivity, object : DialogItemList.Listener {
                        override fun onClick(name: String) {
                         CoroutineScope(Dispatchers.IO).launch {
                             db.CourseDao().insertCategory(ListCategory(null,name))
                         }
                        }
                    }, null)
                }
                else Toast.makeText(this@MainActivity, "Категории доступны в PREMIUM версии", Toast.LENGTH_SHORT).show()


            }
        }
    }
    private fun destroyInterstitialAd() {
        interstitialAd?.setAdEventListener(null)
        interstitialAd = null
    }
    private fun loadInterstitialAd() {
        val adRequestConfiguration = AdRequestConfiguration.Builder(Const.MEZSTR).build()
        interstitialAdLoader?.loadAd(adRequestConfiguration)
    }
     fun showAd() {
        interstitialAd?.apply {
            setAdEventListener(object : InterstitialAdEventListener {
                override fun onAdShown() {
                    // Called when ad is shown.
                }
                override fun onAdFailedToShow(adError: AdError) {
                    // Called when an InterstitialAd failed to show.
                    // Clean resources after Ad dismissed
                    interstitialAd?.setAdEventListener(null)
                    interstitialAd = null

                    // Now you can preload the next interstitial ad.
                    loadInterstitialAd()
                }
                override fun onAdDismissed() {
                    // Called when ad is dismissed.
                    // Clean resources after Ad dismissed
                    interstitialAd?.setAdEventListener(null)
                    interstitialAd = null

                    // Now you can preload the next interstitial ad.
                    loadInterstitialAd()
                }
                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                }
                override fun onAdImpression(impressionData: ImpressionData?) {
                    // Called when an impression is recorded for an ad.
                }
            })
            show(this@MainActivity)
        }
    }

    private fun proverkaVozmoznoyOplaty(context: Context) {
        RuStoreBillingClient.checkPurchasesAvailability(context)
            .addOnSuccessListener { result ->
                when (result) {
                    FeatureAvailabilityResult.Available -> {
                        pokupka()
                    }
                    is FeatureAvailabilityResult.Unavailable -> {
                        Toast.makeText(context, "Оплата временно недоступна", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }.addOnFailureListener {

            }
    } // Проверка возможности оплатить

    private fun pokupka() {
        purchasesUseCase.purchaseProduct(
            productId = "premium_version_flash_light",
            orderId = UUID.randomUUID().toString(),
            quantity = 1,
            developerPayload = null,
        ).addOnSuccessListener { paymentResult: PaymentResult ->
            when (paymentResult) {
                is PaymentResult.Success -> {
                    modelFlashLight.savePremium(true)
                    Toast.makeText(
                        this,
                        "Поздравляю! Теперь вам доступны PREMIUM функции",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    Toast.makeText(
                        this,
                        "Произошла ошибка оплаты",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.addOnFailureListener {
        }
    } // Покупка товара

    private fun shopingList() {
        purchasesUseCase.getPurchases()
            .addOnSuccessListener { purchases: List<Purchase> ->
                if (purchases.isEmpty() && modelFlashLight.getPremium()) {
                    modelFlashLight.savePremium(false)
                    Toast.makeText(this, "PREMIUM версия была отключена", Toast.LENGTH_SHORT).show()
                }
                purchases.forEach {
                    if (it.productId == "premium_version_flash_light" &&
                        (it.purchaseState == PurchaseState.PAID || it.purchaseState == PurchaseState.CONFIRMED) && !modelFlashLight.getPremium()
                    ) {
                        modelFlashLight.savePremium(true)
                        Toast.makeText(this, "PREMIUM версия была восстановлена", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            .addOnFailureListener {
                // Process error
            }

    } // Запрос ранее совершенных покупок

    private fun initVp() {
        vpAdapter = VpAdapter(this)
        binding.placeHolder.adapter = vpAdapter
        TabLayoutMediator(binding.tabLayout, binding.placeHolder) { tab, pos ->
            tab.text = resources.getStringArray(R.array.vp_title_main)[pos]
        }.attach()

    } // инициализирую ViewPager

    private fun initYaBaner() {
        bannerAd = BannerAdView(this)
        binding.yaBaner.setAdUnitId(Const.BANER)
        binding.yaBaner.setAdSize(BannerAdSize.stickySize(this, 350))
        val adRequest = AdRequest.Builder().build()
        binding.yaBaner.loadAd(adRequest)

    } // Инициализирую Яндекс Рекламу

//    private fun updateAlarm() {
//        CoroutineScope(Dispatchers.IO).launch { db.CourseDao().getAllList().forEach { item ->
//            if (item.changeAlarm && item.alarmTime > calendarZero.timeInMillis) {
//                when (item.interval) {
//                    Const.alarmOne -> {
//                        modelFlashLight.alarmInsert(
//                            item,
//                            Const.alarmOne
//                        )
//                    }
//                    else -> {
//                        if (!modelFlashLight.getSP()) {
//                            modelFlashLight.alarmInsert(
//                                item,
//                                Const.deleteAlarm
//                            )
//                            db.CourseDao().update(item.copy(changeAlarm = false))
//                        }
//                        else {
//                            modelFlashLight.alarmInsert(
//                                item,
//                                item.interval
//                            )
//                        }
//                    }
//                }
//            }
//        } }
//
//    } // обновляю будильники вернуть потом как было

    private fun updateAlarm() {
        CoroutineScope(Dispatchers.Main).launch { db.CourseDao().getAllList().forEach { item ->
            if (item.changeAlarm && item.alarmTime > calendarZero.timeInMillis) {
                modelFlashLight.alarmInsert(item, item.interval)
            }
        } }

    } // обновляю будильники
    private fun initAll(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        billingClient = RuStoreBillingClientFactory.create(
            context = this,
            consoleApplicationId = "2063541058",
            deeplinkScheme = "flashlight"
        )
        productsUseCase = billingClient.products
        purchasesUseCase = billingClient.purchases
        calendarZero = Calendar.getInstance()
        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    } // Инициализирую все
    private fun initRcView() {
        val rcView = binding.rcView
        adapter = ListMenuAdapter(this)
        rcView.layoutManager = LinearLayoutManager(this)
        rcView.adapter = adapter

    } // инициализировал ресайклер


        //Override функции

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            shopingList()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        interstitialAdLoader?.setAdLoadListener(null)
        interstitialAdLoader = null
        destroyInterstitialAd()
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        billingClient.onNewIntent(intent)
    }


    override fun onClick(item: ListCategory, action: Int) {
        modelFlashLight.updateCategory(item.name)

        when(action){
            Const.delete ->{
                DialogItemList.AlertDelete(this, object : DialogItemList.Delete {
                    override fun onClick(flag: Boolean) {
                        if (flag) {
                            CoroutineScope(Dispatchers.IO).launch {
                                db.CourseDao().getAllNewNoFlow(item.name).forEach { itemList->
                                        modelFlashLight.alarmInsert(itemList, Const.deleteAlarm)
                                }
                                db.CourseDao().deleteCategory(item.name) // удаляю все из бд
                                db.CourseDao().deleteCategoryMenu(item) // удаляю из меню
                            }
                            modelFlashLight.updateCategory("Повседневные")

                        }
                    }
                })
            } // Удаление элемента
            Const.changeItem -> {
                DialogItemList.AlertList(
                    this,
                    object : DialogItemList.Listener {
                        override fun onClick(name: String) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val newitem = item.copy(name = name)
                                db.CourseDao().updateCategory(newitem)
                                db.CourseDao().getAllNewNoFlow(item.name).forEach {
                                    db.CourseDao().update(it.copy(category = name))
                                }
                            }
                            modelFlashLight.updateCategory(name)
                        }
                    },
                    item.name
                )

            } // Изменение имени элемента
            Const.change -> {
                binding.drawer.closeDrawer(GravityCompat.START)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1))
            } // Простое нажатие
        }


    }


}