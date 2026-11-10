package com.everfold.kotlin.ui.localization

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource

sealed interface UiText {
    data class Literal(
        val value: String,
    ) : UiText

    data class Resource(
        val value: StringResource,
    ) : UiText
}

@Composable
fun UiText.resolve(): String =
    when (this) {
        is UiText.Literal -> value
        is UiText.Resource -> localizedString(value)
    }
