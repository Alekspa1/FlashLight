package domain.repostirory

interface SharedPrefRepository {

    fun saveTextNoteBook(value: String)

    fun loadTextNoteBook() : String
}