package com.everfold.kotlin.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.everfold.kotlin.ui.components.SettingsLayout
import com.everfold.kotlin.ui.localization.AppLanguage
import com.everfold.kotlin.ui.localization.localizedString
import com.everfold.kotlin.ui.theme.ThemeColorSelection
import com.everfold.kotlin.ui.theme.ThemeMode
import com.everfold.kotlin.ui.theme.ThemePreset
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.about
import everfold.shared.generated.resources.app_tagline
import everfold.shared.generated.resources.appearance
import everfold.shared.generated.resources.nav_settings
import everfold.shared.generated.resources.preview_notice
import everfold.shared.generated.resources.preview_version
import everfold.shared.generated.resources.settings_subtitle

@Composable
fun SettingsScreen(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    themeMode: ThemeMode,
    themeColor: ThemeColorSelection,
    onThemeChange: (ThemeMode) -> Unit,
    onSystemColorChange: () -> Unit,
    onPresetChange: (ThemePreset) -> Unit,
    onCustomColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsLayout(localizedString(Res.string.nav_settings), localizedString(Res.string.settings_subtitle), modifier) {
        LanguageSettings(language, onLanguageChange)
        Text(localizedString(Res.string.appearance), style = MaterialTheme.typography.titleMedium)
        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
            Column(Modifier.fillMaxWidth().selectableGroup().padding(vertical = 8.dp)) {
                ThemeMode.entries.forEach { mode ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(themeMode == mode, role = Role.RadioButton, onClick = { onThemeChange(mode) })
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        RadioButton(themeMode == mode, onClick = null)
                        Text(localizedString(mode.label))
                    }
                }
            }
        }
        ThemeColorSettings(themeColor, onSystemColorChange, onPresetChange, onCustomColorChange)
        Text(localizedString(Res.string.about), style = MaterialTheme.typography.titleMedium)
        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
            Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Everfold", style = MaterialTheme.typography.titleLarge)
                Text(localizedString(Res.string.app_tagline), color = MaterialTheme.colorScheme.onSurfaceVariant)
                HorizontalDivider()
                Text(localizedString(Res.string.preview_version), style = MaterialTheme.typography.labelLarge)
                Text(
                    localizedString(Res.string.preview_notice),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
