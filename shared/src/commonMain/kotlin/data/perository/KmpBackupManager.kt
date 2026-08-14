package data.perository

import androidx.room.RoomDatabase
import com.russhwolf.settings.Settings
import io.github.vinceglb.filekit.core.PlatformFile
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import okio.openZip
import kotlinx.serialization.json.*
import domain.repostirory.PathProviderRepostitory
import domain.repostirory.PlatformBackupContextRepository

class KmpBackupManager(
    private val db: RoomDatabase,
    private val settings: Settings, // Сюда мы передадим именно те настройки, которые "TABLE"
    private val pathProvider: PathProviderRepostitory,
    private val backupContext: PlatformBackupContextRepository,
    private val fileSystem: FileSystem = FileSystem.SYSTEM
) {
    private val databaseName = "db"

    fun exportDatabase(targetPlatformFile: PlatformFile): Boolean {
        return try {
            db.close()

            val dbFile = "${pathProvider.getInternalAppPath()}/$databaseName".toPath()
            val imagesDir = "${pathProvider.getInternalAppPath()}/images".toPath()

            backupContext.getSinkFromPlatformFile(targetPlatformFile).buffer().use { sink ->
                val tempZipPath = "${pathProvider.getInternalAppPath()}/temporary_export.zip".toPath()
                fileSystem.write(tempZipPath) {}
                
                val zipFileSystem = fileSystem.openZip(tempZipPath)

                // 1. База
                if (fileSystem.exists(dbFile)) {
                    zipFileSystem.write(databaseName.toPath()) {
                        writeAll(fileSystem.source(dbFile))
                    }
                }

                // 2. Картинки
                if (fileSystem.exists(imagesDir)) {
                    fileSystem.list(imagesDir).forEach { file ->
                        val entryPath = "images/${file.name}".toPath()
                        zipFileSystem.write(entryPath) {
                            writeAll(fileSystem.source(file))
                        }
                    }
                }

                // 3. ИСПРАВЛЕНО: Читаем ключи ТОЛЬКО из переданных настроек ("TABLE")
                val settingsMap = mutableMapOf<String, JsonElement>()
                settings.keys.forEach { key ->
                    // Безопасно вытаскиваем типы данных
                    val value = try { 
                        JsonPrimitive(settings.getString(key, "")) 
                    } catch (e: Exception) {
                        try { JsonPrimitive(settings.getBoolean(key, false)) } 
                        catch (e: Exception) {
                            try { JsonPrimitive(settings.getInt(key, 0)) } 
                            catch (e: Exception) { JsonPrimitive("") }
                        }
                    }
                    settingsMap[key] = value
                }
                
                // Сохраняем под именем вашей таблицы, чтобы структура была прозрачной
                val settingsJson = JsonObject(settingsMap).toString()
                zipFileSystem.write("shared_prefs/TABLE.json".toPath()) {
                    writeUtf8(settingsJson)
                }

                sink.writeAll(fileSystem.source(tempZipPath))
                fileSystem.delete(tempZipPath)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun importDatabase(sourcePlatformFile: PlatformFile): Boolean {
        return try {
            db.close()

            val dbFile = "${pathProvider.getInternalAppPath()}/$databaseName".toPath()
            val imagesDir = "${pathProvider.getInternalAppPath()}/images".toPath()
            
            if (!fileSystem.exists(imagesDir)) fileSystem.createDirectories(imagesDir)

            val tempImportPath = "${pathProvider.getInternalAppPath()}/temporary_import.zip".toPath()
            fileSystem.write(tempImportPath) {
                writeAll(backupContext.getSourceFromPlatformFile(sourcePlatformFile))
            }

            val zipFileSystem = fileSystem.openZip(tempImportPath)

            // 1. База
            val zipDbPath = databaseName.toPath()
            if (zipFileSystem.exists(zipDbPath)) {
                fileSystem.write(dbFile) {
                    writeAll(zipFileSystem.source(zipDbPath))
                }
            }

            // 2. Картинки
            val zipImagesPath = "images".toPath()
            if (zipFileSystem.exists(zipImagesPath)) {
                zipFileSystem.list(zipImagesPath).forEach { entry ->
                    val targetImageFile = imagesDir / entry.name
                    fileSystem.write(targetImageFile) {
                        writeAll(zipFileSystem.source(entry))
                    }
                }
            }

            // 3. ИСПРАВЛЕНО: Накатываем настройки строго в нашу таблицу "TABLE"
            val zipPrefsPath = "shared_prefs/TABLE.json".toPath()
            if (zipFileSystem.exists(zipPrefsPath)) {
                val jsonString = zipFileSystem.read(zipPrefsPath) { readUtf8() }
                val jsonObject = Json.parseToJsonElement(jsonString).jsonObject
                
                settings.clear() // Очищаем только эту таблицу ("TABLE")
                jsonObject.forEach { (key, element) ->
                    val primitive = element.jsonPrimitive
                    if (primitive.isString) settings.putString(key, primitive.content)
                    else if (primitive.booleanOrNull != null) settings.putBoolean(key, primitive.boolean)
                    else if (primitive.intOrNull != null) settings.putInt(key, primitive.int)
                }
            }

            fileSystem.delete(tempImportPath)
            fileSystem.delete("${pathProvider.getInternalAppPath()}/$databaseName-wal".toPath(), mustExist = false)
            fileSystem.delete("${pathProvider.getInternalAppPath()}/$databaseName-shm".toPath(), mustExist = false)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
