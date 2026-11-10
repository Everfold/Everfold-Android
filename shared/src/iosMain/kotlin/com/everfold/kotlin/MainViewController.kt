package com.everfold.kotlin

import androidx.compose.ui.window.ComposeUIViewController

// Keep the exported factory name used by the Swift application.
@Suppress("ktlint:standard:function-naming")
fun MainViewController() = ComposeUIViewController { App() }
