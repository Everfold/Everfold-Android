package com.everfold.kotlin.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.everfold.kotlin.ui.theme.parseHexColor
import com.everfold.kotlin.ui.theme.toHexColor
import com.materialkolor.rememberDynamicColorScheme
import kotlin.math.roundToInt

@Composable
internal fun CustomColorDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onApply: (Color) -> Unit,
) {
    var hex by rememberSaveable { mutableStateOf(initialColor.toHexColor()) }
    // Keep the last valid preview while the user is midway through editing the HEX value.
    var previewArgb by rememberSaveable { mutableStateOf(initialColor.toArgb()) }
    val parsed = parseHexColor(hex)
    val preview = Color(previewArgb)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("自定义主题色") },
        text = {
            Column(
                Modifier.fillMaxWidth().heightIn(max = 480.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("输入颜色代码或拖动滑块，预览后再应用。")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PalettePreview(preview, dark = false, Modifier.weight(1f))
                    PalettePreview(preview, dark = true, Modifier.weight(1f))
                }
                OutlinedTextField(
                    value = hex,
                    onValueChange = { value ->
                        hex = value
                        parseHexColor(value)?.let { previewArgb = it.toArgb() }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("HEX 颜色") },
                    placeholder = { Text("#426653") },
                    singleLine = true,
                    isError = parsed == null,
                    keyboardOptions =
                        KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            autoCorrectEnabled = false,
                            keyboardType = KeyboardType.Ascii,
                        ),
                    supportingText = { Text(if (parsed == null) "请输入 6 位十六进制颜色，如 #426653" else "支持 RRGGBB 或 #RRGGBB") },
                )
                listOf("红色 R" to 16, "绿色 G" to 8, "蓝色 B" to 0).forEach { (label, shift) ->
                    val channel = (previewArgb ushr shift) and 0xFF
                    Column {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(label, style = MaterialTheme.typography.labelLarge)
                            Text(channel.toString(), style = MaterialTheme.typography.labelMedium)
                        }
                        Slider(
                            value = channel.toFloat(),
                            valueRange = 0f..255f,
                            modifier = Modifier.fillMaxWidth().semantics { contentDescription = label },
                            onValueChange = { value ->
                                previewArgb = (previewArgb and (0xFF shl shift).inv()) or (value.roundToInt() shl shift)
                                hex = Color(previewArgb).toHexColor()
                            },
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(enabled = parsed != null, onClick = { parsed?.let(onApply) }) { Text("应用颜色") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
    )
}

@Composable
private fun PalettePreview(
    seed: Color,
    dark: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = rememberDynamicColorScheme(seedColor = seed, isDark = dark)
    MaterialTheme(colorScheme = colors) {
        Surface(modifier = modifier, shape = RoundedCornerShape(12.dp), color = colors.surfaceContainer) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(if (dark) "深色预览" else "浅色预览", style = MaterialTheme.typography.labelMedium)
                Surface(shape = RoundedCornerShape(12.dp), color = colors.primary, contentColor = colors.onPrimary) {
                    Text("开始对话", Modifier.padding(horizontal = 10.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    listOf(colors.primaryContainer, colors.secondaryContainer, colors.tertiaryContainer).forEach { color ->
                        Surface(Modifier.weight(1f).height(8.dp), color = color, shape = RoundedCornerShape(4.dp)) {}
                    }
                }
            }
        }
    }
}
