package com.exampl3.flashlight.Presentation


import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.SORT_STANDART
import com.exampl3.flashlight.Data.GetSystemSoundImp
import com.exampl3.flashlight.Data.Room.BackupManager
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Data.Room.ListCategory
import com.exampl3.flashlight.Data.ThemeImp
import com.exampl3.flashlight.Data.sharedPreference.SettingsSharedPreference
import com.exampl3.flashlight.Data.sharedPreference.SharedPreferenceImpl
import com.exampl3.flashlight.Domain.InsertDateAndAlarm
import com.exampl3.flashlight.Domain.useCase.insertOrDeleteAlarm.ChangeAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.util.Calendar
import javax.inject.Inject


@HiltViewModel
class ViewModelFlashLight @Inject constructor(
    private val pref: SharedPreferenceImpl,
    private val settingsPref: SettingsSharedPreference,
    private val db: Database,
    private val insertDateAndTime: InsertDateAndAlarm,
    private val changeAlarm: ChangeAlarmUseCase,
    private val theme: ThemeImp,
    private val getSystemSoundImp: GetSystemSoundImp,
    private val backupManager: BackupManager
) : ViewModel() {


    private val _sortType = MutableStateFlow(settingsPref.getSort())
    val sortType = _sortType.asStateFlow()

    private val _categoryItemFlow = MutableStateFlow("Повседневные")
    val categoryItemFlow = _categoryItemFlow.asStateFlow()



    fun savePremium(flag: Boolean) = pref.savePremium(flag)
    fun getPremium() = pref.getPremium()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    suspend fun sendEvent(value: String) = _toastEvent.emit(value)

    fun doExport(uri: Uri) { // ИСПРАВЛЕНО: Context больше не принимаем!
        viewModelScope.launch(Dispatchers.IO) {
            val success = backupManager.exportDatabase(uri)
            if (success) {
                // Шлем событие во фрагмент
                sendEvent("Вы успешно сохранили базу данных")
            } else {
                sendEvent("Ошибка при сохранении базы данных")
            }
        }
    }


    fun doImport(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = backupManager.importDatabase(uri)
            if (success) {
                sendEvent("Вы успешно восстановили базу данных")

            } else {
                sendEvent("Ошибка при восстановлении базы данных")
            }
        }
    }


    val sortedItemsFlow: Flow<List<Item>> = combine(
        db.CourseDao().getAllItemsFlow(), // Поток всех дел
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

    fun getAllCategories(onResult: (List<String>) -> Unit, item: Item?, calendar: Boolean) {
        val listCategory = mutableListOf("Повседневные")
        viewModelScope.launch {
            listCategory.addAll(db.CourseDao().getAllCategories())
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


            onResult(listCategory)
        }

    }


    fun setView(map: Map<Const.Action, Map<View, Int>>) {
        theme.view(map)
    }

    fun setSize(map: Map<Const.Action, Map<View, Int>>) {
        theme.setTextSize(map)
    }

    fun setSizeTextIsList(list: List<TextView>) {
        theme.setSizeTextIsList(list)
    }

    fun getAllSound(): Map<String, Uri> {
        return getSystemSoundImp.getSound()
    }


    fun saveNoteBook(value: String) = pref.saveStringNoteBook(value)
    fun getNotebook() = pref.getStringNoteBook()


    val listItemLD = MutableLiveData<List<Item>>()

    val listItemLDCalendar = MutableLiveData<List<Item>>()

    val uriPhoto = MutableLiveData<String>()


    fun getAllListCategory(): Flow<List<ListCategory>> {
        return db.CourseDao().getAllListCategory()
    }

    fun saveSort(value: String) {
        _sortType.value = value
        settingsPref.saveSort(value)
    }

    fun getSort() = settingsPref.getSort()

    fun saveTheme(value: String) = settingsPref.saveTheme(value)
    fun getTheme() = settingsPref.getTheme()

    fun saveSize(value: String) = settingsPref.saveSize(value)
    fun getSize() = settingsPref.getSize()

    fun saveUriAlarm(uri: Uri) = settingsPref.saveUriAlarm(uri)
    fun getUriAlarm() = settingsPref.getUriAlarm()


    fun saveImagePermanently(context: Context, uri: Uri): Uri {
        try {
            val imagesDir = File(context.filesDir, "images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs() // Создаем директорию, если она не существует
            }
            val file = File(imagesDir, "${System.currentTimeMillis()}.jpg")

            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            if (file.exists()) {
                return Uri.fromFile(file)
            } else {
                Toast.makeText(context, "Произошла ошибка сохранения", Toast.LENGTH_SHORT).show()
                return "".toUri()
            }
        } catch (_: FileNotFoundException) {
            return "".toUri()
        }

    }

    fun deleteSavedImage(imageUri: Uri) {
        try {
            // Для URI вида "file:///data/data/.../images/123.jpg"
            if (imageUri.scheme == "file") {
                File(imageUri.path!!).delete()
                return
            }

            // Если URI в строковом формате (из вашего saveImagePermanently)
            val uriString = imageUri.toString()
            if (uriString.startsWith("file://")) {
                File(uriString.substringAfter("file://")).delete()
            }
        } catch (_: Exception) {
        }
    }


    fun updateCategory(value: String) {
        _categoryItemFlow.value = value
    }

    fun insertCategory(name: String, context: Context) {
        viewModelScope.launch {
            if (isCategoryNameExists(name)) Toast.makeText(
                context,
                "Такая категория уже есть",
                Toast.LENGTH_SHORT
            ).show()
            else db.CourseDao().insertCategory(ListCategory(null, name))
        }
    }

    fun upgrateCategory(item: ListCategory, name: String, context: Context) {
        viewModelScope.launch {
            val newitem = item.copy(name = name)
            if (isCategoryNameExists(name)) Toast.makeText(
                context,
                "Такая категория уже есть",
                Toast.LENGTH_SHORT
            ).show()
            else {
                db.CourseDao().updateCategory(newitem)
                db.CourseDao().getAllNewNoFlow(item.name).forEach {
                    db.CourseDao().updateItem(it.copy(category = name))
                }
                updateCategory(item.name)
            }
        }
    }

    suspend fun isCategoryNameExists(name: String): Boolean {
        return try {
            val count = db.CourseDao().isCategoryExists(name)
            count > 0
        } catch (e: Exception) {
            false
        }
    }

 fun updateItemsOrder(newList: List<Item>) {
    viewModelScope.launch(Dispatchers.IO) {
        try {
            // Вычисляем новые индексы на основе размера списка
            val totalCount = newList.size
            val itemsWithNewSort = newList.mapIndexed { index, item ->
                // Верхний элемент получает самый маленький индекс, нижний — самый большой
                item.copy(sort = index - totalCount) 
            }

            // Запускаем транзакцию базы данных. 
            // Room обновит всю сетку индексов за один микро-шаг!
            db.withTransaction {
                itemsWithNewSort.forEach { item ->
                    db.CourseDao().updateItem(item) // Обновляем строго в базе
                }
            }
            
            // Синхронизируем LiveData на главном потоке, если она используется
            withContext(Dispatchers.Main) {
                listItemLD.value = itemsWithNewSort
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

//    fun updateItemsOrder(newList: List<Item>) {
//        viewModelScope.launch {
//            saveNewOrder(newList)
//            listItemLD.value = newList
//        }
//
//    }
//
//    fun saveNewOrder(newList: List<Item>) {
//        newList.forEach { newItem ->
//            updateItem(newItem)
//        }
//    }

    // fun getItemMaxSort() {
    //     viewModelScope.launch {
    //         maxSorted.value = (db.CourseDao().getItemWithMaxSort()?.sort?.minus(1))
    //     }
    // }

    //fun insertItem(item: Item) {
    //    viewModelScope.launch { db.CourseDao().insertItem(item) }
    // }

    fun insertItem(
        name: String,
        category: String,
        desc: String?,
        alarmText: String,
        hasAlarmPermission: Boolean, // Передаем результат проверки разрешения
        isAlarmAction: Boolean,      // Был ли выбран будильник в диалоге
        context: Context,
        calendarDay: Calendar? = null
    ) {
        // Запускаем корутину на IO потоке для работы с БД
        viewModelScope.launch(Dispatchers.IO) {
            // 1. Ищем МИНИМАЛЬНЫЙ sort в базе. Если база пустая — будет 0
            val currentMinSort = db.CourseDao().getItemWithMinSort()?.sort ?: 0

            // 2. Вычитаем 1. Новое дело гарантированно получает самый маленький индекс и идет НАВЕРХ
            val newSortIndex = currentMinSort - 1

            val newItem = Item(
                id = null, // База данных сама сгенерирует ID
                name = name,
                category = category,
                desc = desc,
                alarmTime = calendarDay?.timeInMillis ?: 0,
                alarmText = alarmText,
                sort = newSortIndex
            )

            // 3. Вставляем элемент в БД и СРАЗУ получаем его реальный ID!
            val insertedId = db.CourseDao().insertItem(newItem)

            // 4. Если пользователь выбрал будильник И разрешение получено
            if (isAlarmAction && hasAlarmPermission) {
                // Создаем копию объекта уже с реальным ID из базы
                val savedItem = newItem.copy(id = insertedId.toInt())

                // Переключаемся на Главный поток для вызова вашего метода будильника
                withContext(Dispatchers.Main) {
                    insertDateAndAlarm(savedItem, calendarDay, context)
                }
            }
        }
    }


    fun updateItem(item: Item) {
        viewModelScope.launch { db.CourseDao().updateItem(item) }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch { db.CourseDao().delete(item) }
        deleteSavedImage(item.alarmText.toUri())

    }

    fun changeAlarm(item: Item, action: Int) {
        changeAlarm.exum(item, action)

    }

    fun insertDateAndAlarm(item: Item, date: Calendar?, context: Context) {
        viewModelScope.launch {
            insertDateAndTime.exumDateAndAction(item, date, context)
        }
    }

    fun insertStringAndAlarm(item: Item, context: Context, first: Boolean) {
        viewModelScope.launch {
            insertDateAndTime.exumAlarm(item, context, first)
        }
    }

    private suspend fun listItem(calendaZero: Long): List<Item> {
        return db.CourseDao().getUpdateItemRestartPhone(calendaZero).filter { it.changeAlarm }
    }

    fun updateAlarm(calendaZero: Long) {
        viewModelScope.launch {
            listItem(calendaZero).forEach { item ->
                when (item.interval) {
                    Const.ALARM_ONE -> {
                        changeAlarm(
                            item,
                            Const.ALARM_ONE
                        )

                    }

                    else -> {
                        if (!getPremium()) {
                            changeAlarm(
                                item,
                                Const.DELETE_ALARM
                            )
                            updateItem(item.copy(changeAlarm = false))
                        } else {
                            changeAlarm(
                                item,
                                item.interval
                            )

                        }

                    }
                }
            }
        }
    }


    fun getListItemByCalendar(time: Long) {
        viewModelScope.launch {
            listItemLDCalendar.value =
                db.CourseDao().getAllListCalendarRcView(time)
                    .filter { item -> item.changeAlarm || !item.change }
        }
    }

}


