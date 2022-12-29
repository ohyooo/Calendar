object Ext {
    const val applicationId = "com.ohyooo.calendar"
    const val minSdk = 21
    const val compileSdk = 33
    const val buildToolsVersion = "33.0.1"
    const val targetSdk = 33
    const val versionCode = 1
    const val versionName = "1.0"
}

object Libs {
    const val kotlinVersion = "1.7.21"

    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4"
    }

    object Plugin {
        const val AGP = "com.android.tools.build:gradle:7.3.1"
        const val KGP = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    }

    object Compose {
        const val composeVersion = "1.4.0-alpha03"
        const val compilerVersion = "1.4.0-alpha02"
        const val animation = "androidx.compose.animation:animation:$composeVersion"
        const val compiler = "androidx.compose.compiler:compiler:$compilerVersion"
        const val foundation = "androidx.compose.foundation:foundation:$composeVersion"
        const val layout = "androidx.compose.foundation:foundation-layout:$composeVersion"
        const val livedata = "androidx.compose.runtime:runtime-livedata:$composeVersion"
        const val material = "androidx.compose.material:material:$composeVersion"
        const val materialIconsExtended = "androidx.compose.material:material-icons-extended:$composeVersion"
        const val runtime = "androidx.compose.runtime:runtime:$composeVersion"
        const val tooling = "androidx.compose.ui:ui-tooling:$composeVersion"
        const val ui = "androidx.compose.ui:ui:$composeVersion"
    }

    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:1.9.0"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:1.5.5"
        const val compose = "androidx.activity:activity-compose:1.6.1"
    }

    val appImplements = arrayOf(
        AndroidX.coreKtx,
        AndroidX.fragmentKtx,
        AndroidX.compose,
        Compose.animation,
        Compose.compiler,
        Compose.foundation,
        Compose.layout,
        Compose.livedata,
        Compose.material,
        Compose.materialIconsExtended,
        Compose.runtime,
        Compose.tooling,
        Compose.ui,
        Kotlin.coroutines,
        Kotlin.stdlib,
    )

    val deps: List<String> = mutableSetOf<String>().apply {
        addAll(appImplements)
    }.toList()
}
