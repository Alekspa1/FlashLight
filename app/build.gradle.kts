plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
}

android {
    namespace = "com.exampl3.flashlight"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.exampl3.flashlight"
        minSdk = 24
        targetSdk = 35
        versionCode = 22
        versionName = "8.5"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        kapt {
            arguments {arg("room.schemaLocation", "$projectDir/schemas")}
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("com.applandeo:material-calendar-view:1.9.2")


    implementation("com.yandex.android:mobileads:7.12.2")
    implementation("com.yandex.ads.mediation:mobileads-mytarget:5.22.1.8")

    implementation("ru.rustore.sdk:billingclient:7.0.0")
    implementation ("com.google.dagger:hilt-android:2.55")
    kapt ("com.google.dagger:hilt-compiler:2.55")

    implementation ("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.fragment:fragment-ktx:1.8.6")


    

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

