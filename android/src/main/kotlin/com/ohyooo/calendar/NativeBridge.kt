package com.ohyooo.calendar

object NativeBridge {
    init {
        System.loadLibrary("calendar_native")
        System.loadLibrary("calendar_jni")
    }

    external fun add(a: Int, b: Int): Int
}
