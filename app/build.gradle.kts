plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt") // Apply the Kotlin Annotation Processing plugin

}

android {
    namespace = "com.example.grocerez"
    compileSdk = 34

    viewBinding{
        enable = true
    }

    dataBinding{
        enable =true
    }

    defaultConfig {
        applicationId = "com.example.grocerez"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        //Jocelyn enabling MultiDex for pieChart
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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


    // socket connection dependency
    implementation ("io.socket:socket.io-client:2.0.0")
    implementation("androidx.activity:activity:1.8.0")

    // add room dependencies
    val room_version = ("2.6.1")
    implementation ("androidx.room:room-runtime:$room_version")
    annotationProcessor ("androidx.room:room-compiler:$room_version")
    // To use Kotlin annotation processing tool (kapt)
    kapt ("androidx.room:room-compiler:$room_version")
    implementation ("androidx.room:room-ktx:$room_version")




    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("com.google.zxing:core:3.4.1")
    implementation("androidx.camera:camera-camera2:1.3.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //adding implementation for pieChart - Jocelyn
    implementation("com.github.AnyChart:AnyChart-Android:1.1.5")
    implementation("androidx.multidex:multidex:2.0.1")
}