package com.everfold.kotlin.ui.localization

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import java.util.Locale

@Composable
internal actual fun systemLanguageTag(): String = LocalConfiguration.current.locales[0].toLanguageTag()

internal actual fun applyResourceLocale(languageTag: String) {
    val locale = Locale.forLanguageTag(languageTag)
    if (Locale.getDefault() != locale) Locale.setDefault(locale)
}
