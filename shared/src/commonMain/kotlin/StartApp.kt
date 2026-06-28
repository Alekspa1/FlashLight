import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import presentation.MainWeatherPager

@Composable
fun StartApp() {
    LaunchedEffect(Unit) {
        println("=========================================")
        println("KMP APP LOG: Общий интерфейс успешно запущен!")
        println("=========================================")
    }
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            MainWeatherPager()
        }
    }
}