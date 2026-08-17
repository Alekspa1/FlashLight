package data.perository

import data.room.CourseDao
import data.room.myDataBase
import domain.model.AppJsonBackup
import domain.repostirory.BackupManagerRepository
import domain.repostirory.PathProviderRepostitory
import domain.repostirory.SaveDeleteImageRepositpry
import domain.repostirory.SharedPrefRepository
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import androidx.room.useWriterConnection

class BackupManagerImpl(
    private val db: myDataBase,                             // Прямая ссылка на вашу Room БД
    private val sharedPrefRepository: SharedPrefRepository, // Репозиторий блокнота
    private val pathProvider: PathProviderRepostitory,      // Провайдер путей
    private val imageRepository: SaveDeleteImageRepositpry, // Репозиторий картинок
    private val fileSystem: FileSystem = FileSystem.SYSTEM
) : BackupManagerRepository {

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    // ==========================================
    // ЭКСПОРТ (СОХРАНЕНИЕ)
    // ==========================================
    override suspend fun saveDb(targetPlatformFile: PlatformFile): Boolean {
        return withContext(Dispatchers.IO) {
            val internalPath = pathProvider.getInternalAppPath()
            val backupDir = "$internalPath/temp_backup_dir".toPath()
            val tempZipFile = "$internalPath/final_backup.zip".toPath()

            try {
                // 1. Создаем временную папку бэкапа
                fileSystem.deleteRecursively(backupDir)
                fileSystem.createDirectories(backupDir)

                // 2. Выгружаем данные из блокнота и Room
                val notebookText = sharedPrefRepository.loadTextNoteBook()
                println(notebookText)
                val list1 = db.CourseDao().getAllListCategoryNotFlow()
                val list2 = db.CourseDao().getAllItemsNotFlow()
                val list3 = db.CourseDao().getAllSubItemsNotFlow()

                // Собираем контейнер строго с твоими именами переменных
                val backupContainer = AppJsonBackup(
                    notebookText = notebookText,
                    listCategorys = list1,
                    listItems = list2,
                    listSubItems = list3
                )

                val jsonString = json.encodeToString(AppJsonBackup.serializer(), backupContainer)
                fileSystem.write(backupDir / "backup_data.json") { writeUtf8(jsonString) }

                // 3. Копируем картинки во временную папку
                val imagesSrcDir = "$internalPath/images".toPath()
                if (fileSystem.exists(imagesSrcDir)) {
                    val imagesDestDir = backupDir / "images"
                    fileSystem.createDirectories(imagesDestDir)
                    fileSystem.list(imagesSrcDir).forEach { file ->
                        fileSystem.copy(file, imagesDestDir / file.name)
                    }
                }

                // 4. Пакуем всё в ZIP через expect fun
                fileSystem.delete(tempZipFile, mustExist = false)
                zipDirectory(backupDir.toString(), tempZipFile.toString())

//5
                val success = if (fileSystem.exists(tempZipFile) && (fileSystem.metadata(tempZipFile).size ?: 0L) > 0L) {
                    // Вызываем нашу функцию, которая на Android запишет через .uri, а на iOS/ПК через Okio!
                    writeZipToPlatformFile(tempZipFile.toString(), targetPlatformFile)
                } else {
                    false
                }

                // Тотальная очистка мусора на диске приложения
                fileSystem.deleteRecursively(backupDir)
                fileSystem.delete(tempZipFile, mustExist = false)
                return@withContext success

            } catch (e: Exception) {
                e.printStackTrace()
                println(e.message.toString())
                fileSystem.deleteRecursively(backupDir)
                fileSystem.delete(tempZipFile, mustExist = false)
                return@withContext false
            }
        }
    }

    // ==========================================
    // ИМПОРТ (ВОССТАНОВЛЕНИЕ)
    // ==========================================
    override suspend fun loadDb(sourcePlatformFile: PlatformFile): Boolean {
        return withContext(Dispatchers.IO) {
            val internalPath = pathProvider.getInternalAppPath()
            val tempZipFile = "$internalPath/temp_import.zip".toPath()
            val unpackDir = "$internalPath/temp_unpacked".toPath()

            try {
                // ВОТ СЮДА В САМОЕ НАЧАЛО ВСТАВЛЯЕМ ЧТЕНИЕ ЧЕРЕЗ EXPECT-ФУНКЦИЮ!
                fileSystem.delete(tempZipFile, mustExist = false)

                // Нативно выкачиваем файл из шторки во внутренний кэш приложения
                val copied = readZipFromPlatformFile(sourcePlatformFile, tempZipFile.toString())
                if (!copied) return@withContext false

                // 2. Распаковываем этот ZIP
                fileSystem.deleteRecursively(unpackDir)
                unzipDirectory(tempZipFile.toString(), unpackDir.toString())

                // 3. Находим и читаем наш общий файл backup_data.json
                val jsonFile = unpackDir / "backup_data.json"
                if (!fileSystem.exists(jsonFile)) return@withContext false

                val jsonString = fileSystem.read(jsonFile) { readUtf8() }
                val container = json.decodeFromString<AppJsonBackup>(jsonString)

                // 4. СИНХРОННО восстанавливаем текст блокнота


                // 5. Записываем новые данные в ОДНОЙ транзакции Room
                db.useWriterConnection {
                    db.CourseDao().deleteAllCategorys()
                    db.CourseDao().deleteAllItems()
                    db.CourseDao().deleteAllSubItems()

                    db.CourseDao().insertCategorys(container.listCategorys)
                    db.CourseDao().insertItems(container.listItems)
                    db.CourseDao().insertSubItems(container.listSubItems)
                }
                sharedPrefRepository.saveTextNoteBook(container.notebookText)
                println(container.notebookText)

                // 6. Очищаем старую папку картинок перед накатом новых
                val currentImagesDir = "$internalPath/images".toPath()
                val newImagesDir = unpackDir / "images"

                if (fileSystem.exists(currentImagesDir)) {
                    fileSystem.list(currentImagesDir).forEach { file ->
                        imageRepository.delete(file.name)
                    }
                }

                // 7. Переносим новые картинки в постоянную папку
                if (fileSystem.exists(newImagesDir)) {
                    fileSystem.createDirectories(currentImagesDir)
                    fileSystem.list(newImagesDir).forEach { file ->
                        fileSystem.copy(file, currentImagesDir / file.name)
                    }
                }

                // Полностью очищаем временные файлы
                fileSystem.deleteRecursively(unpackDir)
                fileSystem.delete(tempZipFile, mustExist = false)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                fileSystem.deleteRecursively(unpackDir)
                fileSystem.delete(tempZipFile, mustExist = false)
                false
            }
        }
    }
}

expect fun zipDirectory(sourceDir: String, targetZipFile: String)
expect fun unzipDirectory(sourceZipFile: String, targetDir: String)

expect fun writeZipToPlatformFile(sourceFilePath: String, targetPlatformFile: PlatformFile): Boolean
expect fun readZipFromPlatformFile(sourcePlatformFile: PlatformFile, targetFilePath: String): Boolean