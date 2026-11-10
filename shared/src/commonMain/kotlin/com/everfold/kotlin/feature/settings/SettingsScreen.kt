package com.everfold.kotlin.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.everfold.kotlin.ui.components.SettingsLayout
import com.everfold.kotlin.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
) {
    SettingsLayout("设置", "让这个空间更适合你。") {
        Text("外观", style = MaterialTheme.typography.titleMedium)
        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
            Column(Modifier.fillMaxWidth().selectableGroup().padding(vertical = 8.dp)) {
                ThemeMode.entries.forEach { mode ->
                    Row(Modifier.fillMaxWidth().selectable(themeMode == mode, role = Role.RadioButton, onClick = { onThemeChange(mode) })
                        .padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        RadioButton(themeMode == mode, onClick = null)
                        Text(mode.label)
                    }
                }
            }
        }
        Text("关于", style = MaterialTheme.typography.titleMedium)
        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
            Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Everfold", style = MaterialTheme.typography.titleLarge)
                Text("你的 Agent 对话空间", color = MaterialTheme.colorScheme.onSurfaceVariant)
                HorizontalDivider()
                Text("UI 预览版", style = MaterialTheme.typography.labelLarge)
                Text("会话、回复和连接状态均为示例数据。关闭应用后，本次修改会重置。",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
