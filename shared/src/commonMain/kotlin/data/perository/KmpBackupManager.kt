package data.perository

import androidx.room.RoomDatabase
import com.russhwolf.settings.Settings
import io.github.vinceglb.filekit.core.PlatformFile
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import kotlinx.serialization.json.*

class KmpBackupManager(
    private val db: RoomDatabase,
    private val settings: Settings,
    private val pathProvider: PathProviderRepostitory,
    private val backupContext: PlatformBackupContextRepository,
    private val fileSystem: FileSystem = FileSystem.SYSTEM
) {
    // Имя вашей базы данных (замените на ваше реальное имя файла БД, если оно другое)
    private val databaseName = "db" 

    // ЭКСПОРТ: Упаковываем базу, картинки и настройки в ZIP
    fun exportDatabase(targetPlatformFile: PlatformFile): Boolean {
        return try {
            // 1. Закрываем базу данных, чтобы Room сбросил все кэши (WAL/SHM) на диск
            db.close()

            // 2. Получаем пути к файлам, используя ваш PathProviderRepostitory
            // Предполагаем, что БД лежит в корне внутренней папки приложения, либо скорректируйте путь
            val dbFile = "${pathProvider.getInternalAppPath()}/$databaseName".toPath()
            val imagesDir = "${pathProvider.getInternalAppPath()}/images".toPath()

            // 3. Открываем поток записи в системный файл, который выбрал пользователь
            backupContext.getSinkFromPlatformFile(targetPlatformFile).buffer().use { sink ->
                
                // Создаем временный архив во внутренней папке приложения для сборки ZIP
                val tempZipPath = "${pathProvider.getInternalAppPath()}/temporary_export.zip".toPath()
                fileSystem.write(tempZipPath) {} // Инициализируем пустой файл
                
                val zipFileSystem = fileSystem.openZip(tempZipPath)

                // Шаг А: Упаковываем базу данных
                if (fileSystem.exists(dbFile)) {
                    zipFileSystem.write(databaseName.toPath()) {
                        writeAll(fileSystem.source(dbFile))
                    }
                }

                // Шаг Б: Упаковываем картинки из папки images
                if (fileSystem.exists(imagesDir)) {
                    fileSystem.list(imagesDir).forEach { file ->
                        val entryPath = "images/${file.name}".toPath()
                        zipFileSystem.write(entryPath) {
                            writeAll(fileSystem.source(file))
                        }
                    }
                }

                // Шаг В: Переводим Multiplatform Settings в JSON-строку и упаковываем в архив
                val settingsMap = mutableMapOf<String, JsonElement>()
                settings.keys.forEach { key ->
                    val value = try { JsonPrimitive(settings.getString(key, "")) } catch (e: Exception) {
                        try { JsonPrimitive(settings.getBoolean(key, false)) } catch (e: Exception) {
                            try { JsonPrimitive(settings.getInt(key, 0)) } catch (e: Exception) { JsonPrimitive("") }
                        }
                    }
                    settingsMap[key] = value
                }
                val settingsJson = JsonObject(settingsMap).toString()
                
                zipFileSystem.write("shared_prefs/settings.json".toPath()) {
                    writeUtf8(settingsJson)
                }

                // Закрываем архиватор и переносим готовые байты архива в целевой Sink пользователя
                sink.writeAll(fileSystem.source(tempZipPath))
                
                // Удаляем временный файл с устройства
                fileSystem.delete(tempZipPath)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ИМПОРТ: Распаковываем всё обратно полностью бесшовно
    fun importDatabase(sourcePlatformFile: PlatformFile): Boolean {
        return try {
            // 1. Принудительно закрываем текущую базу данных (освобождаем файлы на диске)
            db.close()

            val dbFile = "${pathProvider.getInternalAppPath()}/$databaseName".toPath()
            val imagesDir = "${pathProvider.getInternalAppPath()}/images".toPath()
            
            if (!fileSystem.exists(imagesDir)) {
                fileSystem.createDirectories(imagesDir)
            }

            // 2. Копируем входящий поток во временный файл архива на устройстве, чтобы Okio мог его прочитать
            val tempImportPath = "${pathProvider.getInternalAppPath()}/temporary_import.zip".toPath()
            fileSystem.write(tempImportPath) {
                writeAll(backupContext.getSourceFromPlatformFile(sourcePlatformFile))
            }

            val zipFileSystem = fileSystem.openZip(tempImportPath)

            // Шаг А: Извлекаем и перезаписываем файл базы данных
            val zipDbPath = databaseName.toPath()
            if (zipFileSystem.exists(zipDbPath)) {
                fileSystem.write(dbFile) {
                    writeAll(zipFileSystem.source(zipDbPath))
                }
            }

            // Шаг Б: Извлекаем и перезаписываем картинки
            val zipImagesPath = "images".toPath()
            if (zipFileSystem.exists(zipImagesPath)) {
                zipFileSystem.list(zipImagesPath).forEach { entry ->
                    val targetImageFile = imagesDir / entry.name
                    fileSystem.write(targetImageFile) {
                        writeAll(zipFileSystem.source(entry))
                    }
                }
            }

            // Шаг В: Извлекаем настройки и накатываем их через Multiplatform Settings API
            val zipPrefsPath = "shared_prefs/settings.json".toPath()
            if (zipFileSystem.exists(zipPrefsPath)) {
                val jsonString = zipFileSystem.read(zipPrefsPath) { readUtf8() }
                val jsonObject = Json.parseToJsonElement(jsonString).jsonObject
                
                settings.clear() // Стираем текущие настройки в кэше и памяти
                jsonObject.forEach { (key, element) ->
                    val primitive = element.jsonPrimitive
                    if (primitive.isString) settings.putString(key, primitive.content)
                    else if (primitive.booleanOrNull != null) settings.putBoolean(key, primitive.boolean)
                    else if (primitive.intOrNull != null) settings.putInt(key, primitive.int)
                }
            }

            // 3. Удаляем временный файл импорта
            fileSystem.delete(tempImportPath)

            // 4. Очищаем старые временные файлы кэша Room (WAL и SHM), чтобы они не конфликтовали с новой БД
            fileSystem.delete("${pathProvider.getInternalAppPath()}/$databaseName-wal".toPath(), mustExist = false)
            fileSystem.delete("${pathProvider.getInternalAppPath()}/$databaseName-shm".toPath(), mustExist = false)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
