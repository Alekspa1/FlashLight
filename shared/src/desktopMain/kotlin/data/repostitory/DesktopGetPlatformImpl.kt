package com.dragon.shared.data.repostitory

import domain.repostirory.GetPlatrormRepository

class DesktopGetPlatformImpl : GetPlatrormRepository {
    override fun getPlatform(): String = "Desktop"
    override suspend fun getAllSound(): Map<String, String> {
        return mapOf("" to "")
    }

    override fun updateApp(result: (Boolean) -> Unit) {
        result(false)
    }
}