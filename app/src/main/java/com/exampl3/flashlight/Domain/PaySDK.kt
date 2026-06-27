package com.exampl3.flashlight.Domain

import android.content.Context
import com.exampl3.flashlight.Const.FOREVER
import com.exampl3.flashlight.Const.ONE_MONTH
import com.exampl3.flashlight.Const.ONE_YEAR
import com.exampl3.flashlight.Const.PURCHASE_LIST
import com.exampl3.flashlight.Const.SIX_MONTH
import com.exampl3.flashlight.Data.sharedPreference.SharedPreferenceImpl
import com.exampl3.flashlight.Presentation.DialogItemList
import ru.rustore.sdk.billingclient.RuStoreBillingClient
import ru.rustore.sdk.billingclient.RuStoreBillingClientFactory
import ru.rustore.sdk.billingclient.model.purchase.PurchaseState
import ru.rustore.sdk.pay.RuStorePayClient
import ru.rustore.sdk.pay.model.PreferredPurchaseType
import ru.rustore.sdk.pay.model.Product
import ru.rustore.sdk.pay.model.ProductId
import ru.rustore.sdk.pay.model.ProductPurchase
import ru.rustore.sdk.pay.model.ProductPurchaseParams
import ru.rustore.sdk.pay.model.ProductPurchaseStatus
import ru.rustore.sdk.pay.model.Purchase
import ru.rustore.sdk.pay.model.PurchaseId
import ru.rustore.sdk.pay.model.RuStorePaymentException
import ru.rustore.sdk.pay.model.SubscriptionPurchase
import ru.rustore.sdk.pay.model.SubscriptionPurchaseStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaySDK @Inject constructor(
    private val pref: SharedPreferenceImpl) {

     fun getAllListProducts(context: Context,resultPay: () -> Unit){
        RuStorePayClient.instance.getProductInteractor().getProducts(productsId = PURCHASE_LIST)
            .addOnSuccessListener { products: List<Product> ->
                val list = arrayOfNulls<String>(4)
                products.forEach { product ->
                    when (product.productId) {
                        ONE_MONTH -> list[0] = product.title.value
                        SIX_MONTH -> list[1] = product.title.value
                        ONE_YEAR -> list[2] = product.title.value
                        FOREVER -> list[3] = product.title.value
                    }
            }
                DialogItemList.insertBilling(context, object : DialogItemList.ActionInt {
                    override fun onClick(action: Int) {
                        when (action) {
                            0 -> byProduct(ONE_MONTH,context,resultPay)
                            1 -> byProduct(SIX_MONTH,context,resultPay)
                            2 -> byProduct(ONE_YEAR,context,resultPay)
                            3 -> byProduct(FOREVER,context,resultPay)
                        }
                    }

                }, list)
            }
            .addOnFailureListener { throwable: Throwable ->
                ToastFun(context, "Оплата временно недоступна")
            }
    }

    private fun byProduct(productId: ProductId,context: Context,result: () -> Unit){
        val params = ProductPurchaseParams(
            productId = productId,
            orderId = null,
            quantity = null,
            developerPayload = null,
            appUserId = null,
        )
        RuStorePayClient.instance.getPurchaseInteractor()
            .purchase(params = params, preferredPurchaseType = PreferredPurchaseType.ONE_STEP)
            .addOnSuccessListener {
                ToastFun(context, "Поздравляю! Теперь вам доступны PREMIUM функции")
                result()
            }
            .addOnFailureListener { throwable: Throwable ->
                getListShoppingProductWithMistake(context,result)


            }
    }
    private fun getListShoppingProductWithMistake(context: Context,result: () -> Unit){

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
        RuStorePayClient.instance.getPurchaseInteractor().getPurchases()
            .addOnSuccessListener { purchases: List<Purchase> ->

                val staseList = purchases.map { it.status }

                val hasActivePremiumInStore = staseList.contains(ProductPurchaseStatus.CONFIRMED) ||
                        staseList.contains(SubscriptionPurchaseStatus.ACTIVE)
                if(hasActivePremiumInStore) {
                    ToastFun(context, "Поздравляю! Теперь вам доступны PREMIUM функции")
                    result()
                }
                else ToastFun(context, "Вы отменили покупку")


            }
            .addOnFailureListener {
                ToastFun(context, "Произошла ошибка оплаты")
            }
        }, 3000)
    }

    fun getListShopingProductsNew(context: Context, result : (Boolean) -> Unit){

        val billingClient = RuStoreBillingClientFactory.create(
            context = context,
            consoleApplicationId = "2063541058",
            deeplinkScheme = "flashlight"
        )

        RuStorePayClient.instance.getPurchaseInteractor().getPurchases()
            .addOnSuccessListener { purchases: List<Purchase> ->

                val staseList = purchases.map { it.status }

                val hasActivePremiumInStore = staseList.contains(ProductPurchaseStatus.CONFIRMED) ||
                        staseList.contains(SubscriptionPurchaseStatus.ACTIVE)

                getListShopingProductsOld(billingClient){resultOld->
                    if(resultOld) {
                        result(true)
                    }
                    else{
                        // КЕЙС 1: Если в RuStore пусто (или подписка просрочена), а локально премиум ВКЛЮЧЕН -> ОТКЛЮЧАЕМ
                        if ((purchases.isEmpty() || !hasActivePremiumInStore) && pref.getPremium()) {
                            ToastFun(context, "PREMIUM версия была отключена")
                            result(false)
                            return@getListShopingProductsOld // Выходим, чтобы не пойти в код ниже
                        }

                        // КЕЙС 2: Если в RuStore есть активный премиум, но на устройстве он ВЫКЛЮЧЕН -> ВОССТАНАВЛИВАЕМ
                        if (hasActivePremiumInStore && !pref.getPremium()) {
                            ToastFun(context, "PREMIUM версия была восстановлена")
                            result(true)
                        }
                    }
                }


            }

    }
    fun getListShopingProductsOld(billingClient: RuStoreBillingClient, resultOld : (Boolean) -> Unit){
        val purchasesUseCase = billingClient.purchases
        purchasesUseCase.getPurchases()
            .addOnSuccessListener { purchases: List<ru.rustore.sdk.billingclient.model.purchase.Purchase> ->
                val staseList = purchases.map { it.purchaseState }

                if(staseList.isNotEmpty() && staseList.contains(PurchaseState.CONFIRMED)) resultOld(true)
                else resultOld(false)

            }

    }

}