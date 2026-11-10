package com.everfold.kotlin.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.everfold.kotlin.ui.localization.localizedString
import com.everfold.kotlin.ui.theme.ThemeColorMode
import com.everfold.kotlin.ui.theme.ThemeColorSelection
import com.everfold.kotlin.ui.theme.ThemePreset
import com.everfold.kotlin.ui.theme.toHexColor
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.adjust
import everfold.shared.generated.resources.custom_color
import everfold.shared.generated.resources.follow_system
import everfold.shared.generated.resources.system_color_hint
import everfold.shared.generated.resources.theme_color
import everfold.shared.generated.resources.theme_description

@Composable
internal fun ThemeColorSettings(
    selection: ThemeColorSelection,
    onSystemColorChange: () -> Unit,
    onPresetChange: (ThemePreset) -> Unit,
    onCustomColorChange: (Color) -> Unit,
) {
    var showCustomDialog by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(localizedString(Res.string.theme_color), style = MaterialTheme.typography.titleMedium)
        Text(
            localizedString(Res.string.theme_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
            Column(Modifier.fillMaxWidth().selectableGroup().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .selectable(selection.mode == ThemeColorMode.System, role = Role.RadioButton, onClick = onSystemColorChange)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    RadioButton(selection.mode == ThemeColorMode.System, onClick = null)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(localizedString(Res.string.follow_system), style = MaterialTheme.typography.titleSmall)
                        Text(
                            localizedString(Res.string.system_color_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                ThemePreset.entries.chunked(3).forEach { presets ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        presets.forEach { preset ->
                            val selected = selection.mode == ThemeColorMode.Preset && selection.preset == preset
                            Column(
                                Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                                    .selectable(selected, role = Role.RadioButton, onClick = { onPresetChange(preset) })
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                ColorSwatch(preset.seed, selected)
                                Text(localizedString(preset.label), style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .selectable(selection.mode == ThemeColorMode.Custom, role = Role.RadioButton, onClick = { showCustomDialog = true })
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    ColorSwatch(selection.customSeed, selection.mode == ThemeColorMode.Custom)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(localizedString(Res.string.custom_color), style = MaterialTheme.typography.titleSmall)
                        Text(
                            selection.customSeed.toHexColor(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        localizedString(Res.string.adjust),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
    if (showCustomDialog) {
        CustomColorDialog(
            initialColor = selection.customSeed,
            onDismiss = { showCustomDialog = false },
            onApply = {
                onCustomColorChange(it)
                showCustomDialog = false
            },
        )
    }
}

@Composable
private fun ColorSwatch(
    color: Color,
    selected: Boolean,
) {
    Box(
        Modifier
            .size(48.dp)
            .border(2.dp, if (selected) MaterialTheme.colorScheme.primary else Color.Transparent, CircleShape)
            .padding(4.dp)
            .background(color, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) Text("✓", color = if (color.luminance() > 0.5f) Color.Black else Color.White)
    }
}
