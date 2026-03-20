package com.example.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ExampleApplication : Application() {
    // Hilt автоматически генерирует компонент
}