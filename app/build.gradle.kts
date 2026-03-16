plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.amrica.camera"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.amrica.camera"
        minSdk = 31 // مهم جداً عشان الـ RenderEffect الزجاجي يشتغل
        targetSdk = 34
        versionCode = 1
        versionName = "1.0 Pro"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

dependencies {
    val cameraxVersion = "1.3.1"
    
    // مكتبات الكاميرا للتحكم في العدسة
    implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")

    // مكتبات الواجهة الزجاجية (Compose)
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.core:core-ktx:1.12.0")
}
