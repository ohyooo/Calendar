@file:Suppress("UnstableApiUsage")


plugins {
    alias(libs.plugins.agp)
    alias(libs.plugins.jc)
    alias(libs.plugins.cc)
}

group = "com.ohyooo"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
}

android {
    ndkVersion = "29.0.14206865"
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
    namespace = "com.ohyooo.calendar"
    compileSdk = libs.versions.compile.sdk.get().toInt()
    defaultConfig {
        applicationId = "com.ohyooo.calendar"
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.target.sdk.get().toInt()
        versionCode = 10
        versionName = "2.10" + rootProject.extra["gitVersion"]
        proguardFile("consumer-rules.pro")
        signingConfig = signingConfigs.getByName("debug")
        ndk {
            abiFilters += setOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
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
        shaders = false
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
    sourceSets["main"].jniLibs.directories.add(layout.buildDirectory.dir("generated/jniLibs").get().asFile.path)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

kotlin {
    jvmToolchain(25)
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}

val knNativeLibName = "calendar_native"
val knTargetToAbi = mapOf(
    "androidNativeArm64" to "arm64-v8a",
    "androidNativeArm32" to "armeabi-v7a",
    "androidNativeX64" to "x86_64",
    "androidNativeX86" to "x86"
)

val copyKotlinNativeLibs by tasks.registering(Copy::class) {
    val nativeProject = project(":native")
    dependsOn(knTargetToAbi.keys.map { targetName ->
        ":native:linkReleaseShared" + targetName.replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase() else c.toString() }
    })
    knTargetToAbi.forEach { (targetName, abi) ->
        from(nativeProject.layout.buildDirectory.dir("bin/$targetName/releaseShared")) {
            include("lib${knNativeLibName}.so")
            into(abi)
        }
    }
    into(layout.buildDirectory.dir("generated/jniLibs"))
}

tasks.named("preBuild") {
    dependsOn(copyKotlinNativeLibs)
}
