
import CommonConst.ALARM_ONE
import CommonConst.NOTIFICATION
import CommonConst.SORT_STANDART
import CommonConst.SORT_USER
import CommonConst.TIME
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Transaction
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
import presentation.dialogs.DialogState
import kotlin.time.Clock
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import presentation.theme.SizeNormal
import presentation.theme.Theme
import presentation.theme.ThemeNeon

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

    var premiumState = MutableStateFlow(true)
    var updateState by mutableStateOf(false)
    var themeState by mutableStateOf<Theme>(ThemeNeon())
    var sizeState by mutableStateOf(SizeNormal())



    //private val _sortType = MutableStateFlow(settingsPref.getSort())
    private val _sortType = MutableStateFlow(SORT_USER)
    val sortType = _sortType.asStateFlow()

    private val _categoryItemFlow = MutableStateFlow("Повседневные")
    val categoryItemFlow = _categoryItemFlow.asStateFlow()

    val getItemsInCalendar = db.getItemsInCalendar()
    val categories: StateFlow<List<ListCategory>> = db.getAllListCategory()
    .stateIn(
        scope = viewModelScope, // Привязываем к жизненному циклу ViewModel
        started = SharingStarted.WhileSubscribed(5000), // Засыпает через 5 сек после закрытия экрана
        initialValue = emptyList() // Начальное значение, пока база грузится
    )
    val getItemCalendarCombine = combine(getItemsInCalendar, premiumState) { list, premium ->
        if (premium) list
        else emptyList()
    }
    fun getUri(fileName :  String) = image.getUri(fileName)
    fun saveImage(temporaryPathString: String, fileName: String) {
        // Убираем блокировку главного потока, переключаясь на дисковый Dispatchers.IO
        viewModelScope.launch(Dispatchers.IO) {
            image.save(temporaryPathString, fileName)
        }
    }

    val spinnerCategories: StateFlow<List<String>> = categories
        .map { listFromDb ->
            listOf("Повседневные") + listFromDb.map { it.name }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf("Повседневные") // Чтобы на старте spinner не был пустым
        )

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


        private val _uiItemsState = MutableStateFlow<List<Item>>(emptyList())
val uiItemsState = _uiItemsState.asStateFlow()

init {
    // 2. Связываем поток из БД с нашим UI-стейтом
    viewModelScope.launch {
        sortedItemsFlow.collect { itemsFromDb ->
            _uiItemsState.value = itemsFromDb
        }
    }
}

// 3. Метод для плавной анимации карточек в памяти во время перетаскивания
fun updateListInUi(newList: List<Item>) {
    _uiItemsState.value = newList
}

// 4. Твой нативный метод, доработанный для перезаписи поля sort
fun updateItemsOrderInDb(finalList: List<Item>) {
    viewModelScope.launch(Dispatchers.IO) {
        try {
            // Перезаписываем поле sort на основе нового положения элементов
            val listWithUpdatedSort = finalList.mapIndexed { index, item ->
                item.copy(sort = index) 
            }
            // Записываем в Room одной транзакцией
            db.CourseDao().updateItemsOrder(listWithUpdatedSort)  
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

    fun saveText() = pref.saveTextNoteBook(stateTextNotebook)

    fun updateCategory(value: String) {
        _categoryItemFlow.value = value
    }


    fun insertItem(item: Item, alarm: Boolean = false,calendar: Boolean){

        viewModelScope.launch(Dispatchers.IO) {

            val currentMinSort = db.getItemWithMinSort()?.sort ?: 0
            val newSortIndex = currentMinSort - 1
            val newItem = item.copy(sort = newSortIndex)

            val insertedId = db.insertItem(newItem)
            withContext(Dispatchers.Main){
                if(alarm) {
                    val savedItem = newItem.copy(id = insertedId.toInt())
                    permission(NOTIFICATION, savedItem,calendar)
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

    fun updateItem(item: Item,alarm: Boolean = false,calendar: Boolean = false){
        viewModelScope.launch(Dispatchers.IO) {
            db.updateItem(item)
            withContext(Dispatchers.Main){
                if(alarm) permission(NOTIFICATION, item, calendar)
                else showDialog = DialogState()
            }

        }
    }

    fun permission(permissionName: String, item: Item,calendar: Boolean = false) {
        viewModelScope.launch{
         val isChekedPermission = permission.isChekedPermission(permissionName)
    
        if(isChekedPermission) {
            val dialog = if(calendar) TIME else NOTIFICATION
            sendMessage(dialog)
            showDialog = DialogState(dialog,item) }
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
                        if (!premiumState.value) {
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

    suspend fun getAllCategories(item: Item?, calendar: Boolean) : List<String> {
        val listCategory = mutableListOf("Повседневные")
        listCategory.addAll(db.getAllCategories())
        if (!calendar) {
            if (item == null) {
                val currentCategory = categoryItemFlow.value
                listCategory.remove(currentCategory)
                listCategory.add(0, currentCategory)
            } else {
                listCategory.remove(item.category)
                listCategory.add(0, item.category)
            }
        } else {
            if (item != null) {
                listCategory.remove(item.category)
                listCategory.add(0, item.category)
            }

        }
        return listCategory
    }

    suspend fun getAllCategoriesTwo() : List<String> {
        val listCategory = mutableListOf("Повседневные")
        listCategory.addAll(db.getAllCategories())
        return listCategory
    }

    fun insertCategory(name: String) {
        viewModelScope.launch {
            if (isCategoryNameExists(name) || name == "Повседневные" || name ==  "Общие дела") sendMessage("Такая категория уже есть")
            else db.insertCategory(ListCategory(null, name))
        }
    }

    fun upgrateListCategory(category: ListCategory, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newitem = category.copy(name = name)
            if (isCategoryNameExists(name) || name == "Повседневные" || name ==  "Общие дела") sendMessage("Такая категория уже есть")
            else {
                db.updateCategory(newitem)
                db.updateAllitemInCategory(name,category.name)
            }
        }
    }


    fun deleteCategory(category: ListCategory){
        viewModelScope.launch(Dispatchers.IO) {
            db.deleteItemInCategory(category.name) // удаляю все из бд
            db.deleteCategoryMenu(category) // удаляю из меню
        }
    }

    private suspend fun isCategoryNameExists(name: String): Boolean {
        return try {
            val count = db.isCategoryExists(name)
            count > 0
        } catch (e: Exception) {
            false
        }
    }




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
