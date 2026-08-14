package presentation.screens

import CommonConst.FOREVER
import CommonConst.ONE_MONTH
import CommonConst.ONE_YEAR
import CommonConst.SIX_MONTH
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import domain.model.ProductCommon
import org.jetbrains.compose.resources.painterResource
import presentation.theme.Size
import presentation.theme.SizeNormal
import presentation.theme.Theme
import presentation.theme.ThemeNeon

@Composable
fun PremiumScreen(
    size: Size = SizeNormal(),
    listProduct : List<ProductCommon> = emptyList(),
    theme: Theme = ThemeNeon(),
    onBack: () -> Unit = {},
    onClickBuy: (String) -> Unit = {},
    innerPadding : PaddingValues = PaddingValues()
    ){
    var isSelected by remember { mutableStateOf(ONE_YEAR) }
    val premiumFeatures = remember {
    listOf(
        "Отключение рекламы",
        "Пользовательская сортировка",
        "Отображение дел в календаре",
        "Повторяющиеся напоминания",
        "Создание своих списков дел",
        "Поддержка разработчика",
        "Создание подзадач"
    )
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
            // 1. ВЕРХНЯЯ ПАНЕЛЬ (Всегда статична на экране)
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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = theme.iconDelTint
                    )
                }

                Text(
                    text = "PREMIUM функции",
                    color = theme.textColor,
                    fontSize = size.textMenu,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            // 2. СРЕДНИЙ COLUMN (Единственный скроллящийся элемент)
            // ИСПРАВЛЕНО: Добавлен .weight(1f), чтобы занять всё свободное место между верхом и низом
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 10.dp),
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 3.dp)
                        .border(3.dp, theme.borderCardMenuItem, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = theme.cardMenuItem)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        premiumFeatures.forEachIndexed { index, feature ->
                        PremiumItem(text = feature, theme = theme, size = size)
    
                    // Рисуем разделитель для всех элементов, кроме самого последнего
                        if (index < premiumFeatures.lastIndex) {
                        HorizontalDivider(
                            thickness = 1.dp,
                        color = theme.textColor.copy(alpha = 0.15f),
                        modifier = Modifier.padding(top = 6.dp)
                        )
                        }
                        }
                    }
                }


                if(listProduct.isNotEmpty()){
                    Text(
                        text = "Тарифы",
                        color = theme.textColor,
                        fontSize = size.textMenu,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        textAlign = TextAlign.Center
                    )
                    listProduct.forEach { productCommon ->

                        CardBuyPremium(
                            productCommon = productCommon,
                            theme = theme,
                            size = size,
                            isSelected = isSelected,
                            onClick = { isSelected = it }
                        )
                    }
                } else Text(
                    text = "Не удалось загрузить список тарифов",
                    color = theme.textColor,
                    fontSize = size.textMenu,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    textAlign = TextAlign.Center
                )


            }

            // 3. НИЖНЯЯ КНОПКА (Вынесена за пределы скролла, всегда на экране)
            Button(
    // Напрямую проверяем список на пустоту
    enabled = listProduct.isNotEmpty(), 
    onClick = { onClickBuy(isSelected) }, 
    modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp, horizontal = 16.dp)
        .border(
            width = 2.dp,
            // Красим рамку в зависимости от состояния списка
            color = if (listProduct.isNotEmpty()) theme.tintPremiumOn else theme.tintPremiumOff.copy(alpha = 0.3f),
            shape = RoundedCornerShape(12.dp)
        ),
    shape = RoundedCornerShape(12.dp),
    colors = ButtonDefaults.buttonColors(
        containerColor = theme.cardMenuItem,
        contentColor = theme.tintPremiumOn,
        // Задаем гаснущие неоновые цвета для disabled-состояния
        disabledContainerColor = theme.cardMenuItem.copy(alpha = 0.2f), 
        disabledContentColor = theme.textDesc 
    ),
    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
) {
    Text(
        text = if (listProduct.isNotEmpty()) "Оформить PREMIUM" else "Оплата временно недоступна",
        fontSize = size.textMenu,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.ExtraBold,
        style = LocalTextStyle.current.copy(
            // Неоновая тень гаснет, если список пуст
            shadow = if (listProduct.isNotEmpty()) {
                Shadow(
                    color = theme.tintPremiumOn.copy(alpha = 0.5f),
                    blurRadius = 8f
                )
            } else null
        )
    )
}
            // Button(
            //     onClick = { if(listProduct.isNotEmpty()) onClickBuy(isSelected) },
            //     modifier = Modifier
            //         .fillMaxWidth()
            //         .padding(vertical = 10.dp, horizontal = 16.dp)
            //         .border(
            //             width = 2.dp,
            //             color = theme.tintPremiumOn,
            //             shape = RoundedCornerShape(12.dp)
            //         ),
            //     shape = RoundedCornerShape(12.dp),
            //     colors = ButtonDefaults.buttonColors(
            //         containerColor = theme.cardMenuItem,
            //         contentColor = theme.tintPremiumOn
            //     ),
            //     elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            // ) {
            //     Text(
            //         text = if(listProduct.isNotEmpty())"Оформить PREMIUM"
            //         else "Оплата временно недоступна",
            //         fontSize = size.textMenu,
            //         textAlign = TextAlign.Center,
            //         fontWeight = FontWeight.ExtraBold,
            //         style = LocalTextStyle.current.copy(
            //             shadow = Shadow(
            //                 color = theme.tintPremiumOn.copy(alpha = 0.5f),
            //                 blurRadius = 8f
            //             )
            //         )
            //     )
            // }
        }
    }

    }


