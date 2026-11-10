package com.everfold.kotlin.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.custom
import everfold.shared.generated.resources.follow_system
import everfold.shared.generated.resources.preset_blue
import everfold.shared.generated.resources.preset_green
import everfold.shared.generated.resources.preset_orange
import everfold.shared.generated.resources.preset_pink
import everfold.shared.generated.resources.preset_purple
import everfold.shared.generated.resources.preset_teal
import org.jetbrains.compose.resources.StringResource

enum class ThemePreset(
    val label: StringResource,
    val seed: Color,
) {
    Purple(Res.string.preset_purple, Color(0xFF673AB7)),
    Blue(Res.string.preset_blue, Color(0xFF2196F3)),
    Teal(Res.string.preset_teal, Color(0xFF009688)),
    Green(Res.string.preset_green, Color(0xFF4CAF50)),
    Orange(Res.string.preset_orange, Color(0xFFFF9800)),
    Pink(Res.string.preset_pink, Color(0xFFE91E63)),
}

enum class ThemeColorMode { System, Preset, Custom }

data class ThemeColorSelection(
    val mode: ThemeColorMode = ThemeColorMode.System,
    val preset: ThemePreset = ThemePreset.Purple,
    val customSeed: Color = ThemePreset.Purple.seed,
) {
    val seed: Color
        get() =
            when (mode) {
                ThemeColorMode.System -> ThemePreset.Purple.seed
                ThemeColorMode.Preset -> preset.seed
                ThemeColorMode.Custom -> customSeed
            }
    val label: StringResource
        get() =
            when (mode) {
                ThemeColorMode.System -> Res.string.follow_system
                ThemeColorMode.Preset -> preset.label
                ThemeColorMode.Custom -> Res.string.custom
            }
}

internal fun Color.toHexColor(): String = "#" + (toArgb() and 0xFFFFFF).toString(16).uppercase().padStart(6, '0')

internal fun parseHexColor(value: String): Color? {
    val hex = value.trim().removePrefix("#")
    if (hex.length != 6 || hex.any { it !in '0'..'9' && it.lowercaseChar() !in 'a'..'f' }) return null
    return Color(0xFF000000.toInt() or (hex.toIntOrNull(16) ?: return null))
}
