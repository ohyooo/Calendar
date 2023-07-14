package com.ohyooo.common

import android.content.Context
import androidx.startup.Initializer

class ContextInitializer : Initializer<Context> {
    override fun create(context: Context) = context.also { App.context = it }

    override fun dependencies() = emptyList<Class<out Initializer<*>>>()
}