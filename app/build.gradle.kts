// app/build.gradle.kts
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    defaultConfig {
        applicationId = "com.fkdeepal.tools.ext"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        
        vectorDrawables.useSupportLibrary = true
        namespace = "com.fkdeepal.tools.ext"
    }
    
    buildTypes {
        getByName("release") {
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
        buildConfig = true
    }
}

dependencies {
    // 强制使用最新版本的 AndroidX 库
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // 其他依赖使用版本目录
    implementation(libs.androidsvg)
    implementation(libs.timber)
    implementation(libs.gson)
    implementation(libs.preference)
    implementation(libs.preference.ktx)
    implementation(libs.liveEventBus)
    
    // 强制解决版本冲突
    configurations.all {
        resolutionStrategy {
            force("androidx.appcompat:appcompat:1.7.0")
            force("com.google.android.material:material:1.11.0")
            force("androidx.core:core-ktx:1.12.0")
        }
    }
}
