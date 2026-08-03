package data.repository

import domain.repostirory.GetPlatrormRepository

class IosGetPlatformImpl : GetPlatrormRepository {
    override fun getPlatform(): String = "Ios"
}