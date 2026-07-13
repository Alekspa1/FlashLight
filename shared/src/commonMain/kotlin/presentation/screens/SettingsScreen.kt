package presentation.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun SettingsScreen(onBack : () -> Unit = {} ){


  Text(text = "Экран настроек", color = Color.White)
}
