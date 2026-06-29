package presentation

import MainViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner


@Composable

fun Notebook(viewModel: MainViewModel){



    val lifecycleOwner = LocalLifecycleOwner.current



    DisposableEffect(lifecycleOwner) {

        // 2. Создаем наблюдателя, который ловит события операционной системы

        val observer = LifecycleEventObserver { _, event ->

            // Если приложение сворачивается (уходит в фон)

            if (event == Lifecycle.Event.ON_STOP) {

                viewModel.saveText() // ЖЕЛЕЗНО сохраняем данные на диск!

            }

        }



        // Подписываем нашего наблюдателя

        lifecycleOwner.lifecycle.addObserver(observer)



        onDispose {

            // 3. Не забываем сохранить данные, если пользователь просто переключил вкладку

            viewModel.saveText()



            // И обязательно отписываемся от жизненного цикла, чтобы не было утечек памяти

            lifecycleOwner.lifecycle.removeObserver(observer)

        }

    }



    OutlinedTextField(

        modifier = Modifier.padding(16.dp).fillMaxSize(),

        value = viewModel.stateTextNotebook,

        onValueChange = { newText -> viewModel.stateTextNotebook = newText }, // Обновляем стейт при вводе

        shape = RoundedCornerShape(16.dp),



        )





}
