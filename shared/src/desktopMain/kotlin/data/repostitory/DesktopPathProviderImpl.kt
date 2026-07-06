package com.dragon.shared.data.repostitory

import domain.repostirory.PathProviderRepostitory
import java.io.File

class DesktopPathProviderImpl : PathProviderRepostitory {
    override fun getInternalAppPath(): String {
        // 1. Автоматически находим домашнюю папку пользователя (например, C:\Users\Name)
        val userHome = System.getProperty("user.home") ?: "."

        // 2. Задаем имя для скрытой папки нашего приложения
        val appDir = File(userHome, ".flashlight_app_data")

        // 3. Если папки еще нет на компьютере — физически создаем её
        if (!appDir.exists()) {
            appDir.mkdirs()
        }

        // 4. Возвращаем чистый абсолютный путь к ней
        return appDir.absolutePath
    }
}