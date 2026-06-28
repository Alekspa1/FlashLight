plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    // 1. Таргеты для Android и Desktop
    androidTarget()
    jvm("desktop")

    // 2. Таргеты для iOS (объединенные в красивый цикл)
    val xcfName = "ComposeApp"
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = xcfName
            isStatic = true // Важно для стабильной интеграции с iOS
        }
    }

    // 3. Конфигурация исходного кода и библиотек
    sourceSets {
        commonMain.dependencies {
            // Подключаем базовые компоненты Compose Multiplatform
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)          // Material 3 дизайн
            implementation(compose.components.resources) // Для строк, картинок и шрифтов
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
        }

        commonTest.dependencies {
            // kotlin-test подключится автоматически, ручные строки не нужны
        }

        androidMain.dependencies {
            implementation("androidx.activity:activity-compose:1.13.0") // Для setContent и ComponentActivity
            implementation("androidx.appcompat:appcompat:1.7.1")
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        iosMain.dependencies {
            // Сюда при необходимости добавим специфичные для iOS библиотеки
        }
    }
}

android {
    namespace = "com.dragon.shared"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }
}
