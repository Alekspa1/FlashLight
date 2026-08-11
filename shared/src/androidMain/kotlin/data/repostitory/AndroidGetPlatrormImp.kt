package data.repostitory

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import domain.repostirory.GetPlatrormRepository
import ru.rustore.sdk.appupdate.manager.factory.RuStoreAppUpdateManagerFactory
import ru.rustore.sdk.appupdate.model.UpdateAvailability
import kotlin.collections.set

class AndroidGetPlatrormImp(private val contentResolver: ContentResolver,private val context: Context) : GetPlatrormRepository {

    override fun getPlatform(): String = "Android"

    override suspend fun getAllSound(): Map<String, String> {
        // Инициализируем LinkedHashMap, чтобы сохранить порядок сортировки из SQL-запроса
        val sounds = linkedMapOf<String, String>()

        // 1. Сразу добавляем дефолтный звук, чтобы он гарантированно был самым первым в списке
        sounds["По умолчанию"] = Settings.System.DEFAULT_ALARM_ALERT_URI.toString()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_INTERNAL)
        } else {
            Uri.parse("content://media/internal/audio/media")
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DISPLAY_NAME
        )

        val selection = """
            ${MediaStore.Audio.Media.IS_RINGTONE} = 1 OR 
            ${MediaStore.Audio.Media.IS_NOTIFICATION} = 1 OR 
            ${MediaStore.Audio.Media.IS_ALARM} = 1
        """.trimIndent()

        // Оптимизация сортировки: перекладываем её на SQLite базу данных Android.
        // Сначала пойдут цифры, затем русский алфавит, затем английский.
        val sortOrder = "CASE WHEN ${MediaStore.Audio.Media.TITLE} GLOB '[А-Яа-яЁё]*' THEN 1 ELSE 2 END, ${MediaStore.Audio.Media.TITLE} ASC"

        try {
            contentResolver.query(
                collection,
                projection,
                selection,
                null,
                sortOrder // Передаем сортировку в SQL
            )?.use { cursor ->
                // ОПТИМИЗАЦИЯ: Достаем индексы ОДИН раз ДО цикла while
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val nameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)

                    val title = when {
                        titleColumn >= 0 && !cursor.isNull(titleColumn) -> cursor.getString(titleColumn)
                        nameColumn >= 0 && !cursor.isNull(nameColumn) -> cursor.getString(nameColumn)
                        else -> "Sound_$id"
                    }

                    sounds[title] = ContentUris.withAppendedId(collection, id).toString()
                }
            }
        } catch (e: Exception) {
            Log.e("SoundQuery", "Error: ${e.message}")
        }

        return sounds
    }

    override fun updateApp(result: (Boolean) -> Unit) {
        val updateManager = RuStoreAppUpdateManagerFactory.create(context)
        updateManager.getAppUpdateInfo().addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability == UpdateAvailability.UPDATE_AVAILABLE) {
               result(true)
            }
        }

    }
}