package data.repostitory

import domain.repostirory.GetPlatrormRepository

class AndroidGetPlatrormImp : GetPlatrormRepository {
    override fun getPlatform(): String = "Android"
}