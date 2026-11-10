package com.everfold.kotlin.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

// Multipliers preserve the user's system accessibility font scale.
enum class ConversationTextSize(
    val scale: Float,
) {
    System(1f),
    Small(0.875f),
    Large(1.125f),
    ExtraLarge(1.25f),
}

enum class SendShortcut {
    ModifiedEnter,
    Enter,
    ;

    fun matches(
        ctrl: Boolean,
        meta: Boolean,
        shift: Boolean,
        alt: Boolean,
        desktop: Boolean,
        composing: Boolean,
    ): Boolean {
        if (composing || shift || alt) return false
        return when (this) {
            ModifiedEnter -> ctrl || meta
            Enter -> desktop && !ctrl && !meta
        }
    }
}

@Composable
internal fun ConversationTextScale(
    size: ConversationTextSize,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(density.density, density.fontScale * size.scale), content = content)
}
