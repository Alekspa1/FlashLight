package com.exampl3.flashlight.Presentation


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View

import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.AUTHORIZED_RUSTORE
import com.exampl3.flashlight.Const.DELETE
import com.exampl3.flashlight.Const.DONATE
import com.exampl3.flashlight.Const.FOREVER
import com.exampl3.flashlight.Const.NOT_AUTHORIZED
import com.exampl3.flashlight.Const.ONE_MONTH
import com.exampl3.flashlight.Const.ONE_YEAR
import com.exampl3.flashlight.Const.PURCHASE_LIST
import com.exampl3.flashlight.Const.RUSTORE
import com.exampl3.flashlight.Const.SIX_MONTH
import com.exampl3.flashlight.Presentation.adapters.VpAdapter
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.Room.ListCategory
import com.exampl3.flashlight.Presentation.adapters.ListMenuAdapter
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.rustore.sdk.billingclient.RuStoreBillingClient
import ru.rustore.sdk.billingclient.RuStoreBillingClientFactory
import ru.rustore.sdk.billingclient.model.product.Product
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult
import ru.rustore.sdk.billingclient.model.purchase.Purchase
import ru.rustore.sdk.billingclient.model.purchase.PurchaseState
import ru.rustore.sdk.billingclient.usecase.ProductsUseCase
import ru.rustore.sdk.billingclient.usecase.PurchasesUseCase
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ListMenuAdapter.onClick {

    private var bannerAd: BannerAdView? = null

    @Inject
    lateinit var db: Database
    private lateinit var binding: ActivityMainBinding
    private lateinit var vpAdapter: VpAdapter

    private lateinit var calendarZero: Calendar
    val modelFlashLight: ViewModelFlashLight by viewModels()
    private lateinit var billingClient: RuStoreBillingClient
    private lateinit var productsUseCase: ProductsUseCase
    private lateinit var purchasesUseCase: PurchasesUseCase
    private lateinit var adapter: ListMenuAdapter
    private lateinit var pLauncher: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAll()
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){}

        if (!modelFlashLight.getPremium()) initYaBaner()
        if (savedInstanceState == null) {
            billingClient.onNewIntent(intent)
        }

        with(binding) {
            if (modelFlashLight.getPremium()) bBuyPremium.text =
                this@MainActivity.getString(R.string.premium_on)
            imMenu.setOnClickListener {
                drawer.openDrawer(GravityCompat.START)
            } //  Меню
            bBuyPremiumCard.setOnClickListener {
                getListProduct()
                drawer.closeDrawer(GravityCompat.START)
            } // ПРЕМИУМ
            bUpdateCard.setOnClickListener {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(RUSTORE)))
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Ошибка", Toast.LENGTH_SHORT).show()
                }
            } // Проверить обновления
            bCallbackCard.setOnClickListener {
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("mailto:apereverzev47@gmail.com")
                        )
                    )
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Ошибка", Toast.LENGTH_SHORT).show()
                }
            } // Обратная связь
            bDonateCard.setOnClickListener {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(DONATE)))
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Ошибка", Toast.LENGTH_SHORT).show()
                }
                drawer.closeDrawer(GravityCompat.START)
            } // Донат
            tvCardMenu.setOnClickListener {
                modelFlashLight.updateCategory("Повседневные")
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1))
                drawer.closeDrawer(GravityCompat.START)
            }

            imBAddMenu.setOnClickListener {
                if (modelFlashLight.getPremium()) {
                    DialogItemList.AlertList(this@MainActivity, object : DialogItemList.Listener {
                        override fun onClickItem(
                            name: String,
                            action: Int?,
                            id: Int?,
                            desc: String?
                        ) {
                            CoroutineScope(Dispatchers.IO).launch {
                                db.CourseDao().insertCategory(ListCategory(null, name))
                            }
                        }
                    }, null)
                } else Toast.makeText(
                    this@MainActivity,
                    "Категории доступны в PREMIUM версии",
                    Toast.LENGTH_SHORT
                ).show()


            }

            tvCardShare.setOnClickListener {
                stub("Общие дела")


            }
            bSettingsCard.setOnClickListener {
                stub("Настройки")
                drawer.closeDrawer(GravityCompat.START)

            }

        }
    }




    private fun initYaBaner() {
        bannerAd = BannerAdView(this)
        binding.yaBaner.setAdUnitId(Const.BANER)
        binding.yaBaner.setAdSize(BannerAdSize.stickySize(this, 350))
        val adRequest = AdRequest.Builder().build()
        binding.yaBaner.loadAd(adRequest)

    } // Инициализирую Яндекс Рекламу


    private fun initAll() {
        modelFlashLight.updateCategory(getString(R.string.everyday))
        calendarZero = Calendar.getInstance()
        billingClient = RuStoreBillingClientFactory.create(
            context = this,
            consoleApplicationId = "2063541058",
            deeplinkScheme = "flashlight"
        )
        productsUseCase = billingClient.products
        purchasesUseCase = billingClient.purchases

        // инициализирую ViewPager
        vpAdapter = VpAdapter(this)
        binding.placeHolder.adapter = vpAdapter
        TabLayoutMediator(binding.tabLayout, binding.placeHolder) { tab, pos ->
            tab.text = resources.getStringArray(R.array.vp_title_main)[pos]
        }.attach()

        // инициализировал ресайклер
        val rcView = binding.rcView
        adapter = ListMenuAdapter(this)
        rcView.layoutManager = LinearLayoutManager(this)
        rcView.adapter = adapter


        // Запрос ранее совершенных покупок
        purchasesUseCase.getPurchases()
            .addOnSuccessListener { purchases: List<Purchase> ->
                val staseList = purchases.map { it.purchaseState }
                if ((purchases.isEmpty() || !staseList.contains(PurchaseState.CONFIRMED)) && modelFlashLight.getPremium()) {
                    updatePremium(false, "PREMIUM версия была отключена")
                }
                purchases.forEach {
                    if (it.purchaseState == PurchaseState.CONFIRMED && !modelFlashLight.getPremium()
                    ) {
                        updatePremium(true, "PREMIUM версия была восстановлена")
                    }
                }

            }
            .addOnFailureListener {
            }
        modelFlashLight.updateAlarm(calendarZero.timeInMillis)

        modelFlashLight.getAllListCategory().asLiveData().observe(this) {
            adapter.submitList(it)
        }

    } // Инициализирую все



    //Override функции

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        billingClient.onNewIntent(intent)
    }


    override fun onClick(item: ListCategory, action: Int) {
        when (action) {
            DELETE -> {
                DialogItemList.AlertDelete(this, object : DialogItemList.ActionTrueOrFalse {
                    override fun onClick(flag: Boolean) {
                        if (flag) {
                            CoroutineScope(Dispatchers.IO).launch {
                                db.CourseDao().getAllNewNoFlow(item.name).forEach { itemList ->
                                    modelFlashLight.changeAlarm(itemList, Const.DELETE_ALARM)
                                }
                                db.CourseDao().deleteItemInCategory(item.name) // удаляю все из бд
                                db.CourseDao().deleteCategoryMenu(item) // удаляю из меню
                            }
                            modelFlashLight.updateCategory(getString(R.string.everyday))

                        }
                    }
                })
            } // Удаление элемента
            Const.CHANGE_ITEM -> {
                DialogItemList.AlertList(
                    this,
                    object : DialogItemList.Listener {
                        override fun onClickItem(
                            name: String,
                            action: Int?,
                            id: Int?,
                            desc: String?
                        ) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val newitem = item.copy(name = name)
                                db.CourseDao().updateCategory(newitem)
                                db.CourseDao().getAllNewNoFlow(item.name).forEach {
                                    db.CourseDao().updateItem(it.copy(category = name))
                                }
                            }
                            modelFlashLight.updateCategory(name)
                        }
                    },
                    item.name
                )

            } // Изменение имени элемента
            Const.CHANGE -> {
                if (modelFlashLight.getPremium()) {
                    modelFlashLight.updateCategory(item.name)
                    binding.drawer.closeDrawer(GravityCompat.START)
                    binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1))
                } else
                    Toast.makeText(
                        this@MainActivity,
                        "Категории доступны в PREMIUM версии",
                        Toast.LENGTH_SHORT
                    ).show()
            } // Простое нажатие
        }


    }

    private fun getListProduct() {
        productsUseCase.getProducts(productIds = PURCHASE_LIST)
            .addOnSuccessListener { products: List<Product> ->
                val list = arrayOfNulls<String>(4)
                products.forEach { product ->
                    when (product.productId) {
                        ONE_MONTH -> list[0] = product.title.toString()
                        SIX_MONTH -> list[1] = product.title.toString()
                        ONE_YEAR -> list[2] = product.title.toString()
                        FOREVER -> list[3] = product.title.toString()
                    }
                }
                DialogItemList.insertBilling(this@MainActivity, object : DialogItemList.ActionInt {
                    override fun onClick(action: Int) {
                        when (action) {
                            0 -> pokupka(ONE_MONTH)
                            1 -> pokupka(SIX_MONTH)
                            2 -> pokupka(ONE_YEAR)
                            3 -> pokupka(FOREVER)
                        }
                    }

                }, list)
            }
            .addOnFailureListener { throwable: Throwable ->
                when (throwable.message.toString()) {
                    NOT_AUTHORIZED ->
                        DialogItemList.openAuth(
                            this@MainActivity,
                            object : DialogItemList.ActionTrueOrFalse {
                                override fun onClick(flag: Boolean) {
                                    if (flag) {
                                        try {
                                            startActivity(
                                                Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse(AUTHORIZED_RUSTORE)
                                                )
                                            )
                                        } catch (e: Exception) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Ошибка",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            })

                    else ->
                        Toast.makeText(
                            this@MainActivity,
                            "Оплата временно недоступна",
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }

    } // Запрос списка продуктов

    private fun pokupka(billing: String) {
        purchasesUseCase.purchaseProduct(
            productId = billing,
            orderId = UUID.randomUUID().toString(),
            quantity = 1,
            developerPayload = null,
        ).addOnSuccessListener { paymentResult: PaymentResult ->
            when (paymentResult) {
                is PaymentResult.Success -> {
                    updatePremium(
                        true,
                        "Поздравляю! Теперь вам доступны PREMIUM функции"
                    )
                }

                else -> {
                    Toast.makeText(
                        this,
                        "Произошла ошибка оплаты",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    } // Покупка товара



    private fun stub(text: String) {
        Toast.makeText(this, "$text появятся в следующих обновлениях", Toast.LENGTH_SHORT).show()
    }

    private fun updatePremium(premium: Boolean, value: String) {
        modelFlashLight.savePremium(premium)
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
        if (premium) {
            binding.yaBaner.visibility = View.GONE
            binding.bBuyPremium.text = this@MainActivity.getString(R.string.premium_on)
        } else {
            binding.yaBaner.visibility = View.VISIBLE
            binding.bBuyPremium.text = this@MainActivity.getString(R.string.premium_off)
        }
    } // обновление ПРЕМИУМ версии
}