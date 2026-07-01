
import CommonConst.SORT_STANDART
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.room.CourseDao
import data.room.Item
import domain.repostirory.DeleteImageInItemReository
import domain.repostirory.SharedPrefRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import presentation.dialogs.DialogState

class MainViewModel(
    private val pref: SharedPrefRepository,
    private val db: CourseDao,
    private val deleteImageInitem: DeleteImageInItemReository
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


    fun insertitem(item: Item){
        viewModelScope.launch { db.insertItem(item) }
    }
    fun deleteitem(item: Item){
        viewModelScope.launch {
            db.delete(item)
            deleteImageInitem.delete(item.uri)
        }
    }

    fun updateitem(item: Item){
        viewModelScope.launch {
            db.updateItem(item.copy(change = !item.change))
        }
    }



}