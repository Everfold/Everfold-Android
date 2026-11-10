package com.everfold.kotlin.ui.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

enum class AppSymbol(val path: String) {
    Chat("M4,4 L20,4 L20,16 L9,16 L4,20 Z M8,8 L16,8 M8,12 L13,12"),
    Connection("M8,7 L8,3 M16,7 L16,3 M5,7 L19,7 L19,10 C19,14 16,16 12,16 C8,16 5,14 5,10 Z M12,16 L12,21"),
    Settings("M4,6 L20,6 M4,12 L20,12 M4,18 L20,18 M8,3 L8,9 M16,9 L16,15 M10,15 L10,21"),
    Plus("M12,5 L12,19 M5,12 L19,12"),
    Back("M19,12 L5,12 M11,6 L5,12 L11,18"),
    Send("M12,19 L12,5 M5,12 L12,5 L19,12"),
    Stop("M6,6 L18,6 L18,18 L6,18 Z"),
    Search("M10,3 C6,3 3,6 3,10 C3,14 6,17 10,17 C14,17 17,14 17,10 C17,6 14,3 10,3 Z M15,15 L21,21"),
    Spark("M12,3 L14.5,9.5 L21,12 L14.5,14.5 L12,21 L9.5,14.5 L3,12 L9.5,9.5 Z"),
}

private val vectors = AppSymbol.entries.associateWith { symbol ->
    ImageVector.Builder(symbol.name, 24.dp, 24.dp, 24f, 24f).apply {
        addPath(
            pathData = addPathNodes(symbol.path), pathFillType = PathFillType.NonZero,
            fill = null, stroke = SolidColor(Color.Black), strokeLineWidth = 1.7f,
            strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round,
        )
    }.build()
}

@Composable
fun AppIcon(symbol: AppSymbol, description: String? = null, modifier: Modifier = Modifier) {
    Icon(vectors.getValue(symbol), contentDescription = description, modifier = modifier)
}
