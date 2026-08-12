package data.repository

import domain.model.ProductCommon
import domain.repostirory.PaySdkRepository

class IosPaySdkImpl : PaySdkRepository {
    override suspend fun getAllProduct(): Result<List<ProductCommon>> {
        return Result.success(emptyList())
    }

    override suspend fun byProduct(productId: String) : Result<Boolean> {
        return Result.success(true)
    }

    override suspend fun isChekedSubcrition(): Result<Boolean>{
        return Result.success(true)
    }

}