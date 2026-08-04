package data.repostitory
import CommonConst.ALARM_SETTINGS
import CommonConst.NOTIFICATION
import CommonConst.SOUND
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import domain.repostirory.PermissionRepository
import kotlinx.coroutines.CompletableDeferred


class AndroidPermissionImpl(private val context: Context):PermissionRepository{

    private  var pLauncher: ActivityResultLauncher<String>? = null
    private var deferredPermission : CompletableDeferred<Boolean>? = null

    override fun isChekedPermission(permissionName: String) : Boolean{
   return when(permissionName){
    NOTIFICATION->  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        isPermissionGranted(context, Manifest.permission.POST_NOTIFICATIONS)
    } else {
        true
    }
       ALARM_SETTINGS -> {
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
            isPermissionGranted(context, permission)
        }
       else -> true
    }
  }

  
    override suspend fun requestPermission(permissionName: String) : Boolean{
        val reservDeferred = CompletableDeferred<Boolean>()
        deferredPermission = reservDeferred
    when(permissionName){
        NOTIFICATION ->{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        ALARM_SETTINGS -> {
            val permissionsToRequest =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
                } else {
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                }

            permissionsToRequest.forEach {
                pLauncher?.launch(it)
            }
        }
        else -> {}
    }
        return deferredPermission?.await() ?: true
  }


    private fun isPermissionGranted(con: Context, p: String): Boolean {
        return ContextCompat.checkSelfPermission(con, p) == PackageManager.PERMISSION_GRANTED
    }


    fun initLauncher(activit: ComponentActivity){
        pLauncher?.unregister()
        pLauncher = null
        pLauncher = activit.registerForActivityResult(ActivityResultContracts.RequestPermission()) {isGranted->
        deferredPermission?.complete(isGranted)
        deferredPermission = null
        }
    }

    fun destroyLaunch(){
        pLauncher?.unregister()
        pLauncher = null
        deferredPermission?.apply {
            if (isActive) cancel() // Отменяем ожидание корутины, если Activity уничтожена
        }
        deferredPermission = null
    }
}
