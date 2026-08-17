package data.repostitory

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import domain.repostirory.PickerRepository
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.CompletableDeferred

class AndroidPlatformFilePickerImpl() : PickerRepository {

    private var importLauncher: ActivityResultLauncher<Array<String>>? = null
    private var exportLauncher: ActivityResultLauncher<String>? = null

    private var deferredFile: CompletableDeferred<PlatformFile?>? = null

    // 1. ИМПОРТ (Выбор файла)
    override suspend fun openZipPicker(): PlatformFile? {
        val deferred = CompletableDeferred<PlatformFile?>()
        deferredFile = deferred
         importLauncher?.launch(arrayOf("application/zip"))

        return deferred.await()
    }

    // 2. ЭКСПОРТ (Создание файла)
    override suspend fun createZipPicker(defaultName: String): PlatformFile? {
        val deferred = CompletableDeferred<PlatformFile?>()
        deferredFile = deferred

        // Запускаем нативный лаунчер сохранения
        exportLauncher?.launch(defaultName)

        return deferred.await()
    }

    // РЕГИСТРАЦИЯ В АКТИВИТИ (Вызываешь в MainActivity.onCreate)
    fun initLauncher(activity: ComponentActivity) {
        // Регистрируем импорт
        importLauncher = activity.registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            val file = uri?.let { PlatformFile(it, activity) }
            deferredFile?.complete(file)
            deferredFile = null
        }

        // Регистрируем экспорт
        exportLauncher = activity.registerForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri ->
            val file = uri?.let { PlatformFile(it, activity) }
            deferredFile?.complete(file)
            deferredFile = null
        }
    }

    fun destroyLauncher() {
        importLauncher?.unregister()
        exportLauncher?.unregister()
        importLauncher = null
        exportLauncher = null
        if (deferredFile?.isActive == true) deferredFile?.cancel()
        deferredFile = null
    }
}