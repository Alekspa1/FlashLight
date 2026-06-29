package presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import data.initKoin
import org.koin.android.ext.koin.androidContext

class App : Application() {

    override fun onCreate() {

        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        initKoin {

            androidContext(this@App)

        }

    }
}