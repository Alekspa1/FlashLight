import org.gradle.kotlin.dsl.implementation

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp") version "2.3.0"
    id("androidx.room") version "2.8.4"
}

room {
    // Указывает Room, куда сохранять JSON-схемы базы данных
    schemaDirectory("$projectDir/schemas")
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
            implementation("io.insert-koin:koin-core:4.2.2")
            implementation("io.insert-koin:koin-compose:4.2.2")
            implementation("io.insert-koin:koin-compose-viewmodel:4.2.2")
            implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.10.0")
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")

            implementation("androidx.sqlite:sqlite-bundled:2.7.0")
            implementation("androidx.room:room-runtime:2.8.4")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.8.0")


            implementation("io.github.vinceglb:filekit-compose:0.8.7")
            implementation("com.kizitonwose.calendar:compose-multiplatform:2.10.1")
            implementation("org.jetbrains.androidx.navigationevent:navigationevent-compose:1.1.0")
            // Зависимости Coil (3.5.0)
            implementation("io.coil-kt.coil3:coil-compose:3.3.0")
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.2")
            implementation("io.github.aclassen:compose-reorderable:1.5.0")
        }

        commonTest.dependencies {
            // kotlin-test подключится автоматически, ручные строки не нужны
        }

        androidMain.dependencies {
            implementation("androidx.activity:activity-compose:1.13.0") // Для setContent и ComponentActivity
            implementation("androidx.appcompat:appcompat:1.7.1")
            implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.10.0")
            implementation("com.yandex.android:mobileads:8.2.0")
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.11.0")
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
    // Отдаем компилятор Room в KSP для метаданных общего кода
    add("kspCommonMainMetadata", "androidx.room:room-compiler:2.8.4")

    // Отдаем компилятор Room в KSP отдельно для каждой из твоих платформ
    add("kspAndroid", "androidx.room:room-compiler:2.8.4")
    add("kspDesktop", "androidx.room:room-compiler:2.8.4")

     add("kspIosSimulatorArm64", "androidx.room:room-compiler:2.8.4")
     add("kspIosArm64", "androidx.room:room-compiler:2.8.4")
}
