
//import data.room.model.SubItem

import CommonConst.ALARM_ONE
import CommonConst.ALARM_SETTINGS
import CommonConst.BATTERY_OPTIMIZATION
import CommonConst.DEFAULT_DIALOG
import CommonConst.INSERT_DIALOG_ITEM
import CommonConst.NOTIFICATION
import CommonConst.SIZE_LARGE
import CommonConst.SIZE_SMALL
import CommonConst.SIZE_STANDART
import CommonConst.SORT_STANDART
import CommonConst.THEME_FUTURE
import CommonConst.THEME_ZABOR
import CommonConst.TIME
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.room.CourseDao
import data.room.model.Item
import data.room.model.ItemWithSubItems
import data.room.model.ListCategory
import data.room.model.SubItem
import domain.model.ProductCommon
import domain.repostirory.AlarmRepeadRepository
import domain.repostirory.AlarmRepository
import domain.repostirory.BackupManagerRepository
import domain.repostirory.GetPlatrormRepository
import domain.repostirory.PaySdkRepository
import domain.repostirory.PermissionRepository
import domain.repostirory.PickerRepository
import domain.repostirory.SaveDeleteImageRepositpry
import domain.repostirory.SettingsAppRepository
import domain.repostirory.SharedPrefRepository
import domain.repostirory.TelegramSyncServiceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import presentation.dialogs.DialogState
import presentation.theme.SizeLarge
import presentation.theme.SizeNormal
import presentation.theme.SizeSmall
import presentation.theme.ThemeNeon
import presentation.theme.ThemeZabor
import presentation.theme.ThemeStorm
import presentation.theme.ThemeMarble

import kotlin.time.Clock

class MainViewModel(
    private val pref: SharedPrefRepository,
    private val db: CourseDao,
    private val settingsPref : SettingsAppRepository,
    private val permission: PermissionRepository,
    private val alarm: AlarmRepository,
    private val alarmRepeat: AlarmRepeadRepository,
    private val image: SaveDeleteImageRepositpry,
    private val platform : GetPlatrormRepository,
    private val paySdk : PaySdkRepository,
    private val telegramSync : TelegramSyncServiceRepository,
    private val backUpManager : BackupManagerRepository,
    private val picker: PickerRepository

    ) : ViewModel() {

    fun testPremium(){
    savePremium(!pref.getPremium())    
    }
    private val _soundState = MutableStateFlow<Map<String, String>>(emptyMap())
    val soundState = _soundState.asStateFlow()

    private val _productState = MutableStateFlow<List<ProductCommon>>(emptyList())
    val productState = _productState.asStateFlow()


    var stateTextNotebook by mutableStateOf(pref.loadTextNoteBook())
    var showDialog by  mutableStateOf(DialogState())
    private var _toast = MutableSharedFlow<String>()
    var toast = _toast.asSharedFlow()

    private val _isBackupLoading = MutableStateFlow(false)
    val isBackupLoading: StateFlow<Boolean> = _isBackupLoading.asStateFlow()

     private val _premiumState = MutableStateFlow(pref.getPremium())

    //private val _premiumState = MutableStateFlow(true)
    val premiumState = _premiumState.asStateFlow()

    private val _updateState = MutableStateFlow(false)
    val updateState = _updateState.asStateFlow()

    private val _sortType = MutableStateFlow(settingsPref.getSort())
    val sortType = _sortType.asStateFlow()

    init {
        loadSounds()
        isUpdateApp()
        loadProduct()
        isCheckPremiumWithBuy()
       // startTelegramRealtimeListener()
    }

private val _sharedIntentEvent = Channel<Pair<String?, String?>>(Channel.BUFFERED)
// 2. Публичный Flow, который Compose будет безопасно слушать на UI-слое
val sharedIntentEvent = _sharedIntentEvent.receiveAsFlow()

// 3. Этот метод вызывается из MainActivity. Он просто складывает данные в очередь,
// не трогая изменчивый стейт диалога раньше времени!
fun openDialogWithSharedData(text: String?, imageUri: String?) {
    viewModelScope.launch {
        _sharedIntentEvent.send(Pair(text, imageUri))
    }
}

fun openDialogByTaskId(taskId: Int) {
    viewModelScope.launch {
        // 1. Берем задачу из базы (у тебя это CourseDao через db)
        // Если у тебя в DAO есть метод getById, используем его
        val task = db.getItemFromId(taskId) // Реализуй этот метод в Room, если его нет
        
        if (task != null) {
            // 2. Просто выставляем стейт диалога напрямую (так как вьюмодель общая)
            showDialog = DialogState(
                isWho = INSERT_DIALOG_ITEM,
                item = task,
                calendar = false // или true, если задача из календаря
            )
        }
    }
}

    val telegramTasksFlow = telegramSync.listenToTelegramRealtime()
        .onEach { taskText ->
            // Логика добавления в базу теперь живет здесь
            val newItem = Item(
                name = taskText,
                category = "Повседневные"
            )
            insertItem(
                item = newItem,
                subItems = emptyList(),
                calendar = false
            )
        }
        // Превращаем Flow в разделяемый поток, который засыпает через 5 секунд после того,=
        // как UI перестал его слушать (например, при сворачивании приложения)
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            replay = 0
        )




