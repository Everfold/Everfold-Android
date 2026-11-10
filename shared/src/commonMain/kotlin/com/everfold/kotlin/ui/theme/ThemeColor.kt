package com.everfold.kotlin.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

enum class ThemePreset(val label: String, val seed: Color) {
    Forest("松绿", Color(0xFF426653)),
    Ocean("海蓝", Color(0xFF386A9D)),
    Iris("鸢紫", Color(0xFF76558D)),
    Rose("玫瑰", Color(0xFF9C4F69)),
    Amber("琥珀", Color(0xFF866016)),
    Teal("青碧", Color(0xFF006A6A)),
}

data class ThemeColorSelection(
    val preset: ThemePreset? = ThemePreset.Forest,
    val customSeed: Color = ThemePreset.Forest.seed,
) {
    val seed: Color get() = preset?.seed ?: customSeed
    val label: String get() = preset?.label ?: "自定义"
}

internal fun Color.toHexColor(): String = "#" + (toArgb() and 0xFFFFFF).toString(16).uppercase().padStart(6, '0')

internal fun parseHexColor(value: String): Color? {
    val hex = value.trim().removePrefix("#")
    if (hex.length != 6 || hex.any { it !in '0'..'9' && it.lowercaseChar() !in 'a'..'f' }) return null
    return Color(0xFF000000.toInt() or (hex.toIntOrNull(16) ?: return null))
}
