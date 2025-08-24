package com.exampl3.flashlight.Data

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.core.net.toUri
import com.exampl3.flashlight.Domain.repository.GetSystemSoundRepository
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSystemSoundImp @Inject constructor(private val contentResolver: ContentResolver): GetSystemSoundRepository {
    override fun getSound(): Map<String, Uri> {
        val sounds = mutableMapOf<String, Uri>()

        // Упрощенный запрос без проверки доступности
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

        try {
            contentResolver.query(
                collection,
                projection,
                selection,
                null,
                null // Убираем сортировку на этапе запроса
            )?.use { cursor ->
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
                    val contentUri = ContentUris.withAppendedId(collection, id)
                    sounds[title] = contentUri
                }
            }
        } catch (e: Exception) {
            Log.e("SoundQuery", "Error: ${e.message}")
        }

        // Добавляем системные звуки БЕЗ проверок
        addDefaultSystemSounds(sounds)

        // Сортируем уже после получения всех данных
        return sounds.toList()
            .sortedWith(compareBy(
                { !it.first.any { char -> char in 'А'..'я' || char == 'ё' || char == 'Ё' } },
                { it.first.lowercase() }
            ))
            .toMap()
    }


    private fun addDefaultSystemSounds(sounds: MutableMap<String, Uri>) {
        sounds["По умолчанию"] = Settings.System.DEFAULT_ALARM_ALERT_URI

    }
}