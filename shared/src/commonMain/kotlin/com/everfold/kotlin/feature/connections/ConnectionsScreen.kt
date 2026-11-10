package com.everfold.kotlin.feature.connections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.everfold.kotlin.ui.components.SettingsLayout

@Composable
fun ConnectionsScreen(
    mode: String,
    address: String,
    previewConnected: Boolean,
    onModeChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onTogglePreview: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsLayout("连接", "选择你的 Agent 在哪里运行。", modifier) {
        Text("运行方式", style = MaterialTheme.typography.titleMedium)
        Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("本地运行" to "在当前设备上使用 Agent", "远程连接" to "连接另一台设备或服务器上的 Agent").forEach { (name, detail) ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color =
                        if (mode ==
                            name
                        ) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerLow
                        },
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                mode == name,
                                role = Role.RadioButton,
                                onClick = { onModeChange(name) },
                            ).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        RadioButton(selected = mode == name, onClick = null)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(name, style = MaterialTheme.typography.titleMedium)
                            Text(detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
        if (mode == "远程连接") {
            OutlinedTextField(
                address,
                onAddressChange,
                Modifier.fillMaxWidth(),
                label = { Text("服务器地址") },
                singleLine = true,
                supportingText = { Text("仅用于界面预览，不会发送网络请求") },
            )
        }
        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(if (previewConnected) "已连接 · 示例状态" else "尚未连接", style = MaterialTheme.typography.titleMedium)
                Text(
                    "当前为界面预览。本地运行和远程连接将在 Agent 库接入后开放。",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedButton(onClick = onTogglePreview) { Text(if (previewConnected) "重置预览状态" else "预览连接状态") }
            }
        }
    }
}
