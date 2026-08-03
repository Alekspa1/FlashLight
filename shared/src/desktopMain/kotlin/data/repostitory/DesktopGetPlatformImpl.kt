package com.dragon.shared.data.repostitory

import domain.repostirory.GetPlatrormRepository

class DesktopGetPlatformImpl : GetPlatrormRepository {
    override fun getPlatform(): String = "Desktop"
}