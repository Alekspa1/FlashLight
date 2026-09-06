package com.dragon.shared.data.repostitory

import CommonConst.PLATFORM_DESKTOP
import domain.repostirory.GetPlatrormRepository

class DesktopGetPlatformImpl : GetPlatrormRepository {
    override fun getPlatform(): String = PLATFORM_DESKTOP
    override suspend fun getAllSound(): Map<String, String> {
        return mapOf("" to "")
    }

    override fun updateApp(result: (Boolean) -> Unit) {
        result(false)
    }
}