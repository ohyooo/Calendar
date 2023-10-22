import org.jetbrains.kotlin.gradle.tasks.DummyFrameworkTask

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
}

group = "com.ohyooo"
version = "1.0.0"

kotlin {
    androidTarget()
    jvm("desktop") {
        jvmToolchain(17)
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.material3)
                api(compose.materialIconsExtended)
            }
        }
        // val sharedTest by getting {
        //     dependencies {
        //         implementation(kotlin("test"))
        //     }
        // }
        val androidMain by getting {
            dependencies {
                api(Libs.AndroidX.coreKtx)
                api(Libs.AndroidX.startup)
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
            }
        }
        val desktopTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    namespace = "com.ohyooo.calendar.shared"
    compileSdk = Ext.compileSdk
    defaultConfig {
        minSdk = Ext.minSdk
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    compose {
        kotlinCompilerPlugin.set(Libs.Compose.compiler)
    }
}


// TODO move to gradle plugin
tasks.withType<DummyFrameworkTask>().configureEach {
    @Suppress("ObjectLiteralToLambda")
    doLast(object : Action<Task> {
        override fun execute(task: Task) {
            task as DummyFrameworkTask

            val frameworkDir = File(task.destinationDir, task.frameworkName.get() + ".framework")

            listOf(
                "compose-resources-gallery:shared.bundle"
            ).forEach { bundleName ->
                val bundleDir = File(frameworkDir, bundleName)
                bundleDir.mkdir()
                File(bundleDir, "dummyFile").writeText("dummy")
            }
        }
    })
}