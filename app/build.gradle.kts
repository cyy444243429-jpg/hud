// app/build.gradle.kts
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 35
    buildToolsVersion = "35.0.0"

    defaultConfig {
        applicationId = "com.fkdeepal.tools.ext"
        minSdk = 24
        targetSdk = 35
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
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    // 使用版本目录中的依赖
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    
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
            force(libs.androidx.appcompat.get())
            force(libs.material.get())
            force(libs.androidx.core.ktx.get())
        }
    }
}
