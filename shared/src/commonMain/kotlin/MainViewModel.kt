
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import domain.repostirory.SharedPrefRepository

class MainViewModel(
    private val pref: SharedPrefRepository
) : ViewModel() {



    var stateTextNotebook by mutableStateOf(pref.loadTextNoteBook())


    fun saveText() = pref.saveTextNoteBook(stateTextNotebook)


}