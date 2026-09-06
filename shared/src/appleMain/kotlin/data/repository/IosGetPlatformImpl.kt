package data.repository

import domain.repostirory.GetPlatrormRepository
import CommonConst.PLATFORM_IOS
class IosGetPlatformImpl : GetPlatrormRepository {

    override fun getPlatform(): String = PLATFORM_IOS

    override suspend fun getAllSound(): Map<String, String> {
        return mapOf("" to "")
    }

    override fun updateApp(result: (Boolean) -> Unit) {
        result(false)
    }
}