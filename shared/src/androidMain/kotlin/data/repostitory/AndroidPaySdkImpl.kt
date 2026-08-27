package data.repostitory

import CommonConst.FOREVER
import CommonConst.ONE_MONTH
import CommonConst.ONE_YEAR
import CommonConst.SIX_MONTH
import android.content.Context
import domain.model.ProductCommon
import domain.repostirory.PaySdkRepository
import domain.repostirory.SharedPrefRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import ru.rustore.sdk.billingclient.RuStoreBillingClient
import ru.rustore.sdk.billingclient.RuStoreBillingClientFactory
import ru.rustore.sdk.billingclient.model.purchase.PurchaseState
import kotlin.collections.forEach
import ru.rustore.sdk.pay.RuStorePayClient
import ru.rustore.sdk.pay.model.PreferredPurchaseType
import ru.rustore.sdk.pay.model.Product
import ru.rustore.sdk.pay.model.ProductId
import ru.rustore.sdk.pay.model.ProductPurchaseParams
import ru.rustore.sdk.pay.model.ProductPurchaseStatus
import ru.rustore.sdk.pay.model.Purchase
import ru.rustore.sdk.pay.model.SubscriptionPurchaseStatus
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import ru.rustore.sdk.pay.model.UserAuthorizationStatus



class AndroidPaySdkImpl(private val pref: SharedPrefRepository, private val context: Context) : PaySdkRepository {

    override suspend fun getAllProduct(): Result<List<ProductCommon>>  = suspendCancellableCoroutine { continuation ->
        val PURCHASE_LIST: List<ProductId> = listOf(
            ProductId(FOREVER) ,
            ProductId(ONE_MONTH),
            ProductId(SIX_MONTH),
            ProductId(ONE_YEAR),
        )

        RuStorePayClient.instance.getProductInteractor().getProducts(productsId = PURCHASE_LIST)
            .addOnSuccessListener { products: List<Product> ->
                val list = mutableListOf<ProductCommon>()

                products.forEach { product ->
                    list.add(ProductCommon(
                        name = product.title.value,
                        desc = product.description?.value ?: "",
                        productId = product.productId.value,
                        price = (product.price?.value?.div(100)) ?: 0
                    ))
                }

                val sortedList = list.sortedBy { product ->
                    when (product.productId) {
                        ONE_MONTH -> 0
                        SIX_MONTH -> 1
                        ONE_YEAR -> 2
                        FOREVER -> 3
                        else -> 4 // Страховка для неизвестных ID
                    }
                }
                if (continuation.isActive) {
                    continuation.resume(Result.success(sortedList))
                }
            }
            .addOnFailureListener { throwable: Throwable ->
                if (continuation.isActive) {
                    continuation.resume(Result.failure(throwable))
                }
            }
    }

    override suspend fun byProduct(productId: String) : Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val params = ProductPurchaseParams(
            productId = ProductId(productId),
            orderId = null,
            quantity = null,
            developerPayload = null,
            appUserId = null,
        )
        RuStorePayClient.instance.getPurchaseInteractor()
            .purchase(params = params, preferredPurchaseType = PreferredPurchaseType.ONE_STEP)
            .addOnSuccessListener {resultBuy ->
                if(continuation.isActive){
                    continuation.resume(Result.success(true))
                }
            }
            .addOnFailureListener { throwable: Throwable ->
                CoroutineScope(continuation.context).launch {
                if(continuation.isActive) {
                    if (getListShoppingProductWithMistake()) {
                        continuation.resume(Result.success(true))
                    } else continuation.resume(Result.failure(throwable))

                }
                }


            }
    }

    suspend fun getListShoppingProductWithMistake(): Boolean {
        // 1. Ждем 3 секунды на KMP-корутинах, пока транзакция RuStore обновится на сервере
        delay(3000)

        // 2. Только после паузы делаем запрос в историю покупок
        return suspendCancellableCoroutine { continuation ->
            RuStorePayClient.instance.getPurchaseInteractor().getPurchases()
                .addOnSuccessListener { purchases: List<Purchase> ->
                    val stateList = purchases.map { it.status }

                    val hasActivePremiumInStore = stateList.contains(ProductPurchaseStatus.CONFIRMED) ||
                            stateList.contains(SubscriptionPurchaseStatus.ACTIVE)

                    if (continuation.isActive) {
                        continuation.resume(hasActivePremiumInStore) // Возвращаем true или false
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(false) // При ошибке сети в истории возвращаем false
                    }
                }
        }
    }

    override suspend fun isChekedSubcrition(): Result<Boolean> {
        if(isAuthorizationInRustore()){
    return try {
        val billingClient = RuStoreBillingClientFactory.create(
            context = context,
            consoleApplicationId = "2063541058",
            deeplinkScheme = "flashlight"
        )

        // 1. Запрашиваем покупки из нового SDK 
        val newPurchases = getNewPurchases()
        val newStatuses = newPurchases.map { it.status }
        val hasActivePremiumInStore = newStatuses.contains(ProductPurchaseStatus.CONFIRMED) || 
                                      newStatuses.contains(SubscriptionPurchaseStatus.ACTIVE)

        // 2. Запрашиваем покупки из старого SDK (ОБЕРНУЛИ В ЛОКАЛЬНЫЙ TRY-CATCH)
        val oldPurchases = try {
            getOldPurchases(billingClient)
        } catch (e: Exception) {
            // Если старый SDK упал — бог с ним, считаем, что там пусто, 
            // но не ломаем работу нового SDK!
            emptyList() 
        }
        
        val oldStatuses = oldPurchases.map { it.purchaseState }
        val hasOldPremium = oldStatuses.contains(PurchaseState.CONFIRMED)

        // 3. Твои кейсы синхронизации (работают как часы)
        if (hasOldPremium) {
            return Result.success(true)
        }

        val isLocalPremiumActive = pref.getPremium()

        // КЕЙС 1: Если в RuStore пусто, а локально премиум включен -> ОТКЛЮЧАЕМ
        if ((newPurchases.isEmpty() || !hasActivePremiumInStore) && isLocalPremiumActive) {
            return Result.success(false)
        }

        // КЕЙС 2: Если в RuStore есть активный премиум, но локально выключен -> ВОССТАНАВЛИВАЕМ
        if (hasActivePremiumInStore && !isLocalPremiumActive) {
            return Result.success(true)
        }

        // Финальный дефолтный кейс
       return Result.success(hasActivePremiumInStore)

    } catch (throwable: Throwable) {
        Result.failure(throwable)
    }
    } else return Result.failure(Exception("Не авторизован"))
}

   suspend fun isAuthorizationInRustore() : Boolean = suspendCancellableCoroutine { continuation ->
    RuStorePayClient.instance.getUserInteractor().getUserAuthorizationStatus()
    .addOnSuccessListener { result ->
        when (result) {
            UserAuthorizationStatus.AUTHORIZED -> {
                 continuation.resume(true)
            }
 
            UserAuthorizationStatus.UNAUTHORIZED -> {
                continuation.resume(false)
            }
        }
    }.addOnFailureListener { throwable ->
            continuation.resume(false)
    }

       
    }

    private suspend fun getNewPurchases(): List<Purchase> = suspendCancellableCoroutine { continuation ->
    RuStorePayClient.instance.getPurchaseInteractor().getPurchases()
        .addOnSuccessListener { purchases -> continuation.resume(purchases) }
        .addOnFailureListener { throwable -> continuation.resumeWithException(throwable) }
}


