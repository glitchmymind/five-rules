package com.fiverules

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.fiverules.common.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Five Rules",
        ) {
            App()
        }
    }
}
