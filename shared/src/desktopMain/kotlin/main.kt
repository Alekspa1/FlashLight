package com.dragon.shared

import StartApp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Focus" // Имя окна вашей программы
    ) {
        StartApp() // Вызываем нашу общую функцию из commonMain
    }
}