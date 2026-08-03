package domain.repostirory

interface SharedPrefRepository {

    fun saveTextNoteBook(value: String)

    fun loadTextNoteBook() : String

    fun getPremium(): Boolean

    fun savePremium(flag: Boolean)
}