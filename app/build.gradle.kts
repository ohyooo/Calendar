plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.ohyooo.calendar"
    signingConfigs {
        getByName("debug") {
            storeFile = file("..\\signkey.jks")
            storePassword = "123456"
            keyPassword = "123456"
            keyAlias = "demo"

            enableV3Signing = true
            enableV4Signing = true
        }
    }
    compileSdk = Ext.compileSdk
    buildToolsVersion = Ext.buildToolsVersion
    defaultConfig {
        applicationId = Ext.applicationId
        minSdk = Ext.minSdk
        targetSdk = Ext.targetSdk
        versionCode = Ext.versionCode
        versionName = Ext.versionName
        signingConfig = signingConfigs.getByName("debug")
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "consumer-rules.pro")
        }
    }
    splits {
        abi {
            isEnable = true
            reset()
            include("x86", "armeabi-v7a", "arm64-v8a", "x86_64")
            isUniversalApk = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true

        // Disable unused AGP features
        buildConfig = false
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Libs.Compose.composeVersion
    }

}

dependencies {
    Libs.appImplements.forEach(::implementation)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xbackend-threads=12",
            "-opt-in=kotlin.RequiresOptIn"
        )
    }
}
