package domain.repostirory

interface SaveDeleteImageRepositpry {

    fun save(temporaryPathString: String, fileName: String)
    fun delete (fileName: String)
    fun getUri(fileName: String) : String
}