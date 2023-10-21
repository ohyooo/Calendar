import org.jetbrains.kotlin.gradle.tasks.DummyFrameworkTask

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("dev.icerock.mobile.multiplatform-resources")
}

group = "com.ohyooo"
version = "1.0.0"

kotlin {
    androidTarget()
    jvm("desktop") {
        jvmToolchain(17)
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.material3)
                api(compose.materialIconsExtended)
                api(Libs.Others.moko_resources)
                api(Libs.Others.moko_compose)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
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
    }
}

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    namespace = "com.ohyooo.calendar.common"
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

multiplatformResources {
    multiplatformResourcesPackage = "com.ohyooo.common"
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