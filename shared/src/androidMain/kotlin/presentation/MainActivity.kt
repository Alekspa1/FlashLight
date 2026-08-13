package presentation

import StartApp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import data.repostitory.AndroidPermissionImpl
import org.koin.android.ext.android.inject
import MainViewModel
import android.content.Intent
import android.net.Uri
import android.os.Build

class MainActivity : ComponentActivity() {

    val permissionImp: AndroidPermissionImpl by inject()
    private val mainViewModel: MainViewModel by inject()
     
    // Временные переменные для удержания данных до прогрузки UI
    private var pendingSharedText: String? = null
    private var pendingSharedImageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Вытаскиваем данные во временный кэш вместо прямой отправки во ViewModel
        extractSharedIntent(intent)
         
        permissionImp.initLauncher(this@MainActivity)
        
        setContent {
            // 2. Блок LaunchedEffect(Unit) сработает ТОЛЬКО ТОГДА, когда весь Compose-граф 
            // полностью проинициализировался. Теперь передавать данные во ViewModel безопасно!
            androidx.compose.runtime.LaunchedEffect(Unit) {
                sendPendingDataToViewModel()
            }
            
            StartApp()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Если приложение уже работает, навигация стабильна — вытаскиваем данные
        extractSharedIntent(intent)
        // И сразу отправляем во ViewModel
        sendPendingDataToViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        permissionImp.destroyLaunch()
    }

    // Извлекает данные и складывает в переменные класса MainActivity
    private fun extractSharedIntent(intent: Intent?) {
        if (intent == null || intent.action != Intent.ACTION_SEND) return
        val type = intent.type ?: return

        when {
            type.startsWith("text/") -> {
                pendingSharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                pendingSharedImageUri = null
            }
            type.startsWith("image/") -> {
                val imageUri: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)
                if (imageUri != null) {
                    pendingSharedImageUri = imageUri.toString()
                    pendingSharedText = null
                }
            }
        }
    }

    // Передает данные во ViewModel и очищает кэш в Activity
    private fun sendPendingDataToViewModel() {
        if (pendingSharedText != null || pendingSharedImageUri != null) {
            mainViewModel.openDialogWithSharedData(
                text = pendingSharedText, 
                imageUri = pendingSharedImageUri
            )
            // Очищаем, чтобы при обычном перезапуске или повороте экрана диалог не открывался снова
            pendingSharedText = null
            pendingSharedImageUri = null
        }
    }
}

// class MainActivity : ComponentActivity() {

//     val permissionImp: AndroidPermissionImpl by inject()
//      private val mainViewModel: MainViewModel by inject()
     
//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)

//          handleSharedIntent(intent)
         
//         permissionImp.initLauncher(this@MainActivity)
//         setContent {
//             StartApp()
//         }



//     }

//         override fun onNewIntent(intent: Intent) {
//         super.onNewIntent(intent)
//         handleSharedIntent(intent)
//     }

//     override fun onDestroy() {
//         super.onDestroy()
//         permissionImp.destroyLaunch()
//     }

//         private fun handleSharedIntent(intent: Intent?) {
//         if (intent == null || intent.action != Intent.ACTION_SEND) return
        
//         val type = intent.type ?: return

//         when {
//             // Если поделились текстом
//             type.startsWith("text/") -> {
//                 val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
//                 if (!sharedText.isNullOrBlank()) {
//                     mainViewModel.openDialogWithSharedData(text = sharedText, imageUri = null)
//                 }
//             }
//             // Если поделились картинкой
//             type.startsWith("image/") -> {
//                 // Извлекаем Uri картинки из системного потока
//                 val imageUri: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)
//                 if (imageUri != null) {
//                     mainViewModel.openDialogWithSharedData(text = null, imageUri = imageUri.toString())
//                 }
//             }
//         }
//     }


// }
