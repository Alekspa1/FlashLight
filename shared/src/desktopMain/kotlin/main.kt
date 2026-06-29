package com.dragon.shared

import StartApp
import androidx.compose.material.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import data.initKoin

fun main() = application {
    initKoin()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Focus" // Имя окна вашей программы
    ) {
        StartApp()

    }
}