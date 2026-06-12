package com.exampl3.flashlight.Data.Room

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.sqlite.db.SupportSQLiteDatabase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.exitProcess

@Singleton
class BackupManager @Inject constructor(
    private val context: Application,
    private val db: Database
) {
    private val databaseName = "db"


    private val sharedPrefsName = "TABLE"

    // ЭКСПОРТ: База + Картинки + Один конкретный XML-файл настроек
    fun exportDatabase(outputStreamUri: Uri): Boolean {
        return try {
            val supportDb: SupportSQLiteDatabase = db.openHelper.writableDatabase
            supportDb.query("PRAGMA wal_checkpoint(FULL)").close()
            db.close()

            val dbFile = context.getDatabasePath(databaseName)
            val imagesDir = File(context.filesDir, "images")

            // Находим конкретный XML-файл настроек в системной папке shared_prefs
            val prefsDir = File(context.filesDir.parentFile, "shared_prefs")
            val singlePrefsFile = File(prefsDir, "$sharedPrefsName.xml")

            context.contentResolver.openOutputStream(outputStreamUri)?.use { outputStream ->
                ZipOutputStream(outputStream).use { zipOut ->
                    // 1. Упаковываем базу данных
                    if (dbFile.exists()) {
                        addFileToZip(dbFile, dbFile.name, zipOut)
                    }

                    // 2. Упаковываем картинки
                    if (imagesDir.exists() && imagesDir.isDirectory) {
                        imagesDir.listFiles()?.forEach { file ->
                            addFileToZip(file, "images/${file.name}", zipOut)
                        }
                    }

                    // 3. ИСПРАВЛЕНО: Упаковываем ТОЛЬКО ОДИН файл настроек
                    if (singlePrefsFile.exists()) {
                        addFileToZip(singlePrefsFile, "shared_prefs/$sharedPrefsName.xml", zipOut)
                    }
                }
            }
            restartApp()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ИМПОРТ: Распаковывает базу, картинки и заменяет один XML-файл настроек
    fun importDatabase(inputStreamUri: Uri): Boolean {
        return try {
            db.close()

            val dbFile = context.getDatabasePath(databaseName)
            val imagesDir = File(context.filesDir, "images")
            if (!imagesDir.exists()) imagesDir.mkdirs()

            val prefsDir = File(context.filesDir.parentFile, "shared_prefs")
            if (!prefsDir.exists()) prefsDir.mkdirs()

            context.contentResolver.openInputStream(inputStreamUri)?.use { inputStream ->
                ZipInputStream(inputStream).use { zipIn ->
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
                            // ИСПРАВЛЕНО: Распаковываем строго наш единственный XML-файл настроек
                            entry.name == "shared_prefs/$sharedPrefsName.xml" -> {
                                val prefsFile = File(prefsDir, "$sharedPrefsName.xml")
                                FileOutputStream(prefsFile).use { fos -> zipIn.copyTo(fos) }
                            }
                        }
                        zipIn.closeEntry()
                        entry = zipIn.nextEntry
                    }
                }
            }

            File("${dbFile.path}-wal").delete()
            File("${dbFile.path}-shm").delete()

            restartApp()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun addFileToZip(file: File, zipEntryName: String, zipOut: ZipOutputStream) {
        FileInputStream(file).use { fis ->
            zipOut.putNextEntry(ZipEntry(zipEntryName))
            fis.copyTo(zipOut)
            zipOut.closeEntry()
        }
    }

    private fun restartApp() {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        exitProcess(0)
    }
}
