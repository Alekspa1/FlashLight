
import CommonConst.ALARM_ONE
import CommonConst.NOTIFICATION
import CommonConst.SORT_STANDART
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.room.CourseDao
import data.room.Item
import data.room.ListCategory
import domain.repostirory.AlarmRepeadRepository
import domain.repostirory.AlarmRepository
import domain.repostirory.DeleteImageInItemReository
import domain.repostirory.PermissionRepository
import domain.repostirory.SaveDeleteImageRepositpry
import domain.repostirory.SharedPrefRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import presentation.dialogs.DialogState
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(
    private val pref: SharedPrefRepository,
    private val db: CourseDao,
    val deleteImageInitem: DeleteImageInItemReository,
    private val permission: PermissionRepository,
    private val alarm: AlarmRepository,
    private val alarmRepeat: AlarmRepeadRepository,
    private val image: SaveDeleteImageRepositpry
) : ViewModel() {



    var stateTextNotebook by mutableStateOf(pref.loadTextNoteBook())
    var showDialog by  mutableStateOf(DialogState())
    var _toast = MutableSharedFlow<String>()
    var toast = _toast.asSharedFlow()

    //private val _sortType = MutableStateFlow(settingsPref.getSort())
    private val _sortType = MutableStateFlow(SORT_STANDART)
    val sortType = _sortType.asStateFlow()

    private val _categoryItemFlow = MutableStateFlow("Повседневные")
    val categoryItemFlow = _categoryItemFlow.asStateFlow()

    val categories: StateFlow<List<ListCategory>> = db.getAllListCategory()
    .stateIn(
        scope = viewModelScope, // Привязываем к жизненному циклу ViewModel
        started = SharingStarted.WhileSubscribed(5000), // Засыпает через 5 сек после закрытия экрана
        initialValue = emptyList() // Начальное значение, пока база грузится
    )
    fun getUri(fileName :  String) = image.getUri(fileName)
    fun saveImage(temporaryPathString: String, fileName: String) {
        // Убираем блокировку главного потока, переключаясь на дисковый Dispatchers.IO
        viewModelScope.launch(Dispatchers.IO) {
            image.save(temporaryPathString, fileName)
        }
    }
    fun deleteImage(fileName: String) = image.delete(fileName)

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
                 compareBy<Item> { if (it.changeAlarm) 0 else 1 } // 1. Сначала ВСЕ с активным будильником (желтые)
                     .thenBy { if (it.change) 1 else 0 }          // 2. ОПУСКАЕМ ЗЕЛЕНЫЕ: сначала незавершенные (0), выполненные (1) вниз!
                     .thenBy { it.alarmTime }                     // 3. Сортируем будильники по времени
                     .thenBy { it.sort }                          // 4. Стандартная сортировка для остальных
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
    fun updateCategory(value: String) {
        _categoryItemFlow.value = value
    }


    fun insertItem(item: Item, alarm: Boolean = false){

        viewModelScope.launch(Dispatchers.IO) {

            val currentMinSort = db.getItemWithMinSort()?.sort ?: 0
            val newSortIndex = currentMinSort - 1
            val newItem = item.copy(sort = newSortIndex)

            val insertedId = db.insertItem(newItem)
            withContext(Dispatchers.Main){
                if(alarm) {
                    val savedItem = newItem.copy(id = insertedId.toInt())
                    permission(NOTIFICATION, savedItem)
                } else showDialog = DialogState()
            }

        }
    }
    fun deleteItem(item: Item){
        viewModelScope.launch(Dispatchers.IO) {
            db.delete(item)
            deleteImage(item.uri)
            deleteAlarm(item.id)
        }
    }

    fun updateItem(item: Item,alarm: Boolean = false){
        viewModelScope.launch(Dispatchers.IO) {
            db.updateItem(item)
            withContext(Dispatchers.Main){
                if(alarm) permission(NOTIFICATION, item)
                else showDialog = DialogState()
            }

        }
    }

    fun permission(permissionName: String, item: Item) {
        viewModelScope.launch{
         val isChekedPermission = permission.isChekedPermission(permissionName)
    
        if(isChekedPermission) { showDialog = DialogState(permissionName,item) }
        else {
            val isGranted = permission.requestPermission(permissionName)
            if(isGranted){
                showDialog = DialogState(permissionName,item)

            }
            else {sendMessage("Для стабильной работы, необходимо дать разрешение")}
            
        }   
        }
        
    }

    fun insertAlarm(item: Item){
        alarm.createAlarm(item)
    }

    fun deleteAlarm(id: Int){
        alarm.deleteAlarm(id)
    }

    fun insertAlarmRepeat(item: Item){
        viewModelScope.launch(Dispatchers.IO) {
            if (item.changeAlarm) {
              deleteAlarm(item.id)
                db.updateItem(item.copy(changeAlarm = false))
            }

            if ((item.change || !item.changeAlarm) && item.alarmTime > currentTime()) {
                val newItem = item.copy(change = false, changeAlarm = !item.changeAlarm)
                insertAlarm(newItem)
                db.updateItem(newItem)
            }
            if (!item.changeAlarm && item.alarmTime < currentTime()) {
                alarmRepeat.alarmRepead(item.id){message -> sendMessage(message)}
            }

        }

    }

    private suspend fun listItem(calendaZero: Long): List<Item> {
        return db.getUpdateItemRestartPhone(calendaZero).filter { it.changeAlarm }
    }

    fun updateAlarm() {
        viewModelScope.launch(Dispatchers.IO) {
            listItem(currentTime()).forEach { item ->
                when (item.interval) {
                    ALARM_ONE -> {
                        insertAlarm(item)
                    }

                    else -> {
                        if (!getPremium()) {
                            deleteAlarm(item.id)
                            updateItem(item.copy(changeAlarm = false))
                        } else {
                            insertAlarm(item)
                        }

                    }
                }
            }
        }
    }



    fun getPremium() = true

    private fun currentTime():Long {
        val nowInstant = Clock.System.now()
        val currentMillis: Long = nowInstant.toEpochMilliseconds()
        return currentMillis

    }
    

    fun sendMessage(value: String){
        viewModelScope.launch {
            _toast.emit(value)
        }
    }



}
