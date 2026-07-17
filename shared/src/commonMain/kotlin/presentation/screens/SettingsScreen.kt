package presentation.screens

import MainViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BackHand
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BackupTable
import androidx.compose.material.icons.filled.DoorBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import presentation.theme.Size
import presentation.theme.SizeNormal
import presentation.theme.Theme
import presentation.theme.ThemeNeon


@Composable
fun SettingsScreen(
    theme: Theme = ThemeNeon(),
    size : Size = SizeNormal(),
    onBack: () -> Unit = {},
    viewModel : MainViewModel,
) {
val dialogState = viewModel.showDialog 
    
    when(dialogState){
        THEME_SETTINGS-> {ThemeDialog(theme = themeB,
                                      onClick ={selectedTheme->
                                      if(selectedTheme == THEME_FUTURE) viewModel.themeState = ThemeNeon() else viewModel.themeState = ThemeZabor()
                                      dialogState = DialogState()
                                      },
                                      onCancel = {dialogState = DialogState()})}
    }


    Box(modifier = Modifier.fillMaxSize()) {
        // 1. ФОН: android:background="@drawable/img"
        Image(
            painter = painterResource(theme.backgroundStart),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // 2. КОНТЕНТ: ScrollView + fillViewport
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // ШАПКА: im_back (ImageView) + tv_settings ("Общие настройки")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp)
            ) {
                IconButton(
                    onClick = { onBack() },
                    modifier = Modifier.size(35.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Или ваша иконка ic_menu
                        contentDescription = "Меню",
                        tint = theme.iconDelTint
                    )
                }


                Text(
                    text = "Общие настройки", // tv_settings
                    color = theme.textColor,
                    fontSize = size.textMenu,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp) // layout_marginBottom="3dp"
            ) {
                // --- СЕКЦИЯ 1: СТИЛИ И ИНСТРУКЦИИ ---
                SettingItem("Тема", theme, size, theme.cardMenuItem, theme.borderCardMenuItem){dialogState = DialogState("THEME_SETTINGS")}
                SettingItem(
                    "Размер шрифта",
                    theme,
                    size,
                    theme.cardMenuItem,
                    theme.borderCardMenuItem
                ) { /* ... */ }
                SettingItem(
                    "Сортировка",
                    theme,
                    size,
                    theme.cardMenuItem,
                    theme.borderCardMenuItem
                ) { /* ... */ }
                SettingItem(
                    "Звук будильника",
                    theme,
                    size,
                    theme.cardMenuItem,
                    theme.borderCardMenuItem
                ) { /* ... */ }
                SettingItem(
                    "Инструкция",
                    theme,
                    size,
                    theme.cardMenuItem,
                    theme.borderCardMenuItem
                ) { /* ... */ }
                SettingItem(
                    "Обратная связь",
                    theme,
                    size,
                    theme.cardMenuItem,
                    theme.borderCardMenuItem
                ) { /* ... */ }
                SettingItem(
                    "Поддержать разработчика",
                    theme,
                    size,
                    theme.cardMenuItem,
                    theme.borderCardMenuItem
                ) { /* ... */ }

                // --- СЕКЦИЯ 2: РАЗРЕШЕНИЯ (tv_settings_permissions) ---
                Text(
                    text = "Разрешения",
                    color = theme.textColor,
                    fontSize = size.textMenu,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    textAlign = TextAlign.Center
                )
                SettingItem(
                    "Работа в фоне",
                    theme,
                    size,
                    theme.cardMenuItem,
                    theme.borderCardMenuItem
                ) { /* Настройки батареи */ }
                SettingItem(
                    "Настройки приложения",
                    theme,
                    size,
                    theme.cardMenuItem,
                    theme.borderCardMenuItem
                ) { /* Системные настройки аппа */ }

                // --- СЕКЦИЯ 3: РЕЗЕРВНОЕ КОПИРОВАНИЕ (tv_settings_backup) ---
                Text(
                    text = "Резервное копирование",
                    color = theme.textColor,
                    fontSize = size.textMenu,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    textAlign = TextAlign.Center
                )
                SettingItem("Сохранить базу данных", theme, size, theme.cardMenuItem, theme.borderCardMenuItem) {
                    //viewModel.saveDatabase() // Пример вызова во ViewModel
                }
                SettingItem("Загрузить базу данных", theme, size, theme.cardMenuItem, theme.borderCardMenuItem) {
                    // viewModel.loadDatabase()
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // 3. СЛОЙ ИНДИКАТОРА ЗАГРУЗКИ: progressBar2 (Заменяет ProgressBar из XML)
        // Предполагаем, что во ViewModel есть переменная isLoadingState (Boolean)
        // if (viewModel.updateState) { // Или твой флаг загрузки
        //     CircularProgressIndicator(
        //         modifier = Modifier
        //             .size(48.dp)
        //             .align(Alignment.Center),
        //         color = theme.textColor
        //     )
        // }
    }
}

// Карточка элемента настроек (Полностью повторяет button_background_item_category)
@Composable
fun SettingItem(
    text: String,
    theme: Theme,
    size: Size,
    cardColor: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 3.dp) // layout_marginStart/End="3dp"
            .border(3.dp, borderColor, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Text(
            text = text,
            color = theme.textColor,
            fontSize = size.textItem,
            lineHeight = size.lineHeightItem,
            modifier = Modifier.padding(8.dp) // padding="8dp"
        )
    }
}

@Preview
@Composable
fun Prev(){
    SettingsScreen()
}
