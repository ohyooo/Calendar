@file:Suppress("UnstableApiUsage")

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
        versionName = Ext.versionName + hashTag
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
        create("benchmark") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false

            // https://developer.android.com/topic/performance/baselineprofiles/create-baselineprofile
            // Benchmark builds should not be obfuscated.
            postprocessing {
                isRemoveUnusedCode = true
                isRemoveUnusedResources = true
                isObfuscate = false
                isOptimizeCode = true
            }
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
        isCoreLibraryDesugaringEnabled = true
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
        kotlinCompilerExtensionVersion = Libs.Compose.compilerVersion
    }

}

dependencies {
    coreLibraryDesugaring(Libs.AndroidX.desugar)
    Libs.implementList.forEach(::implementation)
    Libs.debugImplementList.forEach(::debugImplementation)
}

val hashTag
    get() = try {
        val r = providers.exec {
            workingDir(rootDir)
            commandLine("git", "rev-parse", "--short", "HEAD")
        }.standardOutput?.asText?.get()?.trim()
        if (!r.isNullOrBlank()) "-$r" else ""
    } catch (e: Exception) {
        ""
    }
