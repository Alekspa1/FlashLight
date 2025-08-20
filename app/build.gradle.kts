plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
}

android {
    namespace = "com.exampl3.flashlight"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.exampl3.flashlight"
        minSdk = 26
        targetSdk = 35
        versionCode = 24
        versionName = "8.7"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    kapt {
        arguments {arg("room.schemaLocation", "$projectDir/schemas")}
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
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("com.applandeo:material-calendar-view:1.9.2")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.3")
    implementation("androidx.fragment:fragment:1.8.8")
    implementation("androidx.cardview:cardview:1.0.0")

    kapt ("com.github.bumptech.glide:compiler:4.16.0")

    implementation("com.yandex.android:mobileads:7.15.0")
    implementation("com.yandex.ads.mediation:mobileads-mytarget:5.27.1.5")

    implementation("ru.rustore.sdk:billingclient:7.0.0")
    implementation ("com.google.dagger:hilt-android:2.57")
    implementation("androidx.activity:activity-ktx:1.10.1")
    kapt ("com.google.dagger:hilt-compiler:2.57")

    implementation ("androidx.room:room-runtime:2.7.2")
    kapt ("androidx.room:room-compiler:2.7.2")
    implementation ("androidx.room:room-ktx:2.7.2")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.2")
    implementation("androidx.fragment:fragment-ktx:1.8.8")


    

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}

