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
import com.everfold.kotlin.ui.localization.localizedString
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.address_preview_hint
import everfold.shared.generated.resources.connected_preview
import everfold.shared.generated.resources.connection_mode
import everfold.shared.generated.resources.connection_notice
import everfold.shared.generated.resources.connections_subtitle
import everfold.shared.generated.resources.nav_connections
import everfold.shared.generated.resources.not_connected
import everfold.shared.generated.resources.preview_connection
import everfold.shared.generated.resources.reset_connection
import everfold.shared.generated.resources.server_address

@Composable
fun ConnectionsScreen(
    mode: ConnectionMode,
    address: String,
    previewConnected: Boolean,
    onModeChange: (ConnectionMode) -> Unit,
    onAddressChange: (String) -> Unit,
    onTogglePreview: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsLayout(localizedString(Res.string.nav_connections), localizedString(Res.string.connections_subtitle), modifier) {
        Text(localizedString(Res.string.connection_mode), style = MaterialTheme.typography.titleMedium)
        Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ConnectionMode.entries.forEach { item ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color =
                        if (mode ==
                            item
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
                                mode == item,
                                role = Role.RadioButton,
                                onClick = { onModeChange(item) },
                            ).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        RadioButton(selected = mode == item, onClick = null)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(localizedString(item.label), style = MaterialTheme.typography.titleMedium)
                            Text(
                                localizedString(item.description),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
        if (mode == ConnectionMode.Remote) {
            OutlinedTextField(
                address,
                onAddressChange,
                Modifier.fillMaxWidth(),
                label = { Text(localizedString(Res.string.server_address)) },
                singleLine = true,
                supportingText = { Text(localizedString(Res.string.address_preview_hint)) },
            )
        }
        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    if (previewConnected) localizedString(Res.string.connected_preview) else localizedString(Res.string.not_connected),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    localizedString(Res.string.connection_notice),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedButton(onClick = onTogglePreview) {
                    Text(
                        if (previewConnected) {
                            localizedString(
                                Res.string.reset_connection,
                            )
                        } else {
                            localizedString(Res.string.preview_connection)
                        },
                    )
                }
            }
        }
    }
}
