
import CommonConst.SORT_STANDART
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.room.CourseDao
import data.room.Item
import domain.repostirory.SharedPrefRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import presentation.dialogs.DeleteDialog
import presentation.dialogs.DialogState

class MainViewModel(
    private val pref: SharedPrefRepository,
    private val db: CourseDao
) : ViewModel() {



    var stateTextNotebook by mutableStateOf(pref.loadTextNoteBook())
    var showDialog by  mutableStateOf(DialogState())
    //private val _sortType = MutableStateFlow(settingsPref.getSort())
    private val _sortType = MutableStateFlow(SORT_STANDART)
    val sortType = _sortType.asStateFlow()

    private val _categoryItemFlow = MutableStateFlow("Повседневные")
    val categoryItemFlow = _categoryItemFlow.asStateFlow()

     val sortedItemsFlow = combine(
        db.getAll(), // Поток всех дел
        sortType,                         // Поток типа сортировки
        categoryItemFlow                  // Поток выбранной категории
    ) { list, sort, currentCategory ->

        // 1. СНАЧАЛА ФИЛЬТРУЕМ СПИСОК: оставляем только дела из выбранной категории
        val filteredList = list.filter { it.category == currentCategory }

        // 2. ЗАТЕМ СОРТИРУЕМ ОТФИЛЬТРОВАННЫЙ СПИСОК
        if (sort == SORT_STANDART) {
            filteredList.sortedWith(
                compareBy<Item> { it.change }
                    .thenBy { if (it.alarmTime > 0L) 0 else 1 }
                    .thenByDescending { it.alarmTime }
                    .thenBy { it.sort }
            )
        } else {
            filteredList.sortedBy { it.sort }
        }
    }.flowOn(Dispatchers.Default)
        .stateIn(
        scope = viewModelScope, // Корутина привязана к жизни ViewModel
        started = SharingStarted.WhileSubscribed(5000), // Бережёт оперативку и батарейку
        initialValue = emptyList() // Пока база данных считывается с диска, отдаем пустой список
    )

    fun saveText() = pref.saveTextNoteBook(stateTextNotebook)


    fun insertitem(){
        viewModelScope.launch { db.insertItem(Item(null,"тестовое дело",true,"",0,true,true,0,"Повседневные","",0)) }
    }



}