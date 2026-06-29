import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import flashlight.shared.generated.resources.Res
import flashlight.shared.generated.resources.background_neon

import org.jetbrains.compose.resources.painterResource
import presentation.MainWeatherPager

@Composable
fun StartApp() {

    MaterialTheme {
        // 1. Scaffold сам вычисляет отступы челки, статус-бара и системной полоски снизу
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding -> // <-- Переменная, которая хранит все безопасные отступы

            Box(
                modifier = Modifier
                    .fillMaxSize()
                   ,
                contentAlignment = Alignment.Center
            ) {
                Image(
                    // Вызываем painterResource через объект Res:
                    painter = painterResource(Res.drawable.background_neon),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    // Растягиваем картинку на весь экран без искажения пропорций:
                    contentScale = ContentScale.Crop
                )
                MainWeatherPager(innerPadding)
            }
        }
    }
}
