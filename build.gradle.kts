// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Плагины сборки Android (версию 8.11.2 оставляем как на вашем скриншоте)
    id("com.android.application") version "8.11.2" apply false
    id("com.android.library") version "8.11.2" apply false
    id("com.android.lint") version "8.11.2" apply false

    // Все плагины Kotlin ставим на одну версию (например, 2.0.20)
    id("org.jetbrains.kotlin.android") version "2.3.0" apply false
    id("org.jetbrains.kotlin.multiplatform") version "2.3.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0" apply false

    // Плагин Compose Multiplatform (актуальная стабильная под Kotlin 2.0.20)
    id("org.jetbrains.compose") version "1.11.1" apply false

    // Ваши сопутствующие плагины (версии из вашего проекта)
    id("com.google.devtools.ksp") version "2.3.0" apply false // версия KSP привязана к версии Kotlin
    id("com.google.dagger.hilt.android") version "2.57.2" apply false
}

