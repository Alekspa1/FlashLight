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
      //  iosX64(),
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
            implementation("org.jetbrains.compose.runtime:runtime:1.11.1")
            implementation("org.jetbrains.compose.foundation:foundation:1.11.1")
            implementation("org.jetbrains.compose.material3:material3:1.9.0")          // Material 3 дизайн
            implementation("org.jetbrains.compose.components:components-resources:1.11.1") // Для строк, картинок и шрифтов
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.9.6")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.9.6")
            // 1. Аналог libs.koin.core (Базовый движок Koin)
            implementation("io.insert-koin:koin-core:4.2.2")

            // 2. Аналог libs.koin.compose (Поддержка Koin внутри UI-функций)
            implementation("io.insert-koin:koin-compose:4.2.2")

            // 3. Аналог libs.koin.compose.viewmodel (Магия функции koinViewModel() для KMP)
            implementation("io.insert-koin:koin-compose-viewmodel:4.2.2")

            implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.10.0") // если нужен превью

            // САМАЯ ВАЖНАЯ СТРОЧКА:
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
        }

        commonTest.dependencies {
            // kotlin-test подключится автоматически, ручные строки не нужны
        }

        androidMain.dependencies {
            implementation("androidx.activity:activity-compose:1.13.0") // Для setContent и ComponentActivity
            implementation("androidx.appcompat:appcompat:1.7.1")
            implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.10.0")
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
    sourceSets.commonTest.dependencies {
        implementation(kotlin("test"))
    }
}

android {
    namespace = "com.dragon.shared"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }
}

dependencies {
    debugImplementation("androidx.compose.ui:ui-tooling:1.10.0")
}
