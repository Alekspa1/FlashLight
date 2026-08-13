package presentation

import StartApp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import data.repostitory.AndroidPermissionImpl
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {

    val permissionImp: AndroidPermissionImpl by inject()
     private val mainViewModel: MainViewModel by inject()
     
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         handleSharedIntent(intent)
         
        permissionImp.initLauncher(this@MainActivity)
        setContent {
            StartApp()
        }



    }

        override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleSharedIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        permissionImp.destroyLaunch()
    }

        private fun handleSharedIntent(intent: Intent?) {
        if (intent == null || intent.action != Intent.ACTION_SEND) return
        
        val type = intent.type ?: return

        when {
            // Если поделились текстом
            type.startsWith("text/") -> {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (!sharedText.isNullOrBlank()) {
                    mainViewModel.openDialogWithSharedData(text = sharedText, imageUri = null)
                }
            }
            // Если поделились картинкой
            type.startsWith("image/") -> {
                // Извлекаем Uri картинки из системного потока
                val imageUri: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)
                if (imageUri != null) {
                    mainViewModel.openDialogWithSharedData(text = null, imageUri = imageUri.toString())
                }
            }
        }
    }


}
