package domain.repostirory

interface GetPlatrormRepository {

    fun getPlatform() : String
    suspend fun getAllSound () : Map<String, String>
}