//    fun startTelegramRealtimeListener() {
//    viewModelScope.launch(Dispatchers.IO) {
//        telegramSync.listenToTelegramRealtime()
//            .collect { taskText -> // Сюда задача прилетает мгновенно в момент отправки в ТГ
//                val newItem = Item(
//                    name = taskText,
//                    category = "Повседневные"
//                )
//                // Записываем в Room. База обновится, и Compose сам перерисует экран!
//                insertItem(newItem)
//            }
//        }
//    }


    fun doImport() {
        viewModelScope.launch(Dispatchers.IO) {
            _isBackupLoading.value = true // Включаем незакрываемый лоадер
            val file = picker.openZipPicker()
            if(file != null) {
                if (backUpManager.loadDb(file)) {
                    // Шлем успех в ваш собственный SharedFlow для тостов
                    sendMessage("Восстановление прошло успешно!")
                    stateTextNotebook = pref.loadTextNoteBook()
                } else {
                    sendMessage("Не удалось прочитать или записать файл бэкапа.")
                }
            }

            _isBackupLoading.value = false
        }
    }




    fun doExport() {
        viewModelScope.launch {
            _isBackupLoading.value = true

                val file = picker.createZipPicker("Focus_Backup.zip")
                if (file != null) {
                    backUpManager.saveDb(file)
                    sendMessage("Резервная копия создана!")
                }


            _isBackupLoading.value = false
        }
    }


     private fun loadSounds() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = platform.getAllSound()
            _soundState.value = result // Обновляем состояние (это безопасно)
        }
    }

    private fun loadProduct() {
        viewModelScope.launch(Dispatchers.IO) {
         paySdk.getAllProduct()
             .onSuccess {listProduct ->
                 _productState.value = listProduct
             }
            // .onFailure { sendMessage("Оплата временна недоступна")}
             .onFailure { _productState.value = emptyList<ProductCommon>()}
        }
    }

    fun buyProduct(productId : String) {
        viewModelScope.launch {
            paySdk.byProduct(productId)
                .onSuccess { hasPremium ->
                    if(hasPremium) {
                        savePremium(true)
                        sendMessage("Поздравляю! Теперь вам доступны PREMIUM функции")
                    } else {
                        sendMessage("Вы отменили покупку")
                    }


                }
                .onFailure { sendMessage("Произошла ошибка оплаты") }
        }

    }

    private fun isCheckPremiumWithBuy() {
        viewModelScope.launch {
            paySdk.isChekedSubcrition()
                .onSuccess { result->
                    val isLocalPremiumActive = getPremium()

                    if (result != isLocalPremiumActive) {
                        if (result) {
                            sendMessage("PREMIUM версия была восстановлена")
                        } else {
                            sendMessage("PREMIUM версия была отключена")
                        }
                    }
                    savePremium(result)
                }
                .onFailure {  }



        }
    }

    private fun isUpdateApp() {
        viewModelScope.launch(Dispatchers.IO) {
            platform.updateApp { result ->
                _updateState.value = result
            }
        }
    }


    
    var themeState by mutableStateOf(
        when (settingsPref.getTheme()) {
            THEME_FUTURE -> ThemeNeon()
            THEME_ZABOR -> ThemeZabor()
            THEME_MRAMOR -> ThemeMarble()
            THEME_GROZA -> ThemeStorm()
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


    val categories: StateFlow<List<ListCategory>> = db.getAllListCategory()
    .stateIn(
        scope = viewModelScope, // Привязываем к жизненному циклу ViewModel
        started = SharingStarted.WhileSubscribed(5000), // Засыпает через 5 сек после закрытия экрана
        initialValue = emptyList() // Начальное значение, пока база грузится
    )

    val getCalendarWithSubItemsCombine: StateFlow<List<ItemWithSubItems>> = combine(
        db.getItemsInCalendar(), // 1. Поток дел для календаря из БД
        db.getAllSubItems(),     // 2. Поток всех подзадач из БД
        premiumState             // 3. Поток статуса премиума
    ) { calendarItems, allSubItems, isPremium ->

        // Если у пользователя нет премиума — сразу возвращаем пустой список
        if (!isPremium) return@combine emptyList()

        // Если премиум есть, группируем подзадачи по idTask для скорости
        val subItemsGrouped = allSubItems.groupBy { it.idTask }

        // Склеиваем дела календаря с их подзадачами
        calendarItems.map { item ->
            ItemWithSubItems(
                item = item,
                subItems = (subItemsGrouped[item.id] ?: emptyList()).sortedBy { it.sort }
            )
        }
    }.flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
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
        // 1. Фильтруем дела по категории
        val filteredList = itemsList.filter { it.category == currentCategory }

        // 2. Сортируем дела
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

        // 💡 ОПТИМИЗАЦИЯ: Группируем ВСЕ подзадачи по idTask один раз.
        // Получится Map<Int, List<SubItem>>, где ключ — это idTask.
        val subItemsGrouped = allSubItems.groupBy { it.idTask }

        // 3. Мгновенно маппим за один проход O(N)
        sortedList.map { item ->
            val subItemsForThisTask = subItemsGrouped[item.id] ?: emptyList()
            ItemWithSubItems(
                item = item,
                // Сортируем уже только подзадачи конкретно этого дела
                subItems = subItemsForThisTask.sortedBy { it.sort }
            )
        }
    }.flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

//val sortedItemsFlow: StateFlow<List<ItemWithSubItems>> = combine(
//    db.getAll(),
//    db.getAllSubItems(),
//    sortType,
//    categoryItemFlow
//) { itemsList, allSubItems, sort, currentCategory ->
//    val filteredList = itemsList.filter { it.category == currentCategory }
//
//    val sortedList = if (sort == SORT_STANDART) {
//        filteredList.sortedWith(
//            compareBy<Item> { if (it.changeAlarm) 0 else 1 }
//                .thenBy { if (it.change) 1 else 0 }
//                .thenBy { it.alarmTime }
//                .thenBy { it.sort }
//        )
//    } else {
//        filteredList.sortedBy { it.sort }
//    }
//
//    sortedList.map { item ->
//        ItemWithSubItems(
//            item = item,
//            subItems = allSubItems.filter { it.idTask == item.id }.sortedBy { it.sort }
//        )
//    }
//}.flowOn(Dispatchers.Default)
//    .stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = emptyList()
//    )


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
            db.insertSubItems(newList) 
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

        fun insertItem(
        item: Item,
        subItems: List<SubItem> = emptyList(),
        alarm: Boolean = false,
        calendar: Boolean = false
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


    // fun insertItem(
    //     item: Item,
    //     subItems: List<SubItem>,
    //     alarm: Boolean = false,
    //     calendar: Boolean
    // ) {
    //     viewModelScope.launch(Dispatchers.IO) {
    //         val finalItem = if (item.id == 0) {
    //             val currentMinSort = db.getItemWithMinSort()?.sort ?: 0
    //             item.copy(sort = currentMinSort - 1)
    //         } else {
    //             item
    //         }

    //         // 1. Сохраняем дело и получаем его реальный ID
    //         val insertedId = db.insertItem(finalItem).toInt()

    //         // 2. ЕСЛИ ЭТО РЕДАКТИРОВАНИЕ (item.id != 0), чистим старые подзадачи дела в БД
    //         if (item.id != 0) {
    //             db.deleteAllSubItemsForTask(item.id)
    //         }

    //         // 3. Перепривязываем подзадачи к ID дела (для новых дел это будет insertedId)
    //         val updatedSubItems = subItems.map { subItem ->
    //             // Важно: обнуляем id самой подзадачи, так как мы пишем их заново как новые строки
    //             subItem.copy(id = 0, idTask = insertedId)
    //         }

    //         // 4. Записываем финальный список подзадач из диалога
    //         db.insertSubItems(updatedSubItems)

    //         withContext(Dispatchers.Main) {
    //             if (alarm) {
    //                 val savedItem = finalItem.copy(id = insertedId)
    //                 permission(NOTIFICATION, savedItem, calendar)
    //             } else {
    //                 showDialog = DialogState()
    //             }
    //         }
    //     }
    // }
    fun deleteItem(item: Item){
        viewModelScope.launch(Dispatchers.IO) {
            db.delete(item)
            deleteImage(item.uri)
            deleteAlarm(item.id)
        }
    }

    fun deleteSubItem(subItem: SubItem) {
           viewModelScope.launch(Dispatchers.IO) {
            db.deleteSubItem(subItem) 
        }     
    }

        fun updateSubItem(subItem: SubItem) {
           viewModelScope.launch(Dispatchers.IO) {
            db.updateSubItem(subItem) 
        }     
    }

    fun updateItem(
        item: Item,
        subItems: List<SubItem>? = null, // 👈 Сделали Nullable со значением по умолчанию null
        alarm: Boolean = false,
        calendar: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. Обновляем только само родительское дело в базе
            db.updateItem(item)
            // делаю все поля true у подзадач если главное дело true
            if (item.change) {
                db.markAllSubItemsAsCompleted(item.id)
            }

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

            if (isChekedPermission) {
                println(isChekedPermission)

                when (permissionName) {
                    // Для батареи диалоги не нужны — просто уведомляем пользователя, что всё уже работает
                    BATTERY_OPTIMIZATION -> {
                        sendMessage("Разрешение уже выдано")
                    }

                    // Для уведомлений и будильников открываем соответствующие диалоги
                    NOTIFICATION -> {
                        showDialog = DialogState(if (calendar) TIME else NOTIFICATION, item)
                    }
                    ALARM_SETTINGS -> {
                        showDialog = DialogState(ALARM_SETTINGS, item)
                    }

                    else -> {
                        showDialog = DialogState(DEFAULT_DIALOG, item)
                    }
                }
            }
            else {
                val isGranted = permission.requestPermission(permissionName)
                if (isGranted) {
                    // Если всё успешно
                    when (permissionName) {
                        "APP_SETTINGS" -> {  }
                        else -> showDialog = DialogState(permissionName, item)
                    }
                } else {
                    // Если произошла ошибка или отказ
                    when (permissionName) {
                        "APP_SETTINGS" -> sendMessage("Не удалось открыть настройки")
                        else -> sendMessage("Для стабильной работы, необходимо дать разрешение")
                    }
                }

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
