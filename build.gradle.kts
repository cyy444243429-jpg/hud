// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

// 移除 buildscript 块中的 ApkUploadTools 依赖
buildscript {
    dependencies {
        // 移除这行：classpath(libs.apkuploadtools)
    }
}
