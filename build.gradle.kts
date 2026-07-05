// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {

    id("com.android.application") version "8.13.2" apply false
    id("com.android.library") version "8.13.2" apply false
    id("com.android.lint") version "8.13.2" apply false


    id("org.jetbrains.kotlin.android") version "2.3.0" apply false
    id("org.jetbrains.kotlin.multiplatform") version "2.3.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0" apply false


    id("org.jetbrains.compose") version "1.11.1" apply false


    id("com.google.devtools.ksp") version "2.3.0" apply false
    //id("com.google.dagger.hilt.android") version "2.57.2" apply false
}

