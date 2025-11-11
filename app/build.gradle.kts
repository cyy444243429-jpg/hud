import java.text.SimpleDateFormat

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.github.aitiwang.apkupload")
}

android {
    namespace = "com.fkdeepal.tools.ext"
    compileSdk {
        version = release(35)
    }

    defaultConfig {
        applicationId = "com.fkdeepal.tools.ext"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("long", "BUILD_TIME_MILLIS", "${System.currentTimeMillis()}L")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    signingConfigs {
        create("release") {
            keyAlias = "FkDeepal"
            keyPassword = "fkdeepal"
            storeFile = File(project.rootDir.absolutePath + "/key.jks")
            storePassword = "fkdeepal"
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-sdk.pro", "proguard-app.pro", "proguard-custom.pro")
            signingConfig = signingConfigs.getByName("release")

        }
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")

        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        //   resValues = true
    }
    applicationVariants.all {

        val buildTypeName = this.buildType.name
        this.outputs.forEach {
            if (it is com.android.build.gradle.internal.api.BaseVariantOutputImpl) {
                val versionCode = this.versionCode
                val buildTime = (SimpleDateFormat("yyyyMMddHHmm").format(System.currentTimeMillis())).substring(4)
                val fileName = "deepalExtTools_v${versionName}(${versionCode})_${buildTime}_${buildTypeName}.${it.outputFile.extension}"
                it.outputFileName = fileName
            }

        }
    }
}
uploadApkConfig {

    enablePgyer = true
    pgyApiUrl = "https://www.xcxwo.com/apiv2/app/upload"
    pgyApiKey = "2c209989dba7b3f71afea5ae40212cef"
    buildUpdateDescription = "手机"
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.liveEventBus)
    implementation(libs.gson)
    implementation(libs.timber)
    implementation(libs.preference)
    implementation(libs.preference.ktx)
    testImplementation(libs.junit)
    testImplementation(fileTree(mapOf("dir" to "testLibs", "include" to listOf("*.jar"))))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}