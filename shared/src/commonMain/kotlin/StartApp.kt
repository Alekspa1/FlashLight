import CommonConst.DELETE_DIALOG_CATEGORY
import CommonConst.INSERT_DIALOG_CATEGORY
import CommonConst.PREMIUM_CLICK
import CommonConst.SETTINGS_CLICK
import CommonConst.SHARED_ClICK
import CommonConst.UPGRATE_CLICK
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import data.room.model.ListCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import presentation.MainPager
import presentation.dialogs.AddOrChangeCategoryDialog
import presentation.dialogs.DeleteDialog
import presentation.dialogs.DialogState
import presentation.screens.Faq
import presentation.screens.PremiumScreen
import presentation.screens.SettingsScreen
import presentation.theme.Size
import presentation.theme.SizeNormal
import presentation.theme.Theme
import presentation.theme.ThemeNeon
import presentation.theme.ThemeZabor

@Composable
fun StartApp(viewModel: MainViewModel = koinViewModel()) {
    val theme = viewModel.themeState
    val size = viewModel.sizeState
    val premiumState by viewModel.premiumState.collectAsStateWithLifecycle()
    val navController = rememberNavController()


    MaterialTheme(
        colorScheme = if (viewModel.themeState == ThemeNeon()) darkColorScheme(
            primary = theme.textColor,
            surfaceContainerHigh = theme.backgroundDialog,
            primaryContainer = Color(0xFF616161),
            onPrimaryContainer = theme.textColor,
            surfaceContainerHighest = Color(0xFF616161),
            onSurfaceVariant = theme.textColor

        ) else lightColorScheme(
            primary = theme.textColor
        )
    ) {

        val categories by viewModel.categories.collectAsStateWithLifecycle()
        val updateState by viewModel.updateState.collectAsStateWithLifecycle()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        var isCommonMode by remember { mutableStateOf(false) }
        val category = viewModel.showDialog.category
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.toast.collect { message ->
                snackbarHostState.showSnackbar(message)
            }
        }
        when (viewModel.showDialog.isWho) {
            DELETE_DIALOG_CATEGORY -> {
                DeleteDialog(theme = theme) { result ->
                    if (result && category != null) {
                        viewModel.deleteCategory(category)
                        viewModel.updateCategory("Повседневные")
                        viewModel.showDialog = DialogState()
                    } else {
                        viewModel.showDialog = DialogState()
                    }
                }
            }

            INSERT_DIALOG_CATEGORY -> {
                AddOrChangeCategoryDialog(
                    category = category,
                    theme = theme,
                    onSave = { listCategory, name ->
                        if (listCategory == null) viewModel.insertCategory(name)
                        else viewModel.upgrateListCategory(listCategory, name)
                        viewModel.showDialog = DialogState()
                    },
                    onCancel = { viewModel.showDialog = DialogState() })
            }
        }
        viewModel.updateAlarm()



        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                Box(
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                    contentAlignment = Alignment.Center
                ) {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )


                }
            }
        ) { innerPadding -> 
    Box(modifier = Modifier.fillMaxSize()) { 
        Image( 
            painter = painterResource(theme.backgroundStart), 
            contentDescription = null, 
            modifier = Modifier.fillMaxSize(), 
            contentScale = ContentScale.FillBounds 
        ) 
        
        NavHost( 
            navController = navController, 
            startDestination = "main_screen"
        ) { 
            // ЭКРАН №1: Главный экран
            composable( 
                route = "main_screen", 
                // Старый экран замирает на месте и не исчезает, пока новый заезжает поверх
                exitTransition = { androidx.compose.animation.ExitTransition.None },
                popEnterTransition = { androidx.compose.animation.EnterTransition.None }
            ) { 
                StartAppContent( 
                    isCommonMode = isCommonMode, 
                    onToggleCommonMode = { isCommonMode = !isCommonMode }, 
                    categories = categories, 
                    toastEvents = viewModel.toast, 
                    updateCategory = { category -> viewModel.updateCategory(category) }, 
                    openPager = { onOpenDrawer, pagerState -> MainPager(innerPadding, viewModel, onOpenDrawer, pagerState) }, 
                    drawerState = drawerState, 
                    theme = viewModel.themeState, 
                    size = viewModel.sizeState, 
                    premium = premiumState, 
                    update = updateState, 
                    onClick = { click -> 
                        when (click) { 
                            PREMIUM_CLICK -> { navController.navigate("premium_screen") } 
                            UPGRATE_CLICK -> {} 
                            SETTINGS_CLICK -> { navController.navigate("settings_screen") } 
                            SHARED_ClICK -> { viewModel.sendMessage("Общие дела появяться в следующих обновлениях") } 
                        } 
                    }, 
                    onClickCategory = { listCategory, action -> 
                        when (action) { 
                            INSERT_DIALOG_CATEGORY -> { 
                                if (premiumState) { 
                                    viewModel.showDialog = DialogState(INSERT_DIALOG_CATEGORY, category = listCategory) 
                                } else viewModel.sendMessage("Категории доступны в PREMIUM версии") 
                            } 
                            DELETE_DIALOG_CATEGORY -> { 
                                viewModel.showDialog = DialogState(DELETE_DIALOG_CATEGORY, category = listCategory) 
                            } 
                        } 
                    } 
                ) 
            } 

            // ЭКРАН №2: Настройки
            composable( 
                route = "settings_screen", 
                enterTransition = { slideInHorizontally(animationSpec = tween(300), initialOffsetX = { -it }) }, 
                popExitTransition = { slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { -it }) },
                // Настройки тоже должны замереть, когда поверх них открывается FAQ
                exitTransition = { androidx.compose.animation.ExitTransition.None },
                popEnterTransition = { androidx.compose.animation.EnterTransition.None }
            ) { 
                SettingsScreen( 
                    theme = theme, 
                    size = size, 
                    onBack = { navController.popBackStack() }, 
                    onClick = { click -> 
                        when (click) { "FAQ" -> navController.navigate("faq_screen") } 
                    }, 
                    viewModel = viewModel, 
                    innerPadding = innerPadding 
                ) 
            } 

            // ЭКРАН №3: FAQ
            composable( 
                route = "faq_screen", 
                enterTransition = { slideInHorizontally(animationSpec = tween(300), initialOffsetX = { -it }) }, 
                popExitTransition = { slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { -it }) },
                exitTransition = { androidx.compose.animation.ExitTransition.None },
                popEnterTransition = { androidx.compose.animation.EnterTransition.None }
            ) { 
                Faq( 
                    theme = theme, 
                    size = size, 
                    onBack = { navController.popBackStack() }, 
                    innerPadding = innerPadding 
                ) 
            } 

            // ЭКРАН №4: Премиум
            composable( 
                route = "premium_screen", 
                enterTransition = { slideInHorizontally(animationSpec = tween(300), initialOffsetX = { -it }) }, 
                popExitTransition = { slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { -it }) },
                exitTransition = { androidx.compose.animation.ExitTransition.None },
                popEnterTransition = { androidx.compose.animation.EnterTransition.None }
            ) { 
                PremiumScreen(size = size, theme = theme, onBack = { navController.popBackStack() }, innerPadding = innerPadding) 
            } 
        } 
    } 
}


    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StartAppContent(
    isCommonMode: Boolean = false,
    onToggleCommonMode: () -> Unit = {},
    categories: List<ListCategory> = emptyList(),
    toastEvents: Flow<String> = emptyFlow(),
    updateCategory: (String) -> Unit = {},
    openPager: @Composable (onOpenDrawer: () -> Unit, pagerStateUp: PagerState) -> Unit = { _, _ -> },
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    theme: Theme = ThemeNeon(),
    size: Size = SizeNormal(),
    premium: Boolean = false,
    update: Boolean = false,
    onClick: (String) -> Unit = {}, // Лямбда для клика
    onClickCategory: (category: ListCategory?, action: String) -> Unit = { _, _ -> }, // Лямбда для клика
) {
    val localNavController = rememberNavController()
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(pageCount = { 3 })
    val onOpenDrawer = remember {
        {
            scope.launch {
                drawerState.open()
            }
            Unit
        }
    }


    if (!LocalInspectionMode.current) {
        val navigationEventState = rememberNavigationEventState(
            currentInfo = NavigationEventInfo.None
        )
        NavigationBackHandler(
            state = navigationEventState,
            isBackEnabled = drawerState.isOpen, // Работает только когда drawer открыт
            onBackCompleted = {
                scope.launch {
                    drawerState.close() // Закрываем drawer при успешном нажатии/жесте "Назад"
                }
            }
        )
    }


    Box(modifier = Modifier.fillMaxSize()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    drawerContainerColor = Color.Transparent
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {

                  
                        Image(
                            painter = painterResource(theme.backgroundDrawer),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillBounds
                        )
                        Column(
                            modifier = Modifier.fillMaxSize()
                                .padding(8.dp)

                        ) {
                            // 1. ЗАГОЛОВОК ШТОРКИ (tvCategoryDrawer из XML)
                            Text(
                                text = if (!isCommonMode) "Категории" else "Общие дела",
                                color = theme.textColor,
                                fontSize = size.textMenu,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
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
                                if (!isCommonMode) {
                                    item {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Card(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .border(
                                                        3.dp,
                                                        theme.borderCardMenuItem,
                                                        RoundedCornerShape(10.dp)
                                                    )
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .clickable {
                                                        updateCategory("Повседневные")
                                                        scope.launch {
                                                            launch { drawerState.close() }
                                                            launch {
                                                                pagerState.animateScrollToPage(
                                                                    1
                                                                )
                                                            }
                                                        }
                                                    },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = CardDefaults.cardColors(containerColor = theme.cardMenuItem)
                                                // colors = CardDefaults.cardColors(containerColor = cardSolidColor)
                                            ) {
                                                Text(
                                                    modifier = Modifier.padding(8.dp),
                                                    text = "Повседневные",
                                                    color = theme.textColor,
                                                    lineHeight = size.lineHeightItem,
                                                    fontSize = size.textItem
                                                )
                                            }
                                            IconButton(
                                                onClick = { /* Логика меню */ },
                                                modifier = Modifier.size(35.dp)
                                            ) {
                                                Icon(
                                                    imageVector = theme.iconDrawerEveryday, // Или ваша иконка ic_menu
                                                    contentDescription = "Меню",
                                                    tint = theme.iconTint
                                                )
                                            }
                                        }
                                    }
                                }

                                // Категория: Общие дела
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                // .clip(RoundedCornerShape(10.dp)) // corners android:radius="10dp"
                                                // // 1. Задаем фоновый цвет карточки
                                                // .background(theme.cardMenuItem)
                                                // // 2. Рисуем рамку толщиной 3dp
                                                .border(
                                                    3.dp,
                                                    theme.borderCardMenuItem,
                                                    RoundedCornerShape(10.dp)
                                                )
                                                // 3. Добавляем клик (эффект волны подстроится под форму автоматически)
                                                .clickable {
                                                    onClick(SHARED_ClICK)
//                                                    onToggleCommonMode()
//
//                                                    // 2. Рассчитываем роуты на основе инвертированного значения (так как стейт обновится на следующем кадре)
//                                                    val nextMode = !isCommonMode
//                                                    val targetRoute =
//                                                        if (nextMode) "common_screen" else "personal_pager_hub"
//                                                    val popUpRoute =
//                                                        if (nextMode) "personal_pager_hub" else "common_screen"
//
//                                                    localNavController.navigate(targetRoute) {
//                                                        popUpTo(popUpRoute) { inclusive = true }
//                                                        launchSingleTop = true
//                                                    }

                                                },
                                            shape = RoundedCornerShape(10.dp),
                                            
                                            colors = CardDefaults.cardColors(containerColor = theme.cardMenuItem)
                                        ) {
                                            Text(
                                                modifier = Modifier.padding(8.dp),
                                                text = if (isCommonMode) "Личные дела" else "Общие дела",
                                                color = theme.textColor,
                                                lineHeight = size.lineHeightItem,
                                                fontSize = size.textItem
                                            )
                                        }
                                        IconButton(
                                            onClick = { },
                                            modifier = Modifier.size(35.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isCommonMode) theme.iconDrawerEveryday
                                                else theme.iconDrawerShare, // Замените на иконку Share, если добавлена в тему
                                                contentDescription = "Поделиться",
                                                tint = theme.iconTint
                                            )
                                        }
                                    }
                                }

                                if (!isCommonMode) {
                                    items(
                                        items = categories,
                                        key = { it.id!! }
                                    ) { category ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Card(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    // .clip(RoundedCornerShape(10.dp)) // corners android:radius="10dp"
                                                    // // 1. Задаем фоновый цвет карточки
                                                    // .background(theme.cardMenuItem)
                                                    // 2. Рисуем рамку толщиной 3dp
                                                    .border(
                                                        3.dp,
                                                        theme.borderCardMenuItem,
                                                        RoundedCornerShape(10.dp)
                                                    )
                                                    // 3. Добавляем клик (эффект волны подстроится под форму автоматически)
                                                    .combinedClickable(
                                                        onClick = {
                                                            scope.launch {
                                                                launch { drawerState.close() }
                                                                launch {
                                                                    pagerState.animateScrollToPage(
                                                                        1
                                                                    )
                                                                }
                                                            }
                                                            updateCategory(category.name)
                                                        },
                                                        onLongClick = {
                                                            onClickCategory(
                                                                category,
                                                                INSERT_DIALOG_CATEGORY
                                                            )
                                                        }
                                                    ),
                                                shape = RoundedCornerShape(10.dp),
                                               
                                                colors = CardDefaults.cardColors(containerColor = theme.cardMenuItem)
                                            ) {
                                                Text(
                                                    modifier = Modifier.padding(8.dp),
                                                    text = category.name,
                                                    color = theme.textColor,
                                                    lineHeight = size.lineHeightItem,
                                                    fontSize = size.textItem
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    onClickCategory(
                                                        category,
                                                        DELETE_DIALOG_CATEGORY
                                                    )
                                                },
                                                modifier = Modifier.size(35.dp)
                                            ) {
                                                Icon(
                                                    modifier = Modifier.fillMaxSize(),
                                                   // imageVector = ThemeNeon().iconDelItem,
                                                    imageVector = theme.iconDelItem,
                                                    contentDescription = "Удалить",
                                                    tint = theme.iconDelTint
                                                )
                                            }
                                        }
                                    }
                                } else item { Text("Временный элемент") }
                            }


                            // 3. КНОПКА ДОБАВЛЕНИЯ (imBAddMenu из XML - над нижним меню)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                IconButton(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .align(Alignment.CenterEnd),
                                    onClick = { onClickCategory(null, INSERT_DIALOG_CATEGORY) }
                                ) {
                                    Icon(
                                        imageVector = ThemeNeon().iconAdd,
                                        contentDescription = "Добавить категорию",
                                        modifier = Modifier.fillMaxSize(),
                                        tint = theme.iconAddTint
                                    )
                                }
                            }

                            // 4. НИЖНЕЕ МЕНЮ (Заменяет LinearLayout @id/drawerL из XML)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Полоска 1
                                HorizontalDivider(thickness = 2.dp, color = theme.iconTint)

                                // Кнопка Премиум
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .clickable { onClick(PREMIUM_CLICK) }
                                        .padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = theme.iconDrawerPremium,
                                        contentDescription = "Добавить категорию",

                                        tint = if (premium) theme.tintPremiumOn else theme.tintPremiumOff
                                    )
                                    Text(
                                        text = if (premium) "PREMIUM версия активировна"
                                        else "Активировать PREMIUM версию", // Отвечает tvNewPremium
                                        color = theme.textColor,
                                        fontSize = size.drawerBottomMenuText,
                                        modifier = Modifier
                                            .fillMaxWidth().padding(start = 4.dp),
                                        fontWeight = FontWeight.Bold

                                    )
                                }


                                // Полоска 2
                                HorizontalDivider(thickness = 2.dp, color = theme.iconTint)

                                // Кнопка Обновления
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .clickable { onClick(UPGRATE_CLICK) }
                                        .padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (update) theme.iconDrawerUpdateOn else theme.iconDrawerUpdateOff,
                                        contentDescription = "Обновление",

                                        tint = if (update) theme.tintPremiumOn else theme.iconTint
                                    )
                                    Text(
                                        text = if (update) "Вышло обновление" else "Обновлений нет", // Отвечает tvNewPremium
                                        color = theme.textColor,
                                        fontSize = size.drawerBottomMenuText,
                                        modifier = Modifier
                                            .fillMaxWidth().padding(start = 4.dp)

                                        ,
                                        fontWeight = FontWeight.Bold

                                    )
                                }

                                // Полоска 3
                                HorizontalDivider(thickness = 2.dp, color = theme.iconTint)

                                // Кнопка Настройки
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .clickable { onClick(SETTINGS_CLICK) }
                                        .padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = theme.iconDrawerSettigs,
                                        contentDescription = "Настройки",

                                        tint = theme.iconTint
                                    )
                                    Text(
                                        text = "Настройки", // Отвечает tvNewPremium
                                        color = theme.textColor,
                                        fontSize = size.drawerBottomMenuText,
                                        modifier = Modifier
                                            .fillMaxWidth().padding(start = 4.dp)
                                        ,
                                        fontWeight = FontWeight.Bold

                                    )
                                }

                                // Полоска 4
                                HorizontalDivider(thickness = 2.dp, color = theme.iconTint)
                            }
                        }
                    }
                }
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(theme.backgroundStart),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )


                NavHost(
                    navController = localNavController,
                    startDestination = "personal_pager_hub",

                    ) {

                    // Точка А: Твой текущий пейджер (Блокнот + Будильник)
                    composable("personal_pager_hub") {
                        openPager(onOpenDrawer, pagerState)
                    }

                    // Точка Б: Новый экран Общих Дел
                    composable("common_screen") {
                        // Сюда мы подставим твой будущий экран общих дел
                        // CommonTasksScreen(innerPadding = innerPadding, viewModel = viewModel)
                    }
                }

            }
        }


    }

}


@Preview(showBackground = true)
@Composable
fun PreviewStartApp() {
    StartAppContent(theme = ThemeZabor())
}
