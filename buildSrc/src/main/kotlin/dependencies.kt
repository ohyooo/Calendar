object Ext {
    const val applicationId = "com.ohyooo.calendar"
    const val minSdk = 21
    const val compileSdk = 34
    const val targetSdk = 34
    const val versionCode = 22
    const val versionName = "3.3"
}

object Libs {
    val updateList = arrayListOf<String>()
    val implementList = arrayListOf<String>()
    val debugImplementList = arrayListOf<String>()

    object Version {
        const val agp = "8.1.2"
        const val kotlin = "1.9.10"
        const val compose = "1.5.3"
    }

    object Plugin {
        val AGP = "com.android.tools.build:gradle:${Version.agp}".regUpdate()
        val KGP = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}".regUpdate()

        val list = arrayOf(AGP, KGP)
    }

    object Kotlin {
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3".regLib()
        val datetime="org.jetbrains.kotlinx:kotlinx-datetime:0.4.1".regLib()
    }

    object Compose {
        const val composeVersion = "1.5.4"
        const val compilerVersion = "1.5.3"
        val compiler = "androidx.compose.compiler:compiler:$compilerVersion".regLib()
        val animation = "androidx.compose.animation:animation:$composeVersion".regLib()
        val foundation = "androidx.compose.foundation:foundation:$composeVersion".regLib()
        val material = "androidx.compose.material3:material3:1.1.2".regLib()
        val materialIconsExtended = "androidx.compose.material:material-icons-extended:$composeVersion".regLib()
        val runtime = "androidx.compose.runtime:runtime:$composeVersion".regLib()
        val tooling = "androidx.compose.ui:ui-tooling:$composeVersion".regUpdate().regDebug()
        val preview = "androidx.compose.ui:ui-tooling-preview:$composeVersion".regLib()
        val ui = "androidx.compose.ui:ui:$composeVersion".regLib()
        val navigation_compose = "androidx.navigation:navigation-compose:2.7.4".regLib()
        val navigation_runtime = "androidx.navigation:navigation-runtime-ktx:2.7.4".regLib()
    }

    object AndroidX {
        val appcompat = "androidx.appcompat:appcompat:1.6.1".regLib()
        val coreKtx = "androidx.core:core-ktx:1.12.0".regLib()
        val fragmentKtx = "androidx.fragment:fragment-ktx:1.6.1".regLib()
        val compose = "androidx.activity:activity-compose:1.8.0".regLib()
        val profileinstaller = "androidx.profileinstaller:profileinstaller:1.3.1".regLib()
        val startup = "androidx.startup:startup-runtime:1.1.1".regLib()
    }

    object Google {
        val accompanistVersion = "0.32.0"
        val pager = "com.google.accompanist:accompanist-pager:$accompanistVersion".regLib()
        val indicators = "com.google.accompanist:accompanist-pager-indicators:$accompanistVersion".regLib()
    }

    init {
        Plugin
        Kotlin
        Compose
        AndroidX
        Google
    }

    private fun String.regLib() = this.also {
        implementList.add(it)
        updateList.add(it)
    }

    fun String.regDebug() = this.also {
        debugImplementList.add(it)
        updateList.add(it)
    }

    fun String.regUpdate() = this.also(updateList::add)
}
