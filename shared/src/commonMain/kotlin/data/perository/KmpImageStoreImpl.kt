package data.perository

import io.github.vinceglb.filekit.core.FileKit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class KmpImageStoreImpl() : KmpImageStoreRepository{


   override suspend fun saveImagePermanently(sourcePath: String,errorMessage : (String) -> Unit): String{
      return withContext(Dispatchers.IO) {
            try {
                // Оборачиваем временный путь (из кэша/галереи) в объект библиотеки
                val sourceFile = FileKit.wrapPath(sourcePath)
                
                // Получаем постоянную защищенную папку документов вашего приложения
                val appDataDir = FileKit.getDocumentsDirectory() ?: return@withContext ""
                val imagesDir = appDataDir.child("images")
                
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs() // Создаем папку "images", если ее еще нет
                }

                // Генерируем уникальное имя для нового постоянного файла
                val fileName = "${System.currentTimeMillis()}.jpg"
                val destinationFile = imagesDir.child(fileName)
                
                // Читаем байты из временного источника и НАВСЕГДА записываем в свой файл
                // После этого оригинал в галерее нам больше не нужен
                destinationFile.writeBytes(sourceFile.readBytes())

                // Возвращаем только относительный путь, чтобы iOS не ломал его после перезапуска
                "images/$fileName" 
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage("Произошла ошибка сохранния изображения")
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
