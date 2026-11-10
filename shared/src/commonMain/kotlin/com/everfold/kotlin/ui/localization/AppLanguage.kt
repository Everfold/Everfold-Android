// Locale is an application-wide resource dependency, like MaterialTheme.
@file:Suppress("ktlint:compose:compositionlocal-allowlist")

package com.everfold.kotlin.ui.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.staticCompositionLocalOf
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

enum class AppLanguage { System, Chinese, English }

internal fun AppLanguage.resolve(systemTag: String): AppLanguage {
    if (this != AppLanguage.System) return this
    val systemLanguage = systemTag.substringBefore('-').substringBefore('_')
    return if (systemLanguage.equals("en", ignoreCase = true)) AppLanguage.English else AppLanguage.Chinese
}

private val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.Chinese }

@Composable
internal fun AppLanguageEnvironment(
    language: AppLanguage,
    content: @Composable () -> Unit,
) {
    val resolved = language.resolve(systemLanguageTag())
    applyResourceLocale(if (resolved == AppLanguage.English) "en" else "zh")
    CompositionLocalProvider(LocalAppLanguage provides resolved, content = content)
}

// Key only the resource lookup: the surrounding UI, drafts and navigation retain their state.
@Composable
fun localizedString(
    resource: StringResource,
    vararg args: Any,
): String =
    key(LocalAppLanguage.current) {
        stringResource(resource, *args)
    }

@Composable
internal expect fun systemLanguageTag(): String

internal expect fun applyResourceLocale(languageTag: String)
