package data.repostitory

import android.content.Context
import android.util.Log
import androidx.room.RoomDatabase
import androidx.room.execSQL
import androidx.room.useWriterConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import data.perository.DatabaseHolder
import io.github.vinceglb.filekit.core.PlatformFile
import okio.Sink
import okio.Source
import okio.sink
import okio.source
import domain.repostirory.PlatformBackupContextRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class AndroidBackupContextImpl(
    private val context: Context,
    private val dbHolder: DatabaseHolder // Наш холдер для закрытия/открытия базы без рестарта
) : PlatformBackupContextRepository {


    private val databaseName = "db"

    override suspend fun exportDatabase(targetPlatformFile: PlatformFile): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // 1. СБРАСЫВАЕМ WAL-ЖУРНАЛ НА ДИСК (БАЗУ НЕ ЗАКРЫВАЕМ!)
                val roomDb = dbHolder.db as RoomDatabase
                roomDb.useWriterConnection { connection ->
                    // Команда FULL принудительно переносит все данные из -wal в основной файл .db,
                    // оставляя базу полностью открытой для ваших Flow и UI!
                    connection.execSQL("PRAGMA wal_checkpoint(FULL);")
                }

                val dbFile = context.getDatabasePath(databaseName)
                val imagesDir = File(context.filesDir, "images")

                // 2. Создаем локальный ZIP во временном кэше приложения
                val localZipFile = File(context.cacheDir, "temp_backup.zip")
                if (localZipFile.exists()) localZipFile.delete()

                FileOutputStream(localZipFile).use { fileOutputStream ->
                    ZipOutputStream(fileOutputStream).use { zipOut ->
                        // Упаковываем базу данных (она гарантированно весит > 0 байт)
                        if (dbFile.exists()) {
                            addFileToZip(dbFile, dbFile.name, zipOut)
                        }

                        // Упаковываем картинки
                        if (imagesDir.exists() && imagesDir.isDirectory) {
                            imagesDir.listFiles()?.forEach { file ->
                                addFileToZip(file, "images/${file.name}", zipOut)
                            }
                        }
                    }
                }

                // 3. Переносим готовый архив в FileKit (SAF)
                var success = false
                if (localZipFile.exists() && localZipFile.length() > 0) {
                    context.contentResolver.openOutputStream(targetPlatformFile.uri)?.use { outputStream ->
                        FileInputStream(localZipFile).use { fis ->
                            fis.copyTo(outputStream)
                            outputStream.flush()
                            success = true
                        }
                    }
                }

                localZipFile.delete()
                success
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    override suspend fun importDatabase(sourcePlatformFile: PlatformFile): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Скачиваем архив во временный кэш
                val localZipFile = File(context.cacheDir, "temp_import.zip")
                if (localZipFile.exists()) localZipFile.delete()

                context.contentResolver.openInputStream(sourcePlatformFile.uri)?.use { inputStream ->
                    FileOutputStream(localZipFile).use { fos ->
                        inputStream.copyTo(fos)
                    }
                }

                if (!localZipFile.exists() || localZipFile.length().toInt() == 0) return@withContext false

                // 2. ПРИ ИМПОРТЕ ЗАКРЫВАЕМ БАЗУ (тут файлы заменить необходимо физически)
                dbHolder.close()

                val dbFile = context.getDatabasePath(databaseName)
                val imagesDir = File(context.filesDir, "images")
                if (!imagesDir.exists()) imagesDir.mkdirs()

                // 3. Распаковываем локальный ZIP
                FileInputStream(localZipFile).use { fileInputStream ->
                    ZipInputStream(fileInputStream).use { zipIn ->
                        var entry: ZipEntry? = zipIn.nextEntry
                        while (entry != null) {
                            when {
                                entry.name == databaseName -> {
                                    FileOutputStream(dbFile).use { fos -> zipIn.copyTo(fos) }
                                }
                                entry.name.startsWith("images/") -> {
                                    val fileName = entry.name.substringAfter("images/")
                                    if (fileName.isNotEmpty()) {
                                        val imageFile = File(imagesDir, fileName)
                                        FileOutputStream(imageFile).use { fos -> zipIn.copyTo(fos) }
                                    }
                                }
                            }
                            zipIn.closeEntry()
                            entry = zipIn.nextEntry
                        }
                    }
                }

                // Чистим логи SQLite
                File("${dbFile.path}-wal").delete()
                File("${dbFile.path}-shm").delete()
                localZipFile.delete()

                // Открываем базу заново
                dbHolder.recreate()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                dbHolder.recreate()
                false
            }
        }
    }

    private fun addFileToZip(file: File, zipEntryName: String, zipOut: ZipOutputStream) {
        FileInputStream(file).use { fis ->
            zipOut.putNextEntry(ZipEntry(zipEntryName))
            fis.copyTo(zipOut)
            zipOut.closeEntry()
        }
    }
}
