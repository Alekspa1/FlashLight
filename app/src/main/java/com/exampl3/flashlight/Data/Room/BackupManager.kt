import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Environment
import androidx.sqlite.db.SupportSQLiteDatabase
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.exitProcess
import com.exampl3.flashlight.Data.Room.Database

@Singleton
class BackupManager @Inject constructor(
    private val context: Application,
    private val db: Database
) {
    private val databaseName = "db"

    // Экспорт: принимает Uri места, которое пользователь сам выбрал для сохранения
    fun exportDatabase(outputStreamUri: Uri): Boolean {
        return try {
            val supportDb: SupportSQLiteDatabase = db.openHelper.writableDatabase
            supportDb.query("PRAGMA wal_checkpoint(FULL)").close()
            db.close()

            val dbFile = context.getDatabasePath(databaseName)
            if (!dbFile.exists()) return false

            // Открываем поток записи по выбранному пользователем Uri
            context.contentResolver.openOutputStream(outputStreamUri)?.use { output ->
                dbFile.inputStream().use { input ->
                    input.copyTo(output) // Копируем данные байт в байт
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Импорт: принимает Uri файла бэкапа, который пользователь сам выбрал на телефоне
    fun importDatabase(inputStreamUri: Uri): Boolean {
        return try {
            db.close()

            val dbFile = context.getDatabasePath(databaseName)

            // Читаем данные из выбранного пользователем файла и пишем поверх нашей БД
            context.contentResolver.openInputStream(inputStreamUri)?.use { input ->
                dbFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Удаляем старые журналы-кэши
            File("${dbFile.path}-wal").delete()
            File("${dbFile.path}-shm").delete()

            // Перезапуск
            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
            exitProcess(0)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
    
