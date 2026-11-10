package com.everfold.kotlin.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

// A missing platform palette falls back to the shared default theme.
@Composable
internal expect fun systemColorScheme(dark: Boolean): ColorScheme?
