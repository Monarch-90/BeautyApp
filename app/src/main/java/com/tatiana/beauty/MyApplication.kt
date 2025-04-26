package com.tatiana.beauty

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


/**
 * Класс Application, необходимый для Hilt.
 * Аннотация @HiltAndroidApp запускает генерацию кода Hilt.
 */

@HiltAndroidApp
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}