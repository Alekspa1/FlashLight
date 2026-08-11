package data.repository

import domain.repostirory.GetPlatrormRepository

class IosGetPlatformImpl : GetPlatrormRepository {
    override fun getPlatform(): String = "Ios"
    override suspend fun getAllSound(): Map<String, String> {
        return mapOf("" to "")
    }

    override fun updateApp(result: (Boolean) -> Unit) {
        result(false)
    }
}