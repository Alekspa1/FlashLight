
import CommonConst.ALARM_ONE
import CommonConst.DEFAULT_DIALOG
import CommonConst.NOTIFICATION
import CommonConst.SORT_STANDART
import CommonConst.TIME
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.room.CourseDao
import data.room.model.Item
//import data.room.model.SubItem
import data.room.model.ListCategory
import domain.repostirory.AlarmRepeadRepository
import domain.repostirory.AlarmRepository

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
import presentation.theme.ThemeNeon
import domain.repostirory.SettingsAppRepository

import CommonConst.THEME_FUTURE
import CommonConst.THEME_ZABOR

import CommonConst.SIZE_SMALL
import CommonConst.SIZE_STANDART
import CommonConst.SIZE_LARGE

import CommonConst.ALARM_SETTINGS
import data.room.model.SubItem
import domain.repostirory.GetPlatrormRepository
import kotlinx.coroutines.flow.Flow

import presentation.theme.ThemeZabor
import presentation.theme.SizeSmall
import presentation.theme.SizeLarge
import data.room.model.ItemWithSubItems

class MainViewModel(
    private val pref: SharedPrefRepository,
    private val db: CourseDao,
    private val settingsPref : SettingsAppRepository,

    private val permission: PermissionRepository,
    private val alarm: AlarmRepository,
    private val alarmRepeat: AlarmRepeadRepository,
    private val image: SaveDeleteImageRepositpry,
    private val platform : GetPlatrormRepository,
) : ViewModel() {

    private val _soundState = MutableStateFlow<Map<String, String>>(emptyMap())
    val soundState = _soundState.asStateFlow()

    init {
        loadSounds()
    }

     fun loadSounds() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = platform.getAllSound()
            _soundState.value = result // Обновляем состояние (это безопасно)
        }
    }

    var stateTextNotebook by mutableStateOf(pref.loadTextNoteBook())
    var showDialog by  mutableStateOf(DialogState())
    private var _toast = MutableSharedFlow<String>()
    var toast = _toast.asSharedFlow()

  // private val _premiumState = MutableStateFlow(pref.getPremium())
   private val _premiumState = MutableStateFlow(true)
   var premiumState = _premiumState.asStateFlow()

    private val _updateState = MutableStateFlow(false)
    var updateState = _updateState.asStateFlow()

    private val _sortType = MutableStateFlow(settingsPref.getSort())
    val sortType = _sortType.asStateFlow()
    
    var themeState by mutableStateOf(
        when (settingsPref.getTheme()) {
            THEME_FUTURE -> ThemeNeon()
            THEME_ZABOR -> ThemeZabor()
            else -> ThemeNeon()
        }
    )

    val getPlatform = platform.getPlatform()
    
    var sizeState by mutableStateOf(
        when (settingsPref.getSize()) {
            SIZE_SMALL -> SizeSmall()
            SIZE_STANDART -> SizeNormal()
            SIZE_LARGE -> SizeLarge()
            else -> SizeNormal()
        }
    )
   
    fun saveTheme(value: String){
    settingsPref.saveTheme(value)
    
    when(value){
    THEME_FUTURE ->{themeState = ThemeNeon()}
    THEME_ZABOR -> {themeState = ThemeZabor()}   
    }
    }
    fun getTheme() = settingsPref.getTheme()
    fun getSize() = settingsPref.getSize()

    fun saveSort(sort: String) {
        settingsPref.saveSort(sort)
        _sortType.value = sort
    }

     fun saveSize(size: String) {
        settingsPref.saveSize(size)
        when(size){
         SIZE_SMALL -> {sizeState = SizeSmall()} 
         SIZE_STANDART -> {sizeState = SizeNormal()} 
         SIZE_LARGE -> {sizeState = SizeLarge()} 
         else -> sizeState = SizeNormal()   
    }
    }

    fun savePremium(value: Boolean){
        pref.savePremium(value)
        _premiumState.value = value
        }
    fun getPremium() = pref.getPremium()
    
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