@Composable
fun PremiumItem(text: String,
                theme: Theme = ThemeNeon(),
                size: Size = SizeNormal()
){
        Row(
            modifier = Modifier
                .fillMaxWidth()
            ,
            verticalAlignment = Alignment.CenterVertically,


            ) {
            Icon(
                imageVector = Icons.Default.Star, // Нужен импорт androidx.compose.material.icons.Icons
                contentDescription = "Картинка",
                tint = theme.tintPremiumOn
            )




            Text(
                modifier = Modifier.padding(start = 5.dp, end = 5.dp).weight(1f),
                text = text,
                color = theme.textColor,
                lineHeight = size.lineHeightItem,
                fontSize = size.textItem
            )

            IconButton(
                onClick = { },
                modifier = Modifier.padding(end = 8.dp).size(24.dp)
            ) {

                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Default.Info,
                    contentDescription = "Chek",
                    tint = theme.chekBoxTint
                )

            }
        }

}

@Composable
fun CardBuyPremium(
    productCommon: ProductCommon,
    theme: Theme,
    size: Size,
    isSelected: String = "На один год",
    onClick: (String) -> Unit = {}
) {
    val currentSelected = isSelected == productCommon.productId

    // val desc = when(productCommon.productId){
    //     ONE_MONTH -> "Неделя бесплатно"
    //     SIX_MONTH -> "${productCommon.price/6} р/мес"
    //     ONE_YEAR -> "\uD83D\uDD25 АКЦИЯ:${productCommon.price/12} р/мес"
    //     FOREVER -> "Навсегда без подписок"
    //     else -> ""
    // }

    val remoteDesc = productCommon.desc
    val calculation = when(productCommon.productId) {
    SIX_MONTH -> "${productCommon.price / 6} ₽/мес"
    ONE_YEAR -> "${productCommon.price / 12} ₽/мес"
    else -> ""
    }

    val desc = "$remoteDesc $calculation"
    val price = "${productCommon.price} ₽"

    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp) // Небольшой отступ между карточками
                .border(
                    width = if (currentSelected) 3.dp else 1.dp, // Выбранная карточка толще
                    color = if (currentSelected) theme.tintPremiumOn else theme.borderCardMenuItem,
                    shape = RoundedCornerShape(10.dp)
                )
                .clip(RoundedCornerShape(10.dp))
                .clickable { onClick(productCommon.productId) },
            shape = RoundedCornerShape(10.dp),
            // Задаем цвет контейнера правильно через CardDefaults, чтобы не использовать .background
            colors = CardDefaults.cardColors(containerColor = theme.cardMenuItem)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Вместо IconButton используем обычный Icon.
                // IconButton внутри кликабельной карточки блокирует нажатия на себя!
//                Icon(
//                    imageVector = if (currentSelected) Icons.Default.Star else Icons.Outlined.Star, // Меняем на контурную, если не выбрана
//                    contentDescription = null,
//                    tint = if (currentSelected) theme.tintPremiumOn else theme.tintPremiumOff,
//                    modifier = Modifier.size(24.dp)
//                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = productCommon.name,
                        color = theme.textColor,
                        lineHeight = size.lineHeightItem,
                        fontSize = size.textItem,
                        fontWeight = if (currentSelected) FontWeight.Bold else FontWeight.Medium // Выделяем текст жирным
                    )

                    Text(
                        modifier = Modifier.padding(top = 2.dp),
                        text = desc,
                        color = if (currentSelected) theme.tintPremiumOn else theme.textDesc, // Текст описания года горит ярче
                        lineHeight = size.lineHeightDescAndAlarm,
                        fontSize = size.textDesc
                    )
                }

                Text(
                    text = price,
                    color = if (currentSelected) theme.textAlarm else theme.textColor,
                    fontSize = size.textMenu,
                    lineHeight = size.lineHeightDescAndAlarm,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Рисуем шильдик "АКЦИЯ" только если передан текст и карточка выбрана
        if (productCommon.productId == ONE_YEAR) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = theme.tintPremiumOn, // Берем ваш золотой/желтый цвет
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp) // Сдвиг к правому краю карточки
            ) {
                Text(
                    text = "ВЫГОДНО",
                    color = Color.Black, // Черный текст на желтом фоне читается идеально
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}



    @Preview
    @Composable
    fun PrevItemTwo() {
        PremiumScreen()
}


