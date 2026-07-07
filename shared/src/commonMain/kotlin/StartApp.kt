import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
// Звездочка гарантирует импорт ВСЕХ модификаторов размеров (weight, fillMaxSize, padding, size, Arrangement и т.д.)
import androidx.compose.foundation.layout.* 
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// Ресурсы JetBrains Compose
import flashlight.shared.generated.resources.Res
import flashlight.shared.generated.resources.background_neon
import flashlight.shared.generated.resources.background_zabor
import flashlight.shared.generated.resources.ic_micro_neon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import androidx.compose.material3.NavigationDrawerItemDefaults
// Корутины и Архитектура (DI / UI)
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import presentation.MainWeatherPager
import presentation.YandexBannerAd
import presentation.theme.ThemeNeon
import androidx.compose.foundation.background
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.filled.MyLocation 
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.Icons

@Composable
fun StartApp(viewModel: MainViewModel = koinViewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val CardSolidColor = Color(0x6500BCD4)  // solid android:color
    val BorderNeonColor = Color(0x9900E2FF) // @color/vPagerCant

    val onOpenDrawer = remember {
        {
            scope.launch {
                drawerState.open()
            }
            Unit
        }
    }

    viewModel.updateAlarm()
    LaunchedEffect(Unit) {
        viewModel.toast.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
    ModalDrawerSheet(
        modifier = Modifier.fillMaxWidth(0.8f),
        drawerContainerColor = Color.Transparent // Позволит увидеть фон, если шторка кастомная
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            
            // СЛОЙ 1: Ваша фоновая неоновая картинка
            Image(
                painter = painterResource(Res.drawable.background_neon),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. ЗАГОЛОВОК ШТОРКИ (tvCategoryDrawer из XML)
            Text(
                text = "Категории",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            // 2. ДИНАМИЧЕСКИЙ СПИСОК (Заменяет RecyclerView и системные пункты)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Прижимает всё, что ниже, к нижнему краю
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                // Категория: Повседневные
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
    modifier = Modifier
        .weight(1f)
        .clip(RoundedCornerShape(10.dp)) // corners android:radius="10dp"
        // 1. Задаем фоновый цвет карточки
        .background(CardSolidColor) 
        // 2. Рисуем рамку толщиной 3dp
        .border(3.dp, BorderNeonColor, RoundedCornerShape(10.dp)) 
        // 3. Добавляем клик (эффект волны подстроится под форму автоматически)
        .clickable { 
            viewModel.updateCategory("Повседневные")
            scope.launch { drawerState.close() }
        },
    shape = RoundedCornerShape(10.dp),
    // Прозрачный контейнер у Card обязателен, чтобы работал наш кастомный background
    colors = CardDefaults.cardColors(containerColor = Color.Transparent) 
) {
                            Text(
                                text = "Повседневные",
                                color = Color.Black,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                        IconButton(
                            onClick = { /* Логика меню */ },
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp).size(35.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation, // Или ваша иконка ic_menu
                                contentDescription = "Меню",
                                tint = ThemeNeon().iconDelTint
                            )
                        }
                    }
                }

                // Категория: Общие дела
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
    modifier = Modifier
        .weight(1f)
        .clip(RoundedCornerShape(10.dp)) // corners android:radius="10dp"
        // 1. Задаем фоновый цвет карточки
        .background(CardSolidColor) 
        // 2. Рисуем рамку толщиной 3dp
        .border(3.dp, BorderNeonColor, RoundedCornerShape(10.dp)) 
        // 3. Добавляем клик (эффект волны подстроится под форму автоматически)
        .clickable { 
            
            scope.launch { drawerState.close() }
        },
    shape = RoundedCornerShape(10.dp),
    // Прозрачный контейнер у Card обязателен, чтобы работал наш кастомный background
    colors = CardDefaults.cardColors(containerColor = Color.Transparent) 
) {
                            Text(
                                text = "Общие дела",
                                color = Color.Black,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                        IconButton(
                            onClick = { /* Логика Поделиться */ },
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp).size(35.dp)
                        ) {
                            Icon(
                                 imageVector = Icons.Default.Share, // Замените на иконку Share, если добавлена в тему
                                contentDescription = "Поделиться",
                                tint = ThemeNeon().iconAddTint
                            )
                        }
                    }
                }

                // Кастомные категории из базы данных (бывший RecyclerView)
                items(
                    items = categories,
                    key = { it.id!! }
                ) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
    modifier = Modifier
        .weight(1f)
        .clip(RoundedCornerShape(10.dp)) // corners android:radius="10dp"
        // 1. Задаем фоновый цвет карточки
        .background(CardSolidColor) 
        // 2. Рисуем рамку толщиной 3dp
        .border(3.dp, BorderNeonColor, RoundedCornerShape(10.dp)) 
        // 3. Добавляем клик (эффект волны подстроится под форму автоматически)
        .clickable { 
            viewModel.updateCategory(item.name)
            scope.launch { drawerState.close() }
        },
    shape = RoundedCornerShape(10.dp),
    // Прозрачный контейнер у Card обязателен, чтобы работал наш кастомный background
    colors = CardDefaults.cardColors(containerColor = Color.Transparent) 
) {
                            Text(
                                text = item.name,
                                color = Color.Black,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                        IconButton(
                            onClick = { /* Удаление категории */ },
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp).size(35.dp)
                        ) {
                            Icon(
                                modifier = Modifier.fillMaxSize(),
                                imageVector = ThemeNeon().iconDelItem,
                                contentDescription = "Удалить",
                                tint = ThemeNeon().iconDelTint
                            )
                        }
                    }
                }
            }

            // 3. КНОПКА ДОБАВЛЕНИЯ (imBAddMenu из XML - над нижним меню)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                IconButton(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.CenterEnd),
                    onClick = { /* Логика добавления новой категории */ }
                ) {
                    Icon(
                        imageVector = ThemeNeon().iconAdd,
                        contentDescription = "Добавить категорию",
                        modifier = Modifier.fillMaxSize(),
                        tint = ThemeNeon().iconAddTint
                    )
                }
            }

            // 4. НИЖНЕЕ МЕНЮ (Заменяет LinearLayout @id/drawerL из XML)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Полоска 1
                HorizontalDivider(thickness = 2.dp, color = Color.White)
                
                // Кнопка Премиум
                Text(
                    text = "Премиум отключен", // Отвечает tvNewPremium
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Логика Премиума */ }
                        .padding(vertical = 10.dp)
                )

                // Полоска 2
                HorizontalDivider(thickness = 2.dp, color = Color.White)

                // Кнопка Обновления
                Text(
                    text = "Обновлений нет", // Отвечает tvNewUpgrate
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Проверка обновлений */ }
                        .padding(vertical = 10.dp)
                )

                // Полоска 3
                HorizontalDivider(thickness = 2.dp, color = Color.White)

                // Кнопка Настройки
                Text(
                    text = "Настройки", // Отвечает tvNewSettings
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Открыть настройки */ }
                        .padding(vertical = 10.dp)
                )

                // Полоска 4
                HorizontalDivider(thickness = 2.dp, color = Color.White)
            }
        }
        }
    }
}
    ) {
        MaterialTheme {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(Res.drawable.background_neon),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    bottomBar = {
                        Box(
                            modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                            contentAlignment = Alignment.Center
                        ) {
                            YandexBannerAd(CommonConst.BANER, Modifier.fillMaxWidth())
                        }
                    },
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    MainWeatherPager(innerPadding, viewModel) { onOpenDrawer() }
                }
            }
        }
    }
}
