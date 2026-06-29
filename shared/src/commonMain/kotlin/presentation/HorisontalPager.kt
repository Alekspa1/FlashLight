package presentation

import MainViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

import org.koin.compose.viewmodel.koinViewModel



@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

@Composable
fun MainWeatherPager(paddingValues: PaddingValues, onClick: () -> Unit = {}) {

    val viewModel: MainViewModel = koinViewModel()
    val titles = listOf("Блокнот","Список дел","Календарь")
    val pagerState = rememberPagerState(pageCount = { titles.size })
    val scope = rememberCoroutineScope()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                // Достаем из объекта строго верхний системный отступ:
                top = paddingValues.calculateTopPadding(),
                // Достаем из объекта строго нижний системный отступ:
                bottom = paddingValues.calculateBottomPadding(),
                // Ваши фиксированные аккуратные отступы по бокам:
                start = 4.dp,
                end = 4.dp
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp) // ЖЕСТКО ЗАДАЕМ ВЫСОТУ 48.dp вместо IntrinsicSize.Min
                .background(Color.Black),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(

                onClick = { onClick() },

                modifier = Modifier.fillMaxHeight() // Иконка растягивается на всю высоту вкладок

            ) {

                Icon(
                    imageVector = Icons.Default.Menu, // Нужен импорт androidx.compose.material.icons.Icons
                    contentDescription = "Меню",
                    tint = Color.White
                )

            }



            PrimaryTabRow(

                selectedTabIndex = pagerState.currentPage,

                modifier = Modifier.weight(1f),

                containerColor = Color.Transparent, // Прозрачный фон

                divider = {}, // Убираем стандартную линию снизу

                // Настройка полоски (индикатора)

                indicator = {

                    val modifier = Modifier.tabIndicatorOffset(pagerState.currentPage)
                    TabRowDefaults.PrimaryIndicator(

                        modifier = modifier,

                        width = 60.dp,        // Ширина полоски

                        height = 3.dp,        // Толщина

                        color = Color.White,  // БЕЛЫЙ ЦВЕТ
                        shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)

                    )

                }

            ) {

                titles.forEachIndexed { index, title ->

                    val selected = pagerState.currentPage == index



                    Tab(

                        selected = selected,

                        onClick = {
                            if (pagerState.currentPage != index) {
                                scope.launch { pagerState.animateScrollToPage(index) }
                            }

                        },
                        text = {

                            Text(

                                color = if (selected) Color.White else Color.Gray,

                                text = title,

                                style = MaterialTheme.typography.titleSmall,

                                // Дополнительно можно менять жирность

                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal

                            )

                        }

                    )

                }

            }

        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()

        ) { pageIndex ->
            when (pageIndex) {
                0 -> {Notebook(viewModel)}
                1 -> {Text("2")}
                2 -> {Text("3")}

            }

        }

    }

}

@Preview
@Composable
fun Preview(){
    MainWeatherPager(PaddingValues())
}
