@file:Suppress("UnstableApiUsage")

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
    implementation(libs.androidx.compose.activity)
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
    namespace = libs.versions.application.id.get()
    compileSdk = libs.versions.compile.sdk.get().toInt()
    defaultConfig {
        applicationId = libs.versions.application.id.get()
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.target.sdk.get().toInt()
        versionCode = libs.versions.version.code.get().toInt()
        versionName = libs.versions.target.sdk.get() + hashTag
        proguardFile("consumer-rules.pro")
        signingConfig = signingConfigs.getByName("debug")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
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
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
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
