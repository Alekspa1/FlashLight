package com.dragon.shared

import StartApp
import androidx.compose.material.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import data.initKoin
import flashlight.shared.generated.resources.Res
import flashlight.shared.generated.resources.icon
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    initKoin()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Focus",
        icon = painterResource(Res.drawable.icon)// Имя окна вашей программы
    ) {
        StartApp()

    }
}