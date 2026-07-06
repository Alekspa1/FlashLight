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

@Composable
fun StartApp(viewModel : MainViewModel = koinViewModel ()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope() // Добавлено для работы с корутинами
    
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
                Text("Повседневные", modifier = Modifier.padding(16.dp))
                Text("Общие дела", modifier = Modifier.padding(16.dp))
                NavigationDrawerItem(
                    label = { Text("Настройки") },
                    selected = false,
                    onClick = {
                        // Исправлено: закрываем drawer через scope
                        scope.launch { drawerState.close() }
                    }
                )
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
