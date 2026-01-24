plugins {
    alias(libs.plugins.kmm)
}

group = "com.ohyooo"
version = "1.0.0"

kotlin {
    val androidNativeTargets = listOf(
        androidNativeArm64("androidNativeArm64"),
        androidNativeArm32("androidNativeArm32"),
        androidNativeX64("androidNativeX64"),
        androidNativeX86("androidNativeX86")
    )
    androidNativeTargets.forEach { target ->
        target.binaries {
            sharedLib {
                baseName = "calendar_native"
            }
        }
    }

    sourceSets {
        val androidNativeMain by creating
        val androidNativeArm64Main by getting {
            dependsOn(androidNativeMain)
        }
        val androidNativeArm32Main by getting {
            dependsOn(androidNativeMain)
        }
        val androidNativeX64Main by getting {
            dependsOn(androidNativeMain)
        }
        val androidNativeX86Main by getting {
            dependsOn(androidNativeMain)
        }
    }
}
