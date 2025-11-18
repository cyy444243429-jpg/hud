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
        
        // 添加这行解决命名空间问题
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
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")  // 更新到最新版本
    implementation("com.google.android.material:material:1.11.0")  // 更新到最新版本
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // 添加 AndroidSVG 依赖
    implementation("com.caverock:androidsvg:1.4")
    
    // Timber 日志
    implementation("com.jakewharton.timber:timber:5.0.1")
}
