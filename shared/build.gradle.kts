import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kmm)
    alias(libs.plugins.jc)
    alias(libs.plugins.alp)
    alias(libs.plugins.ks)
    alias(libs.plugins.cc)
}

group = "com.ohyooo"
version = "1.0.0"

kotlin {
    androidLibrary {
        namespace = "com.ohyooo.shared"
        compileSdk = libs.versions.compile.sdk.get().toInt()
        minSdk = libs.versions.min.sdk.get().toInt()

        withJava() // enable java compilation support
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            }
        }

//        compilerOptions.configure {
//            jvmTarget.set(
//                org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
//            )
//        }
        kotlin {
            jvmToolchain(21)
        }
    }
//    android {
//        namespace = "com.ohyooo.shared"
//
////        sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
////        sourceSets["main"].res.srcDirs("src/androidMain/res")
////        sourceSets["main"].resources.srcDirs("src/commonMain/resources")
//
//        compileSdk = libs.versions.compile.sdk.get().toInt()
////        defaultConfig {
////            minSdk = libs.versions.min.sdk.get().toInt()
////        }
////        compileOptions {
////            sourceCompatibility = JavaVersion.VERSION_21
////            targetCompatibility = JavaVersion.VERSION_21
////        }
//        kotlin {
//            jvmToolchain(21)
//        }
//    }
    jvm("desktop")
    wasmJs {
        outputModuleName = "shared"
        browser {
            commonWebpackConfig {
                outputFileName = "shared.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.projectDir.path)
                    }
                }
            }
        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.components.resources)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(libs.kotlinx.datetime)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.androidx.compose.activity)
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



tasks.register("testClasses")
