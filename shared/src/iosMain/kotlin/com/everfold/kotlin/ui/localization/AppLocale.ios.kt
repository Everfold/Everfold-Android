package com.everfold.kotlin.ui.localization

import androidx.compose.runtime.Composable
import platform.Foundation.NSArgumentDomain
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.preferredLanguages

private val systemLanguage = NSLocale.preferredLanguages.firstOrNull() as? String ?: "zh"
private var appliedLanguage: String? = null

@Composable
internal actual fun systemLanguageTag(): String = systemLanguage

internal actual fun applyResourceLocale(languageTag: String) {
    if (appliedLanguage != languageTag) {
        // Volatile preferences affect resource lookup only for this process, not the next launch.
        NSUserDefaults.standardUserDefaults.setVolatileDomain(
            NSUserDefaults.standardUserDefaults.volatileDomainForName(NSArgumentDomain) +
                mapOf<Any?, Any?>("AppleLanguages" to listOf(languageTag)),
            forName = NSArgumentDomain,
        )
        appliedLanguage = languageTag
    }
}
