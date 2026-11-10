package com.everfold.kotlin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.rememberDynamicColorScheme
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.follow_system
import everfold.shared.generated.resources.theme_dark
import everfold.shared.generated.resources.theme_light
import org.jetbrains.compose.resources.StringResource

enum class ThemeMode(
    val label: StringResource,
) {
    System(Res.string.follow_system),
    Light(Res.string.theme_light),
    Dark(Res.string.theme_dark),
}

@Composable
fun EverfoldTheme(
    mode: ThemeMode = ThemeMode.System,
    seed: Color = ThemePreset.Forest.seed,
    content: @Composable () -> Unit,
) {
    val dark =
        when (mode) {
            ThemeMode.System -> isSystemInDarkTheme()
            ThemeMode.Light -> false
            ThemeMode.Dark -> true
        }
    val colors = rememberDynamicColorScheme(seedColor = seed, isDark = dark)
    MaterialTheme(colorScheme = colors, content = content)
}
