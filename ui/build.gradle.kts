@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kmm)
    alias(libs.plugins.alp)
    alias(libs.plugins.jc)
    alias(libs.plugins.cc)
}

group = "com.ohyooo"
version = "1.0.0"

kotlin {
    androidLibrary {
        namespace = "com.ohyooo.calendar.ui"
        compileSdk = libs.versions.compile.sdk.get().toInt()
        minSdk = libs.versions.min.sdk.get().toInt()

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            }
        }
        androidResources {
            enable = true
        }
    }

    jvm("desktop")

    val uiXCFramework = XCFramework("ui")

    macosArm64 {
        binaries.framework {
            baseName = "ui"
            isStatic = true
            binaryOption("bundleId", "com.ohyooo.calendar.ui")
            export(project(":shared"))
            uiXCFramework.add(this)
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = "ui"
            isStatic = true
            binaryOption("bundleId", "com.ohyooo.calendar.ui")
            export(project(":shared"))
            uiXCFramework.add(this)
        }
    }

    jvmToolchain(21)

    wasmJs {
        outputModuleName = "ui"
        browser {
            commonWebpackConfig {
                outputFileName = "ui.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static(project.projectDir.path)
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":shared"))
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.components.resources)
                implementation(compose.material3)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.androidx.activity.compose)
                api(libs.androidx.core.ktx)
                api(libs.androidx.startup.runtime)
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
            }
        }
    }
}
