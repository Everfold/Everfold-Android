package com.everfold.kotlin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.rememberDynamicColorScheme

enum class ThemeMode(val label: String) {
    System("跟随系统"), Light("浅色"), Dark("深色")
}

@Composable
fun EverfoldTheme(
    mode: ThemeMode = ThemeMode.System,
    seed: Color = ThemePreset.Forest.seed,
    content: @Composable () -> Unit,
) {
    val dark = when (mode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }
    val colors = rememberDynamicColorScheme(seedColor = seed, isDark = dark)
    MaterialTheme(colorScheme = colors, content = content)
}
