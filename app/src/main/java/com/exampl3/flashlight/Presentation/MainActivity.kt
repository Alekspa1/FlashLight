package com.exampl3.flashlight.Presentation


import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
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
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult
import ru.rustore.sdk.billingclient.model.purchase.Purchase
import ru.rustore.sdk.billingclient.model.purchase.PurchaseState
import ru.rustore.sdk.billingclient.usecase.ProductsUseCase
import ru.rustore.sdk.billingclient.usecase.PurchasesUseCase
import ru.rustore.sdk.billingclient.utils.pub.checkPurchasesAvailability
import ru.rustore.sdk.core.feature.model.FeatureAvailabilityResult
import java.util.Calendar
import java.util.UUID


class MainActivity : AppCompatActivity(), ItemListAdapter.onClick, ItemListAdapter.onLongClick {
    private var bannerAd: BannerAdView? = null
    private lateinit var db: GfgDatabase
    private lateinit var binding: ActivityMainBinding
    private lateinit var vpAdapter: VpAdapter
    private lateinit var calendarZero: Calendar
    private lateinit var modelFlashLight: ViewModelFlashLight
    private lateinit var alarmManager: AlarmManager
    private lateinit var pref: SharedPreferences
    private lateinit var edit: SharedPreferences.Editor
    private lateinit var adapter: ItemListAdapter
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
    private val shopingList = arrayListOf<Item>()

    private lateinit var billingClient: RuStoreBillingClient
    private lateinit var productsUseCase: ProductsUseCase
    private lateinit var purchasesUseCase: PurchasesUseCase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAll()
        setContentView(binding.root)
        initVp()
        initDb()
        initRcView()
        updateAlarm()
        if (!Const.premium) initYaBaner()
        if (savedInstanceState == null) {
            billingClient.onNewIntent(intent)
        }



        with(binding) {
            imMenu.setOnClickListener {
                modelFlashLight.turnVibro(it.context, 50)
                drawer.openDrawer(GravityCompat.START)
            } //  Меню
            bBuyPremium.setOnClickListener {
                proverkaVozmoznoyOplaty(this@MainActivity)
                drawer.closeDrawer(GravityCompat.START)
            } // ПРЕМИУМ
            bUpdate.setOnClickListener {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse( "https://apps.rustore.ru/app/com.exampl3.flashlight" )))
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
            imBAddMenu.setOnClickListener {
                DialogItemList.AlertList(this@MainActivity, object : DialogItemList.Listener {
                    override fun onClick(name: String) {
                        shopingList.add(Item(null, name))
                    }
                }, null)

            } // Добавить категори.
        }



    }

    override fun onResume() {
        super.onResume()
        shopingList()


    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        billingClient.onNewIntent(intent)
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
                    edit.putBoolean(Const.premium_KEY, true)
                    edit.apply()
                    Toast.makeText(
                        this,
                        "Поздравляю! Теперь вам доступны премиум функции",
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
                if (purchases.isEmpty() && Const.premium) {
                    edit.putBoolean(Const.premium_KEY, false)
                    Const.premium = pref.getBoolean(Const.premium_KEY, false)
                    edit.apply()
                }
                purchases.forEach {
                    if (it.productId == "premium_version_flash_light" &&
                        (it.purchaseState == PurchaseState.PAID || it.purchaseState == PurchaseState.CONFIRMED) && !Const.premium
                    ) {
                        edit.putBoolean(Const.premium_KEY, true)
                        Const.premium = pref.getBoolean(Const.premium_KEY, false)
                        edit.apply()
                    }
                }

            }
            .addOnFailureListener {
                // Process error
            }

    } // Запрос ранее совершенных покупок

    fun initVp() {
        vpAdapter = VpAdapter(this, listFrag)
        binding.placeHolder.adapter = vpAdapter
        TabLayoutMediator(binding.tabLayout, binding.placeHolder) { tab, pos ->
            tab.text = listName[pos]
        }.attach()
    } // инициализирую ViewPager
   private fun initRcView() {
        val rcView = binding.rcView
        adapter = ItemListAdapter(this, this)
        rcView.layoutManager = LinearLayoutManager(this)
        rcView.adapter = adapter
        adapter.submitList(shopingList)

    }

    private fun initDb() {
        db = Room.databaseBuilder(
            this,
            GfgDatabase::class.java, "db"
        ).build()
    } // инициализирую БД

    private fun initYaBaner() {
        bannerAd = BannerAdView(this)
        binding.yaBaner.setAdUnitId(Const.BANER)
        binding.yaBaner.setAdSize(BannerAdSize.stickySize(this, 350))
        val adRequest = AdRequest.Builder().build()
        binding.yaBaner.loadAd(adRequest)

    } // Инициализирую Яндекс Рекламу

    private fun updateAlarm() {
        Thread {
            db.CourseDao().getAllList().forEach { item ->
                if (item.changeAlarm && item.alarmTime > calendarZero.timeInMillis) {
                    when (item.interval) {
                        Const.alarmOne -> {
                            modelFlashLight.alarmInsert(
                                item,
                                item.alarmTime,
                                this,
                                alarmManager,
                                Const.alarmOne
                            )
                        }

                        else -> {
                            if (!Const.premium) {
                                modelFlashLight.alarmInsert(
                                    item,
                                    item.alarmTime,
                                    this,
                                    alarmManager,
                                    Const.deleteAlarmRepeat
                                )
                                db.CourseDao().update(item.copy(changeAlarm = !item.changeAlarm))
                            } else {
                                modelFlashLight.alarmInsert(
                                    item,
                                    item.alarmTime,
                                    this,
                                    alarmManager,
                                    item.interval
                                )
                            }
                        }
                    }
                }
            }
        }.start()
    } // обновляю будильники
    private fun initAll(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        billingClient = RuStoreBillingClientFactory.create(
            context = this,
            consoleApplicationId = "2063541058",
            deeplinkScheme = "yourappscheme"
        )
        productsUseCase = billingClient.products
        purchasesUseCase = billingClient.purchases
        calendarZero = Calendar.getInstance()
        modelFlashLight = ViewModelFlashLight()
        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        pref = this.getSharedPreferences("PREMIUM", Context.MODE_PRIVATE)
        edit = pref.edit()

    } // Инициализирую все

    override fun onLongClick(item: Item) {
        TODO("Not yet implemented")
    }

    override fun onClick(item: Item, action: Int) {
        modelFlashLight.turnVibro(this, 100)
        when (action) {
            Const.change -> {
                Toast.makeText(this, "Изменить состояние", Toast.LENGTH_SHORT).show()

            } // Изменение состояния элемента(активный/неактивный)

            Const.delete -> {
                shopingList.remove(item)
                Toast.makeText(this, "Удалить", Toast.LENGTH_SHORT).show()
                adapter.submitList(shopingList)

            } // Удаления элемента

            Const.alarm -> {
                Toast.makeText(this, "Установка будильника", Toast.LENGTH_SHORT).show()
            } // Установка будильника

            Const.changeItem -> {
                Toast.makeText(this, "Изменить имя", Toast.LENGTH_SHORT).show()
            } // Изменение имени элемента
        }
    }


}