package com.everfold.kotlin.ui.localization

import androidx.compose.runtime.Composable
import java.util.Locale

// Capture the OS/JVM locale before an in-app override is applied.
private val systemLocale = Locale.getDefault()

@Composable
internal actual fun systemLanguageTag(): String = systemLocale.toLanguageTag()

internal actual fun applyResourceLocale(languageTag: String) {
    val locale = Locale.forLanguageTag(languageTag)
    if (Locale.getDefault() != locale) Locale.setDefault(locale)
}
