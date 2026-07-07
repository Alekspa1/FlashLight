import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import flashlight.shared.generated.resources.Res
import flashlight.shared.generated.resources.background_neon
import flashlight.shared.generated.resources.background_zabor
import flashlight.shared.generated.resources.ic_micro_neon
import kotlinx.coroutines.launch

import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import presentation.MainWeatherPager
import presentation.YandexBannerAd

// Для работы с LazyColumn и его элементами
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement

// Для стилизации текста (sp и FontWeight)
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

// Для работы делегата 'by' и подписки на StateFlow
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import presentation.theme.ThemeNeon
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight // Нужен для Модификатора weight в Row/Column
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun StartApp(viewModel : MainViewModel = koinViewModel ()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope() // Добавлено для работы с корутинами
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    // Лямбда для открытия Drawer
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
        

                LazyColumn(

            modifier = Modifier.fillMaxWidth().weight(1f),

            verticalArrangement = Arrangement.spacedBy(5.dp),

        ) {
           item() {
                  NavigationDrawerItem(
                    label = { Text("Повседневные") },
                    selected = true,
                    onClick = {
                         viewModel.updateCategory("Повседневные")
                        scope.launch {
                            drawerState.close() }
                    }
                )
            }
           item() {
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
                key = { it.id!!}
            ){item->
                      NavigationDrawerItem(
                    label = {  Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // Все три элемента будут идеально ровно по центру высоты
        ) {
				Card(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp)
                    .weight(1f) // Заставляет карточку занять ВСЁ свободное место между кнопками
                    .clip(RoundedCornerShape(15.dp))
                    .border(
                        2.dp,
                        Color.Black,
                        RoundedCornerShape(15.dp)

                    ).clickable{/*меняю категорию*/}
                    ,

                shape = RoundedCornerShape(16.dp),

                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
           		 ) {
                Text(
                            modifier = Modifier.padding(start = 5.dp,end = 5.dp),
                            text = item.name,
                            color = Color.White,
                            lineHeight = 20.sp,
                            fontSize = 18.sp)
            		}
                 IconButton(

                    onClick = { /*удалить категорию*/  },
                    modifier = Modifier.padding(end = 8.dp).size(35.dp)

                ) {

                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = ThemeNeon().iconDelItem, 
                        contentDescription = "Меню",
                        tint = theme.iconDelTint,

                    )

                }

        } // Конец Row },
                    selected = false,
                    onClick = {
                        viewModel.updateCategory(item.name)
                        scope.launch { drawerState.close() }
                    }
                )  
            }   
           
           
        }
            }
        }
    ) {
        MaterialTheme {
            // 1. САМЫЙ НИЖНИЙ СЛОЙ
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(Res.drawable.background_neon),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

                // 2. СЛЕДУЮЩИЙ СЛОЙ
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    bottomBar = {
                        Box(
                            modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                            contentAlignment = Alignment.Center
                        ) {
                            // Внимание: убедитесь, что CommonConst импортирован в вашем проекте
                            YandexBannerAd(CommonConst.BANER, Modifier.fillMaxWidth())
                        }
                    },
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    MainWeatherPager(innerPadding, viewModel){onOpenDrawer ()}
                }
            }
        }              
    }
}
