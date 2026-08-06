package presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import flashlight.shared.generated.resources.Res
import flashlight.shared.generated.resources.ic_alarm_faq
import flashlight.shared.generated.resources.ic_category_faq
import flashlight.shared.generated.resources.ic_sort_faq
import org.jetbrains.compose.resources.painterResource
import presentation.theme.Size
import presentation.theme.SizeNormal
import presentation.theme.Theme
import presentation.theme.ThemeNeon
import androidx.compose.foundation.Image
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.layout.ContentScale

@Composable
fun Faq(
    size: Size = SizeNormal(),
    theme: Theme = ThemeNeon(),
    onBack: () -> Unit = {},
    innerPadding : PaddingValues = PaddingValues())
{


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
                        tint = theme.iconTint
                    )
                }


                Text(
                    text = "Инструкция", // tv_settings
                    color = theme.textColor,
                    fontSize = size.textMenu,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_alarm_faq),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillWidth
                )
                HorizontalDivider(thickness = 2.dp, color = Color.White)
                Image(
                    painter = painterResource(Res.drawable.ic_sort_faq),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillWidth
                )
                HorizontalDivider(thickness = 2.dp, color = Color.White)
                Image(
                    painter = painterResource(Res.drawable.ic_category_faq),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillWidth
                )

            }


        }
        }



}


@Preview(showBackground = true)
@Composable
fun PFaq(){
    Faq()
}
