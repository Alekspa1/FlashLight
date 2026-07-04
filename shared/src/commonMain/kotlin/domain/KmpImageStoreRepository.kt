package domain

interface KmpImageStoreRepository{

   suspend fun saveImagePermanently(sourcePath: String,errorMessage : (String) -> Unit): String
   suspend fun deleteSavedImage(relativeSummaryPath: String)
   fun getAbsolutePath(relativeSummaryPath: String): String?
   
}
