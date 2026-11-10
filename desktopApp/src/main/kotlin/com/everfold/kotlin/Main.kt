package com.everfold.kotlin

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.everfold.kotlin.ui.components.DesktopTitleBar

fun main() =
    application {
        val windowState = rememberWindowState(width = 1200.dp, height = 820.dp)
        val os = System.getProperty("os.name").lowercase()
        val customTitleBar = os.startsWith("windows") || os.startsWith("linux")
        Window(
            onCloseRequest = ::exitApplication,
            title = "Everfold",
            state = windowState,
            undecorated = customTitleBar,
        ) {
            App(titleBar = {
                if (customTitleBar) DesktopTitleBar(windowState, ::exitApplication)
            })
        }
    }
