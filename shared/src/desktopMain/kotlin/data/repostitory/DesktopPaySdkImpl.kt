package com.dragon.shared.data.repostitory

import domain.model.ProductCommon
import domain.repostirory.PaySdkRepository

class DesktopPaySdkImpl : PaySdkRepository {
    override suspend fun getAllProduct(): Result<List<ProductCommon>> {
        return Result.success(emptyList())
    }

    override suspend fun byProduct(productId: String) : Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun isChekedSubcrition(): Boolean {
        return false
    }

}