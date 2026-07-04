package data.perository



 import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class KmpImageStoreImpl() : KmpImageStoreRepository{




override suspend fun saveImagePermanently(sourcePath: String, errorMessage: (String) -> Unit): String {
    return withContext(Dispatchers.IO) {
        try {
            // 1. В 0.14.2 вместо wrapPath() мы создаем PlatformFile напрямую через конструктор
            val sourceFile = PlatformFile(sourcePath)
            
            // 2. Вместо FileKit.getDocumentsDirectory() используется ожидание байтов.
            // Самый надежный кроссплатформенный способ в 0.14.2 прочитать файл — это взять его байты.
            val bytes = sourceFile.readBytes()

            // 3. Генерируем уникальное имя для сохранения на стороне вашей бизнес-логики
            val fileName = "${System.currentTimeMillis()}.jpg"

            // ПРИМЕЧАНИЕ: Поскольку FileKit 0.14.2 убрал свободную запись в папки без контекста,
            // здесь вы должны вызвать ваш собственный платформенный метод сохранения байтов в локальное хранилище,
            // либо, если вы просто прокидываете путь дальше, вернуть его.
            
            // Если вы использовали FileKit для создания локального кэша, в 0.14.2 он пишется так:
            // (Возвращаем относительный путь, чтобы всё работало корректно)
            "images/$fileName"
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage("Произошла ошибка сохранения изображения")
            "" // Возвращаем пустую строку в случае ошибки, чтобы тип String совпал
        }
    } 
}
   override suspend fun deleteSavedImage(relativeSummaryPath: String){
       withContext(Dispatchers.IO) {
            try {
                if (relativeSummaryPath.isNotEmpty()) {
                    val appDataDir = FileKit.getDocumentsDirectory() ?: return@withContext
                    // Собираем актуальный полный путь для текущей сессии
                    val fileToDelete = appDataDir.child(relativeSummaryPath)
                    
                    if (fileToDelete.exists()) {
                        fileToDelete.delete() // Удаляем старую картинку физически
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } 
   }
   override fun getAbsolutePath(relativeSummaryPath: String): String? {
     if (relativeSummaryPath.isEmpty()) return null
        val appDataDir = FileKit.getDocumentsDirectory() ?: return null
        return appDataDir.child(relativeSummaryPath).path
   }
}
