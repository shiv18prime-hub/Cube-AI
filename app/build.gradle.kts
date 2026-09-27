plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    implementation("io.github.toger2021:cube-kociemba:1.0.2")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.camera:camera-camera2:1.4.2")
    implementation("androidx.camera:camera-lifecycle:1.4.2")
    implementation("androidx.camera:camera-view:1.4.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
}

android {
    namespace = "com.cubeai.app"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.cubeai.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
