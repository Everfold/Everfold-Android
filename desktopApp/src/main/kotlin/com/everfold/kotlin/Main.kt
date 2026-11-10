package com.everfold.kotlin

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Everfold",
            state = rememberWindowState(width = 1200.dp, height = 820.dp),
        ) {
            App()
        }
    }
