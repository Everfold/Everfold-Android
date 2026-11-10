package com.everfold.kotlin.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.custom
import everfold.shared.generated.resources.follow_system
import everfold.shared.generated.resources.preset_amber
import everfold.shared.generated.resources.preset_forest
import everfold.shared.generated.resources.preset_iris
import everfold.shared.generated.resources.preset_ocean
import everfold.shared.generated.resources.preset_rose
import everfold.shared.generated.resources.preset_teal
import org.jetbrains.compose.resources.StringResource

enum class ThemePreset(
    val label: StringResource,
    val seed: Color,
) {
    Forest(Res.string.preset_forest, Color(0xFF426653)),
    Ocean(Res.string.preset_ocean, Color(0xFF386A9D)),
    Iris(Res.string.preset_iris, Color(0xFF76558D)),
    Rose(Res.string.preset_rose, Color(0xFF9C4F69)),
    Amber(Res.string.preset_amber, Color(0xFF866016)),
    Teal(Res.string.preset_teal, Color(0xFF006A6A)),
}

enum class ThemeColorMode { System, Preset, Custom }

data class ThemeColorSelection(
    val mode: ThemeColorMode = ThemeColorMode.System,
    val preset: ThemePreset = ThemePreset.Forest,
    val customSeed: Color = ThemePreset.Forest.seed,
) {
    val seed: Color
        get() =
            when (mode) {
                ThemeColorMode.System -> ThemePreset.Forest.seed
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