val sortedItemsFlow: StateFlow<List<ItemWithSubItems>> = combine(
    db.getAll(),
    db.getAllSubItems(),
    sortType,
    categoryItemFlow
) { itemsList, allSubItems, sort, currentCategory ->
    val filteredList = itemsList.filter { it.category == currentCategory }

    val sortedList = if (sort == SORT_STANDART) {
        filteredList.sortedWith(
            compareBy<Item> { if (it.changeAlarm) 0 else 1 }
                .thenBy { if (it.change) 1 else 0 }
                .thenBy { it.alarmTime }
                .thenBy { it.sort }
        )
    } else {
        filteredList.sortedBy { it.sort }
    }

    sortedList.map { item ->
        ItemWithSubItems(
            item = item,
            subItems = allSubItems.filter { it.idTask == item.id }.sortedBy { it.sort }
        )
    }
}.flowOn(Dispatchers.Default)
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    //  val sortedItemsFlow = combine(
    //     db.getAll(), // Поток всех дел
    //     sortType,                         // Поток типа сортировки
    //     categoryItemFlow                  // Поток выбранной категории
    // ) { list, sort, currentCategory ->

    //     // 1. СНАЧАЛА ФИЛЬТРУЕМ СПИСОК: оставляем только дела из выбранной категории
    //     val filteredList = list.filter { it.category == currentCategory }

    //     // 2. ЗАТЕМ СОРТИРУЕМ ОТФИЛЬТРОВАННЫЙ СПИСОК
    //      if (sort == SORT_STANDART) {
    //          filteredList.sortedWith(
    //              compareBy<Item> { if (it.changeAlarm) 0 else 1 } // 1. Сначала ВСЕ с активным будильником (желтые)
    //                  .thenBy { if (it.change) 1 else 0 }          // 2. ОПУСКАЕМ ЗЕЛЕНЫЕ: сначала незавершенные (0), выполненные (1) вниз!
    //                  .thenBy { it.alarmTime }                     // 3. Сортируем будильники по времени
    //                  .thenBy { it.sort }                          // 4. Стандартная сортировка для остальных
    //          )
    //      } else {
    //         filteredList.sortedBy { it.sort }
    //     }
    // }.flowOn(Dispatchers.Default)
    //     .stateIn(
    //     scope = viewModelScope, // Корутина привязана к жизни ViewModel
    //     started = SharingStarted.WhileSubscribed(5000), // Бережёт оперативку и батарейку
    //     initialValue = emptyList() // Пока база данных считывается с диска, отдаем пустой список
    // )

            fun updateItemsOrder(newList: List<Item>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.updateItemsOrder(newList)  // Один запрос, одна транзакция
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

        fun updateSubItemsOrder(newList: List<SubItem>) {
    viewModelScope.launch(Dispatchers.IO) {
        try {
            // Room запишет обновленные индексы sort для подзадач в одну транзакцию
            db.subItemDao().insertSubItems(newList) 
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


     

    fun saveText() = pref.saveTextNoteBook(stateTextNotebook)

    fun getUri() = settingsPref.getUriAlarm()
    fun saveUri(uri: String) = settingsPref.saveUriAlarm(uri)

    fun updateCategory(value: String) {
        _categoryItemFlow.value = value
    }


    fun getSubItemsForTask(taskId: Int): Flow<List<SubItem>> {
        return db.getSubItemsForTask(taskId)
    }


//    fun insertItem(item: Item, alarm: Boolean = false,calendar: Boolean){
//
//        viewModelScope.launch(Dispatchers.IO) {
//
//            val currentMinSort = db.getItemWithMinSort()?.sort ?: 0
//            val newSortIndex = currentMinSort - 1
//            val newItem = item.copy(sort = newSortIndex)
//
//            val insertedId = db.insertItem(newItem)
//            withContext(Dispatchers.Main){
//                if(alarm) {
//                    val savedItem = newItem.copy(id = insertedId.toInt())
//                    permission(NOTIFICATION, savedItem,calendar)
//                } else showDialog = DialogState()
//            }
//
//        }
//    }

    fun insertItem(
        item: Item,
        subItems: List<SubItem>,
        alarm: Boolean = false,
        calendar: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val finalItem = if (item.id == 0) {
                val currentMinSort = db.getItemWithMinSort()?.sort ?: 0
                item.copy(sort = currentMinSort - 1)
            } else {
                item
            }

            // 1. Сохраняем дело и получаем его реальный ID
            val insertedId = db.insertItem(finalItem).toInt()

            // 2. ЕСЛИ ЭТО РЕДАКТИРОВАНИЕ (item.id != 0), чистим старые подзадачи дела в БД
            if (item.id != 0) {
                db.deleteAllSubItemsForTask(item.id)
            }

            // 3. Перепривязываем подзадачи к ID дела (для новых дел это будет insertedId)
            val updatedSubItems = subItems.map { subItem ->
                // Важно: обнуляем id самой подзадачи, так как мы пишем их заново как новые строки
                subItem.copy(id = 0, idTask = insertedId)
            }

            // 4. Записываем финальный список подзадач из диалога
            db.insertSubItems(updatedSubItems)

            withContext(Dispatchers.Main) {
                if (alarm) {
                    val savedItem = finalItem.copy(id = insertedId)
                    permission(NOTIFICATION, savedItem, calendar)
                } else {
                    showDialog = DialogState()
                }
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

//    fun updateItem(
//        item: Item,
//        alarm: Boolean = false,
//        calendar: Boolean = false){
//        viewModelScope.launch(Dispatchers.IO) {
//            db.updateItem(item)
//            withContext(Dispatchers.Main){
//                if(alarm) permission(NOTIFICATION, item, calendar)
//                else showDialog = DialogState()
//            }
//
//        }
//    }

    fun updateItem(
        item: Item,
        subItems: List<SubItem>? = null, // 👈 Сделали Nullable со значением по умолчанию null
        alarm: Boolean = false,
        calendar: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. Обновляем только само родительское дело в базе
            db.updateItem(item)

            // 2. ОБРАБАТЫВАЕМ ПОДЗАДАЧИ ТОЛЬКО ЕСЛИ СПИСОК БЫЛ ПЕРЕДАН (из диалога)
            if (subItems != null) {
                db.deleteAllSubItemsForTask(item.id)
                val updatedSubItems = subItems.map { subItem ->
                    subItem.copy(id = 0, idTask = item.id)
                }
                db.insertSubItems(updatedSubItems)
            }

            withContext(Dispatchers.Main) {
                if (alarm) permission(NOTIFICATION, item, calendar)
                else showDialog = DialogState()
            }
        }
    }

    fun permission(permissionName: String, item: Item? = null,calendar: Boolean = false) {
        viewModelScope.launch{
            val isChekedPermission = permission.isChekedPermission(permissionName)

            if(isChekedPermission) {
                val dialog = when(permissionName){
                    NOTIFICATION -> if(calendar) TIME else NOTIFICATION
                    ALARM_SETTINGS -> ALARM_SETTINGS
                    else -> DEFAULT_DIALOG
                }
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
                            updateItem(item = item.copy(changeAlarm = false))
                        } else {
                            insertAlarm(item)
                        }

                    }
                }
            }
        }
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
