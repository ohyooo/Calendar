package com.ohyooo.calendar

import App
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sum = NativeBridge.add(2, 3)
        Log.i("NativeDemo", "calendar_native_add result=$sum")
        setContent {
            App()
        }
    }
}