private suspend fun getOldPurchases(billingClient: RuStoreBillingClient): List<ru.rustore.sdk.billingclient.model.purchase.Purchase> = suspendCancellableCoroutine { continuation ->
    billingClient.purchases.getPurchases()
        .addOnSuccessListener { purchases -> continuation.resume(purchases) }
        .addOnFailureListener { throwable -> continuation.resumeWithException(throwable) }
}

   // override  suspend fun isChekedSubcrition() : Result<Boolean>  = suspendCancellableCoroutine { continuation ->

   //      val billingClient = RuStoreBillingClientFactory.create(
   //          context = context,
   //          consoleApplicationId = "2063541058",
   //          deeplinkScheme = "flashlight"
   //      )

   //      RuStorePayClient.instance.getPurchaseInteractor().getPurchases()
   //          .addOnSuccessListener { purchases: List<Purchase> ->

   //              val staseList = purchases.map { it.status }

   //              val hasActivePremiumInStore = staseList.contains(ProductPurchaseStatus.CONFIRMED) ||
   //                      staseList.contains(SubscriptionPurchaseStatus.ACTIVE)

   //              CoroutineScope(continuation.context).launch {

   //                  getListShopingProductsOld(billingClient)
   //                      .onSuccess {resut->
   //                          if (resut) {
   //                              if (continuation.isActive) continuation.resume(Result.success(true))
   //                              return@launch
   //                          } else {
   //                              // КЕЙС 1: Если в RuStore пусто (или подписка просрочена), а локально премиум ВКЛЮЧЕН -> ОТКЛЮЧАЕМ
   //                              if ((purchases.isEmpty() || !hasActivePremiumInStore) && pref.getPremium()) {
   //                                  if (continuation.isActive) continuation.resume(Result.success(false))
   //                                  return@launch // Выходим, чтобы не пойти в код ниже
   //                              }

   //                              // КЕЙС 2: Если в RuStore есть активный премиум, но на устройстве он ВЫКЛЮЧЕН -> ВОССТАНАВЛИВАЕМ
   //                              if (hasActivePremiumInStore && !pref.getPremium()) {
   //                                  //  ToastFun(context, "PREMIUM версия была восстановлена")
   //                                  if (continuation.isActive) continuation.resume(Result.success(true))
   //                                  return@launch
   //                              }

   //                              if (continuation.isActive) {
   //                                  continuation.resume(Result.success(hasActivePremiumInStore))
   //                              }
   //                          }
   //                      }
   //                      .onFailure {throwable-> continuation.resume(Result.failure(throwable)) }

   //              }
   //          }
   //          .addOnFailureListener { throwable ->
   //              if (continuation.isActive) {
   //                  continuation.resume(Result.failure(throwable))
   //              }
   //          }


   //          }
   //  suspend fun getListShopingProductsOld(billingClient: RuStoreBillingClient) : Result<Boolean> = suspendCancellableCoroutine { continuation ->
   //      val purchasesUseCase = billingClient.purchases
   //      purchasesUseCase.getPurchases()
   //          .addOnSuccessListener { purchases: List<ru.rustore.sdk.billingclient.model.purchase.Purchase> ->
   //              val staseList = purchases.map { it.purchaseState }

   //              if (staseList.isNotEmpty() && staseList.contains(PurchaseState.CONFIRMED)) {
   //                  if (continuation.isActive) continuation.resume(Result.success(true))
   //              } else {
   //                  if (continuation.isActive) continuation.resume(Result.success(false))
   //              }
   //          }
   //          .addOnFailureListener {throwable ->
   //              // Обязательно добавляем слушатель ошибки, чтобы при сбое сети корутина тоже отпускала поток!
   //              if (continuation.isActive) continuation.resume(Result.failure(throwable))

   //          }
   //  }
    }


