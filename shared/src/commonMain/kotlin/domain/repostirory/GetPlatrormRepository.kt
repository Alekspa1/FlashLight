package domain.repostirory

interface GetPlatrormRepository {

    fun getPlatform() : String
    suspend fun getAllSound () : Map<String, String>
    fun updateApp(result : (Boolean) -> Unit)
    fun openUpdateApp(errorMessage: (String) -> Unit)
}
