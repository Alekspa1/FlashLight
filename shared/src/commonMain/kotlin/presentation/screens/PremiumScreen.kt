package presentation.screens

import CommonConst.CHANGE
import CommonConst.CHANGE_ITEM
import CommonConst.IMAGE
import CommonConst.INSERT_DIALOG_CATEGORY
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import presentation.theme.Size
import presentation.theme.SizeNormal
import presentation.theme.Theme
import presentation.theme.ThemeNeon

@Composable
fun PremiumScreen(
    size: Size = SizeNormal(),
    theme: Theme = ThemeNeon(),
    onBack: () -> Unit = {},
    innerPadding : PaddingValues = PaddingValues()
    ){
    var isSelected by remember { mutableStateOf("На один год") }
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)

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
                text = "PREMIUM функции", // tv_settings
                color = theme.textColor,
                fontSize = size.textMenu,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 3.dp)
                .border(3.dp, theme.borderCardMenuItem, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .clickable {  },
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor =  theme.cardMenuItem,)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                PremiumItem(text = "Отключение рекламы",theme = theme, size = size)
                PremiumItem("Пользовательская сортировка",theme,size)
                PremiumItem("Отображение дел в календаре",theme,size)
                PremiumItem("Повторяющиеся напоминания",theme,size)
                PremiumItem("Создание своих списков дел",theme,size)
                PremiumItem("Поддержка разработчика",theme,size)

            }
        }

        Text(
            text = "Оформить PREMIUM",
            color = theme.textColor,
            fontSize = size.textMenu,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
            textAlign = TextAlign.Center
        )

        CardBuyPremium(
            text = "На один месяц",
            desc = "7 дней бесплатно",
            price = "99 р",
            theme = theme,
            size = size,
            isSelected = isSelected,
            onClick = { isSelected = it }
        )

        CardBuyPremium(
            text = "На шесть месяцев",
            desc = "81р/мес",
            price = "490 р",
            theme = theme,
            size = size,
            isSelected = isSelected,
            onClick = { isSelected = it }
        )

        CardBuyPremium(
            text = "На один год",
            desc = "🔥 АКЦИЯ: Всего 57 ₽/мес",
            price = "690 ₽",
            badgeText = "ВЫГОДНО", // Самый сильный триггер остается здесь
            theme = theme,
            size = size,
            isSelected = isSelected,
            onClick = { isSelected = it }
        )

        CardBuyPremium(
            text = "На всю жизнь",
            desc = "Навсегда без подписок", // Заменил "Хит" на ценность (навсегда)
            price = "1990 р", // Выше мы обсуждали 1999р, но если оставляете эту — описание ниже сбалансирует её
            theme = theme,
            size = size,
            isSelected = isSelected,
            onClick = { isSelected = it }
        )

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
                .padding(start = 6.dp, top = 8.dp, bottom = 8.dp, end = 6.dp),
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

                    imageVector = Icons.Default.Warning,
                    contentDescription = "Chek",
                    tint = theme.chekBoxTint
                )

            }
        }

}

@Composable
fun CardBuyPremium(
    text: String,
    desc: String,
    theme: Theme,
    size: Size,
    price: String = "199",
    isSelected: String = "На один год",
    badgeText: String? = null, // Добавляем параметр для плашки (например, "-50%" или "АКЦИЯ")
    onClick: (String) -> Unit = {}
) {
    val currentSelected = isSelected == text

    // Оборачиваем в Box, чтобы шильдик мог красиво накладываться поверх верхней грани
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
                .clickable { onClick(text) },
            shape = RoundedCornerShape(10.dp),
            // Задаем цвет контейнера правильно через CardDefaults, чтобы не использовать .background
            colors = CardDefaults.cardColors(containerColor = theme.cardMenuItem)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Вместо IconButton используем обычный Icon.
                // IconButton внутри кликабельной карточки блокирует нажатия на себя!
                Icon(
                    imageVector = if (currentSelected) Icons.Default.Star else Icons.Outlined.Star, // Меняем на контурную, если не выбрана
                    contentDescription = null,
                    tint = if (currentSelected) theme.tintPremiumOn else theme.tintPremiumOff,
                    modifier = Modifier.size(24.dp)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp, end = 12.dp),
                ) {
                    Text(
                        text = text,
                        color = theme.textColor,
                        lineHeight = size.lineHeightItem,
                        fontSize = size.textMenu,
                        fontWeight = if (currentSelected) FontWeight.Bold else FontWeight.Medium // Выделяем текст жирным
                    )

                    Text(
                        modifier = Modifier.padding(top = 2.dp),
                        text = desc,
                        color = if (currentSelected) theme.tintPremiumOn else theme.textDesc, // Текст описания года горит ярче
                        lineHeight = size.lineHeightDescAndAlarm,
                        fontSize = size.textItem
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
        if (badgeText != null) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = theme.tintPremiumOn, // Берем ваш золотой/желтый цвет
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp) // Сдвиг к правому краю карточки
            ) {
                Text(
                    text = badgeText,
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

