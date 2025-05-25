package com.exampl3.flashlight.Presentation


import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.Room.Item
import com.exampl3.flashlight.Data.Room.ListCategory
import com.exampl3.flashlight.Data.sharedPreference.SharedPreferenceImpl
import com.exampl3.flashlight.Domain.InsertDateAndAlarm
import com.exampl3.flashlight.Domain.useCase.insertOrDeleteAlarm.ChangeAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ViewModelFlashLight @Inject constructor(
    private val pref: SharedPreferenceImpl,
    private val db: Database,
    private val insertDateAndTime: InsertDateAndAlarm,
    private val changeAlarm: ChangeAlarmUseCase,
) : ViewModel() {


    fun savePremium(flag: Boolean) = pref.savePremium(flag)
    fun getPremium() = pref.getPremium()

    fun saveNoteBook(value: String) = pref.saveStringNoteBook(value)
    fun getNotebook() = pref.getStringNoteBook()

    val categoryItemLD = MutableLiveData<String>()

    val listItemLD = MutableLiveData<List<Item>>()

    val listItemLDCalendar = MutableLiveData<List<Item>>()

    val uriPhoto = MutableLiveData<String>()

    fun getAllListCategory(): Flow<List<ListCategory>> {
        return db.CourseDao().getAllListCategory()
    }



     fun saveImagePermanently(context: Context, uri: Uri): Uri {
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
    }

    private fun deleteSavedImage(imageUri: Uri) {
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
        categoryItemLD.value = value
        viewModelScope.launch {
            listItemLD.value = db.CourseDao().getAllNewNoFlow(value)
        }


    }

    fun insertItem(item: Item) {
        viewModelScope.launch { db.CourseDao().insertItem(item) }
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


