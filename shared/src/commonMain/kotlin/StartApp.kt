import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import flashlight.shared.generated.resources.Res
import flashlight.shared.generated.resources.background_neon
import flashlight.shared.generated.resources.background_zabor
import flashlight.shared.generated.resources.ic_micro_neon

import org.jetbrains.compose.resources.painterResource
import presentation.MainWeatherPager
import presentation.YandexBannerAd

@Composable
fun StartApp() {
    MaterialTheme {
        // 1. САМЫЙ НИЖНИЙ СЛОЙ: Чистый Box, который намертво растягивает картинку
        Box(
            modifier = Modifier.fillMaxSize() // ТУТ НЕТ И НЕ ДОЛЖНО БЫТЬ .padding(innerPadding)!
        ) {
            Image(
                painter = painterResource(Res.drawable.background_neon),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                // Заставляет картинку заполнить всё окно Windows до последнего пикселя
                contentScale = ContentScale.FillBounds
            )

            // 2. СЛЕДУЮЩИЙ СЛОЙ: Накладываем Scaffold ПОВЕРХ картинки
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent, // Делаем подложку прозрачной, чтобы видеть картинку
                bottomBar = {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .navigationBarsPadding(),
                        contentAlignment = Alignment.Center
                    ) {
                        YandexBannerAd(CommonConst.BANER, Modifier.fillMaxWidth())
                    }

                }
            ) { innerPadding ->
               MainWeatherPager(innerPadding)

            }
        }
    }
}
