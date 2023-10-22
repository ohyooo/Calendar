import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.ohyooo"
version = "1.0.0"

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose {
    kotlinCompilerPlugin.set(Libs.Compose.compiler)
    desktop {
        application {
            mainClass = "MainKt"
            nativeDistributions {
                targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
                packageName = "Calendar"
                packageVersion = "1.0.0"

                windows {
                    menu = true
                    // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                    upgradeUuid = "ad2078b3-5090-46cb-bf1c-314907e1ce7b"
                }

                macOS {
                    bundleID = "com.ohyooo.calendar.widgets"
                }
            }
            buildTypes.release.proguard {
                configurationFiles.from("proguard.pro")
            }
        }
    }
}

