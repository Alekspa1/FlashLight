package presentation

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class WarmupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Отключаем анимацию ОТКРЫТИЯ при создании экрана
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Для Android 14 (API 34) и выше
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
        } else {
            // Для старых версий (подавляем варнинг, так как для них это легальный метод)
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }

        // Закрываем Activity через 350 миллисекунд
        Handler(Looper.getMainLooper()).postDelayed({
            finish()

            // 2. Отключаем анимацию ЗАКРЫТИЯ при выходе
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
            } else {
                @Suppress("DEPRECATION")
                overridePendingTransition(0, 0)
            }
        }, 350)
    }
}