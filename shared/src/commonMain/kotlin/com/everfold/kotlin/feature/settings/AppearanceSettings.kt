package com.everfold.kotlin.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.everfold.kotlin.ui.localization.localizedString
import com.everfold.kotlin.ui.theme.ThemeMode
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.appearance

@Composable
internal fun AppearanceSettings(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    horizontal: Boolean = false,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(localizedString(Res.string.appearance), style = MaterialTheme.typography.titleMedium)
        if (horizontal) {
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                ThemeMode.entries.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = selected == mode,
                        onClick = { onSelect(mode) },
                        shape = SegmentedButtonDefaults.itemShape(index, ThemeMode.entries.size),
                    ) {
                        Text(localizedString(mode.label))
                    }
                }
            }
        } else {
            Column(Modifier.selectableGroup()) {
                ThemeMode.entries.forEach { mode ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected == mode,
                                role = Role.RadioButton,
                                onClick = { onSelect(mode) },
                            ).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        RadioButton(selected == mode, onClick = null)
                        Text(localizedString(mode.label))
                    }
                }
            }
        }
    }
}
