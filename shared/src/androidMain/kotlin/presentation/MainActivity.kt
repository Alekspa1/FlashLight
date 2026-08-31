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
import data.repostitory.AndroidPlatformFilePickerImpl
import presentation.screens.PremiumScreen


class MainActivity : ComponentActivity() {

    val permissionImp: AndroidPermissionImpl by inject()
    private val mainViewModel: MainViewModel by inject()
    val filePickerImp: AndroidPlatformFilePickerImpl by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleSharedIntent(intent)
        handleNotificationIntent(intent)
        permissionImp.initLauncher(this@MainActivity)
        filePickerImp.initLauncher(this@MainActivity)
        setContent {
            StartApp()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleSharedIntent(intent)
        handleNotificationIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        permissionImp.destroyLaunch()
        filePickerImp.destroyLauncher()
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent == null) return

        val taskId = intent.getIntExtra("TASK_ID", -1)
        if (taskId != -1) {
            // Вызываем новый метод во вьюмодели
            mainViewModel.openDialogByTaskId(taskId)
        }
    }

    private fun handleSharedIntent(intent: Intent?) {
        if (intent == null || intent.action != Intent.ACTION_SEND) return
        val type = intent.type ?: return

        when {
            type.startsWith("text/") -> {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (!sharedText.isNullOrBlank()) {
                    mainViewModel.openDialogWithSharedData(text = sharedText, imageUri = null)
                }
            }

            type.startsWith("image/") -> {
                val imageUri: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)
                imageUri?.let { uri ->
                    try {
                        // 1. Пытаемся закрепить права на чтение.
                        // Это то, для чего нужен был флаг!
                        contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: Exception) {
                        // Если это обычный Share (не из файлового менеджера),
                        // этот вызов может кинуть ошибку, это нормально.
                        // Временных прав от интента всё равно хватит для копирования.
                    }

                    // 2. Отправляем во ViewModel
                    mainViewModel.openDialogWithSharedData(text = null, imageUri = uri.toString())
                }
            }
        }
    }

}
