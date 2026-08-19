package com.fiverules

import androidx.compose.ui.window.ComposeUIViewController
import com.fiverules.common.di.initKoin

fun MainViewController() = run {
    initKoin()
    ComposeUIViewController { App() }
}
