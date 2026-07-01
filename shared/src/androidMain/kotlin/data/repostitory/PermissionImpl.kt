package data.repostitory
import domain.repostirory.PermissionRepository



class PermissionImpl(context: Context):PermissionRepository{
  override isChekedPermission(permisson: String) : Boolean{
   return when(permisson){
    NOTIFICATION-> isPermissionGranted(context, Manifest.permission.POST_NOTIFICATIONS)
    else true
    }
  }

  
  override fun requestPermission(permisson: String) : Boolean{}


 private fun isPermissionGranted(con: Context, p: String): Boolean {
        return ContextCompat.checkSelfPermission(con, p) == PackageManager.PERMISSION_GRANTED
    }
}
