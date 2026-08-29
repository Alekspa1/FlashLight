package domain.repostirory

import domain.model.ProductCommon

interface PaySdkRepository {

    suspend fun getAllProduct() : Result<List<ProductCommon>>
    suspend fun byProduct(productId: String) : Result<Boolean>

    suspend fun isChekedSubcrition() : Result<Boolean>
    fun checkAuthorizationInRustore (onResult: (Boolean) -> Unit)

}
