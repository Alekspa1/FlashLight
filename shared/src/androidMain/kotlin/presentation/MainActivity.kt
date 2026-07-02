package presentation

import StartApp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import data.repostitory.AndroidPermissionImpl
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {

    val permissionImp: AndroidPermissionImpl by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionImp.initLauncher(this@MainActivity)
        setContent {
            StartApp()
        }



    }

    override fun onDestroy() {
        super.onDestroy()
        permissionImp.destroyLaunch()
    }


}
