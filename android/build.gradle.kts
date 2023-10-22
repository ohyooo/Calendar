// @file:Suppress("UnstableApiUsage")

plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

group = "com.ohyooo"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(project(":shared"))
    implementation(Libs.AndroidX.compose)
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("signkey.jks")
            storePassword = "123456"
            keyPassword = "123456"
            keyAlias = "demo"

            enableV3Signing = true
            enableV4Signing = true
        }
    }
    namespace = "com.ohyooo.android"
    compileSdk = Ext.compileSdk
    defaultConfig {
        applicationId = "com.ohyooo.calendar"
        minSdk = Ext.minSdk
        targetSdk = Ext.targetSdk
        versionCode = Ext.versionCode
        versionName = Ext.versionName + hashTag
        signingConfig = signingConfigs.getByName("debug")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "consumer-rules.pro")
        }
    }
    buildFeatures {
        compose = true
        // Disable unused AGP features
        buildConfig = false
        aidl = false
        renderScript = false
        shaders = false
    }
    compose {
        kotlinCompilerPlugin.set(Libs.Compose.compiler)
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Libs.Compose.compilerVersion
    }
}

val hashTag: String
    get() = ProcessBuilder(listOf("git", "rev-parse", "--short", "HEAD"))
        .directory(rootDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()
        .apply { waitFor(5, TimeUnit.SECONDS) }
        .run {
            val error = errorStream.bufferedReader().readText().trim()
            if (error.isNotEmpty()) {
                ""
            } else {
                "-" + inputStream.bufferedReader().readText().trim()
            }
        }
