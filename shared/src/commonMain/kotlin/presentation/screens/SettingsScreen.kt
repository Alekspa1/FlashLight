package presentation.screens

import CommonConst.ALARM_SETTINGS
import CommonConst.APP_SETTINGS
import CommonConst.BATTERY_OPTIMIZATION
import CommonConst.DONATE
import CommonConst.SIZE_LARGE
import CommonConst.SIZE_SETTINGS
import CommonConst.SIZE_SMALL
import CommonConst.SIZE_STANDART
import CommonConst.SORT_SETTINGS
import CommonConst.SORT_STANDART
import CommonConst.SORT_USER
import CommonConst.THEME_FUTURE
import CommonConst.THEME_SETTINGS
import CommonConst.THEME_ZABOR
import CommonConst.THEME_MRAMOR
import CommonConst.THEME_GROZA
import MainViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import presentation.DialogSoundAndroid
import presentation.dialogs.DialogState
import presentation.dialogs.SettingsDialog
import presentation.theme.Size
import presentation.theme.SizeNormal
import presentation.theme.Theme
import presentation.theme.ThemeNeon


@Composable
fun SettingsScreen(
    theme: Theme = ThemeNeon(),
    size: Size = SizeNormal(),
    onBack: () -> Unit = {},
    onClick: (String) -> Unit = {},
    viewModel: MainViewModel,
    innerPadding: PaddingValues
) {

    val listTheme = listOf(THEME_FUTURE, THEME_ZABOR,THEME_MRAMOR,THEME_GROZA)
    val listSize = listOf(SIZE_SMALL, SIZE_STANDART, SIZE_LARGE)
    val listSort = listOf(SORT_STANDART, SORT_USER)
    val listSound by viewModel.soundState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }




    val isLoading by viewModel.isBackupLoading.collectAsStateWithLifecycle()


    if (isLoading) {
        Dialog(onDismissRequest = { /* Заблокировано */ }) {
            Card {
                Column(modifier = Modifier.padding(24.dp)) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Обработка данных, пожалуйста, не закрывайте приложение...")
                }
            }
        }
    }


    LaunchedEffect(Unit) {
        viewModel.toast.collect { message ->
            snackbarHostState.showSnackbar(message)
        }

    }

    val uriHandler = LocalUriHandler.current

    when (viewModel.showDialog.isWho) {
        THEME_SETTINGS -> {
            SettingsDialog(
                title = "Выберите тему",
                select = viewModel.getTheme(),
                listAction = listTheme,
                onClick = { selectedTheme ->
                    viewModel.saveTheme(selectedTheme)
                    viewModel.showDialog = DialogState()
                },
                onCancel = { viewModel.showDialog = DialogState() })
        }

        SIZE_SETTINGS -> {
            SettingsDialog(
                title = "Выберите размер шрифа",
                select = viewModel.getSize(),
                listAction = listSize,
                onClick = { size ->
                    viewModel.saveSize(size)
                    viewModel.showDialog = DialogState()
                },
                onCancel = { viewModel.showDialog = DialogState() })
        }

        SORT_SETTINGS -> {
            SettingsDialog(
                title = "Выберите тип сортировки",
                select = viewModel.sortType.value,
                listAction = listSort,
                onClick = { action ->
                    viewModel.saveSort(action)
                    viewModel.showDialog = DialogState()
                },
                onCancel = { viewModel.showDialog = DialogState() })
        }

        ALARM_SETTINGS -> {
            val uri = viewModel.getUri()
            DialogSoundAndroid(
                selectUri = uri,
                listSound = listSound,
                onClick = { newUri ->
                    viewModel.saveUri(newUri)
                    viewModel.showDialog = DialogState()
                },
                onCancel = { viewModel.showDialog = DialogState() }
            )
        }
    }


        Box(modifier = Modifier.fillMaxSize()) { 
        Image( 
            painter = painterResource(theme.backgroundStart), 
            contentDescription = null, 
            modifier = Modifier.fillMaxSize(), 
            contentScale = ContentScale.FillBounds 
        ) 

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()

            ) {
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
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.testPremium() },
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp) // layout_marginBottom="3dp"
                ) {
                    // --- СЕКЦИЯ 1: СТИЛИ И ИНСТРУКЦИИ ---
                    SettingItem(
                        "Тема",
                        theme,
                        size,
                        theme.cardMenuItem,
                        theme.borderCardMenuItem
                    ) { viewModel.showDialog = DialogState("THEME_SETTINGS") }
                    SettingItem(
                        "Размер шрифта",
                        theme,
                        size,
                        theme.cardMenuItem,
                        theme.borderCardMenuItem
                    ) { viewModel.showDialog = DialogState("SIZE_SETTINGS") }
                    SettingItem(
                        "Сортировка",
                        theme,
                        size,
                        theme.cardMenuItem,
                        theme.borderCardMenuItem,
                        true
                    ) {
                        if(viewModel.premiumState.value) viewModel.showDialog = DialogState(SORT_SETTINGS)
                        else viewModel.sendMessage("Доступно в PREMIUM версии")

                    }
                    if (viewModel.getPlatform == "Android") {
                        SettingItem(
                            "Звук будильника",
                            theme,
                            size,
                            theme.cardMenuItem,
                            theme.borderCardMenuItem,
                            true
                        ) {
                            if(viewModel.premiumState.value) viewModel.permission(ALARM_SETTINGS)
                            else viewModel.sendMessage("Доступно в PREMIUM версии")


                        }
                    }


                    SettingItem(
                        "Инструкция",
                        theme,
                        size,
                        theme.cardMenuItem,
                        theme.borderCardMenuItem
                    ) { onClick("FAQ") }
                    SettingItem(
                        "Обратная связь",
                        theme,
                        size,
                        theme.cardMenuItem,
                        theme.borderCardMenuItem
                    ) { uriHandler.openUri("mailto:apereverzev47@gmail.com?subject=FOCUS") }
                    SettingItem(
                        "Поддержать разработчика",
                        theme,
                        size,
                        theme.cardMenuItem,
                        theme.borderCardMenuItem
                    ) { uriHandler.openUri(DONATE) }

                    if (viewModel.getPlatform == "Android") {
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
                        ) { viewModel.permission(BATTERY_OPTIMIZATION) }
                        SettingItem(
                            "Настройки приложения",
                            theme,
                            size,
                            theme.cardMenuItem,
                            theme.borderCardMenuItem
                        ) { viewModel.permission(APP_SETTINGS) }
                    }

                    // --- СЕКЦИЯ 3: РЕЗЕРВНОЕ КОПИРОВАНИЕ (tv_settings_backup) ---
                    Text(
                        text = "Резервное копирование",
                        color = theme.textColor,
                        fontSize = size.textMenu,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        textAlign = TextAlign.Center
                    )
                    SettingItem(
                        "Сохранить базу данных",
                        theme,
                        size,
                        theme.cardMenuItem,
                        theme.borderCardMenuItem
                    ) {
                viewModel.doExport()
                    }
                    SettingItem(
                        "Загрузить базу данных",
                        theme,
                        size,
                        theme.cardMenuItem,
                        theme.borderCardMenuItem
                    ) {
                       viewModel.doImport()
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
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

@Composable
fun SettingItem(
    text: String,
    theme: Theme,
    size: Size,
    cardColor: Color,
    borderColor: Color,
    iconPremium: Boolean = false,
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
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically){
            Text(
                text = text,
                color = theme.textColor,
                fontSize = size.textItem,
                lineHeight = size.lineHeightItem,
                modifier = Modifier.weight(1f) // padding="8dp"
            )
            if(iconPremium) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Меню",
                    tint = theme.tintPremiumOn
                )
            }



        }

    }
}

