package presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import MainViewModel
import presentation.theme.Size
import presentation.theme.Theme
import presentation.theme.ThemeNeon
import androidx.compose.material3.Icon
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val theme = viewModel.themeState
    val size = viewModel.sizeState
    
    // Подтягиваем твои неоновые цвета (как в шторке)
    val cardSolidColor = Color(0x6500BCD4)
    val borderNeonColor = Color(0x9900E2FF)

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
                .verticalScroll(rememberScrollState())
        ) {
            // ШАПКА: im_back (ImageView) + tv_settings ("Общие настройки")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp) // layout_width/height="40dp"
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                ) {
                    IconButton(
                                                    onClick = { /* Логика меню */ },
                                                    modifier = Modifier.size(35.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = theme.iconDrawerEveryday, // Или ваша иконка ic_menu
                                                        contentDescription = "Меню",
                                                        tint = ThemeNeon().iconDelTint
                                                    )
                                                }
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

            // Твой основной LinearLayout (paddingStart/End="10dp")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp) // layout_marginBottom="3dp"
            ) {
                // --- СЕКЦИЯ 1: СТИЛИ И ИНСТРУКЦИИ ---
                SettingItem("Тема", theme, size, cardSolidColor, borderNeonColor) {
                    // Твоя рабочая логика переключения неона
                    viewModel.themeState = if (theme == theme) theme else theme 
                }
                SettingItem("Размер шрифта", theme, size, cardSolidColor, borderNeonColor) { /* ... */ }
                SettingItem("Сортировка", theme, size, cardSolidColor, borderNeonColor) { /* ... */ }
                SettingItem("Звук будильника", theme, size, cardSolidColor, borderNeonColor) { /* ... */ }
                SettingItem("Инструкция", theme, size, cardSolidColor, borderNeonColor) { /* ... */ }
                SettingItem("Обратная связь", theme, size, cardSolidColor, borderNeonColor) { /* ... */ }
                SettingItem("Поддержать разработчика", theme, size, cardSolidColor, borderNeonColor) { /* ... */ }

                // --- СЕКЦИЯ 2: РАЗРЕШЕНИЯ (tv_settings_permissions) ---
                Text(
                    text = "Разрешения",
                    color = theme.textColor,
                    fontSize = size.textMenu,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    textAlign = TextAlign.Center
                )
                SettingItem("Работа в фоне", theme, size, cardSolidColor, borderNeonColor) { /* Настройки батареи */ }
                SettingItem("Настройки приложения", theme, size, cardSolidColor, borderNeonColor) { /* Системные настройки аппа */ }

                // --- СЕКЦИЯ 3: РЕЗЕРВНОЕ КОПИРОВАНИЕ (tv_settings_backup) ---
                Text(
                    text = "Резервное копирование",
                    color = theme.textColor,
                    fontSize = size.textMenu,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    textAlign = TextAlign.Center
                )
                SettingItem("Сохранить базу данных", theme, size, cardSolidColor, borderNeonColor) { 
                    //viewModel.saveDatabase() // Пример вызова во ViewModel
                }
                SettingItem("Загрузить базу данных", theme, size, cardSolidColor, borderNeonColor) { 
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
