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

// Корутины и Архитектура (DI / UI)
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import presentation.MainWeatherPager
import presentation.YandexBannerAd
import presentation.theme.ThemeNeon

@Composable
fun StartApp(viewModel: MainViewModel = koinViewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

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
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        item {
                            NavigationDrawerItem(
                                label = { Text("Повседневные") },
                                selected = true,
                                onClick = {
                                    viewModel.updateCategory("Повседневные")
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }
                        item {
                            NavigationDrawerItem(
                                label = { Text("Общие дела") },
                                selected = false,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }

                        items(
                            items = categories,
                            key = { it.id!! }
                        ) { item ->
                            NavigationDrawerItem(
    label = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Текст теперь занимает всё свободное место слева
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                text = item.name,
                color = Color.Black, // Цвет текста для выбранного/невыбранного состояния можно настроить ниже
                lineHeight = 20.sp,
                fontSize = 18.sp
            )
            
            // Кнопка удаления остается справа и кликается отдельно
            IconButton(
                onClick = { /* Вызов удаления в viewModel */ },
                modifier = Modifier.padding(end = 8.dp).size(35.dp)
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = ThemeNeon().iconDelItem,
                    contentDescription = "Удалить",
                    tint = ThemeNeon().iconDelTint
                )
            }
        }
    },
    selected = false, // Или завязать на состояние: viewModel.selectedCategory.value == item.name
    onClick = {
        viewModel.updateCategory(item.name)
        scope.launch { drawerState.close() }
    },
    // 🎨 Навешиваем рамку, скругление и отступы на сам элемент меню
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 2.dp)
        .clip(RoundedCornerShape(15.dp))
        .border(2.dp, Color.Black, RoundedCornerShape(15.dp)),
    // 🖌️ Задаем белый цвет контейнера (вместо бывшей Card)
    colors = NavigationDrawerItemDefaults.colors(
        unselectedContainerColor = Color.White,
        selectedContainerColor = Color.LightGray // Можно задать другой цвет для выделенного элемента
    )
)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        IconButton(
                            modifier = Modifier
                                        .size(50.dp)
                                        .align(Alignment.CenterEnd),
                            onClick = { /* Логика добавления */ }
                        ) {
                            Icon(
                                imageVector = ThemeNeon().iconAdd,
                                contentDescription = "Добавить",
                                modifier = Modifier.fillMaxSize(),
                                tint = ThemeNeon().iconAddTint
                            )
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
