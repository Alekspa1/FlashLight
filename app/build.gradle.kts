import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id ("dagger.hilt.android.plugin")
}

android {
    namespace = "com.exampl3.flashlight"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.exampl3.flashlight"
        minSdk = 26
        targetSdk = 36
        versionCode = 32
        versionName = "9.5"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("ru.rustore.sdk:appupdate:7.0.0")
    implementation("com.applandeo:material-calendar-view:1.9.2")
    implementation ("com.github.bumptech.glide:glide:5.0.5")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.6")
    implementation("androidx.fragment:fragment:1.8.9")
    implementation("androidx.cardview:cardview:1.0.0")

    ksp ("com.github.bumptech.glide:compiler:5.0.5")

    implementation("com.yandex.android:mobileads:7.18.1")


    implementation("ru.rustore.sdk:billingclient:7.0.0")
    implementation ("com.google.dagger:hilt-android:2.57.2")
    implementation("androidx.activity:activity-ktx:1.12.2")
    ksp ("com.google.dagger:hilt-compiler:2.57.2")

    implementation ("androidx.room:room-runtime:2.8.4")
    ksp ("androidx.room:room-compiler:2.8.4")
    implementation ("androidx.room:room-ktx:2.8.4")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")
    implementation("androidx.fragment:fragment-ktx:1.8.9")


    

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}

