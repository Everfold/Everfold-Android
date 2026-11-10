package com.everfold.kotlin.feature.connections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.everfold.kotlin.ui.components.SettingsLayout
import com.everfold.kotlin.ui.localization.localizedString
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.connected_preview
import everfold.shared.generated.resources.connection_failure
import everfold.shared.generated.resources.connection_mode
import everfold.shared.generated.resources.connection_notice
import everfold.shared.generated.resources.connection_preview_notice
import everfold.shared.generated.resources.connections_subtitle
import everfold.shared.generated.resources.invalid_connection
import everfold.shared.generated.resources.nav_connections
import everfold.shared.generated.resources.not_connected
import everfold.shared.generated.resources.simulate_failure
import everfold.shared.generated.resources.test_connection
import everfold.shared.generated.resources.testing_connection
import kotlinx.coroutines.delay

@Composable
fun ConnectionsScreen(
    mode: ConnectionMode,
    setup: ClusterSetup,
    previewConnected: Boolean,
    onModeChange: (ConnectionMode) -> Unit,
    onSetupChange: (ClusterSetup) -> Unit,
    onConnectionChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var testing by remember(mode, setup) { mutableStateOf(false) }
    var failed by remember(mode, setup) { mutableStateOf(false) }
    var showError by remember(mode, setup) { mutableStateOf(false) }
    var failTest by remember { mutableStateOf(false) }
    val updateConnection by rememberUpdatedState(onConnectionChange)
    LaunchedEffect(testing, mode, setup, failTest) {
        if (testing) {
            delay(900)
            testing = false
            failed = failTest
            updateConnection(!failTest)
        }
    }
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
                            Text(localizedString(item.description), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
        if (mode == ConnectionMode.Remote) {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                ClusterConnectionForm(setup, wide = maxWidth >= 600.dp, onChange = onSetupChange)
            }
            Text(localizedString(Res.string.connection_preview_notice), style = MaterialTheme.typography.bodySmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(localizedString(Res.string.simulate_failure), Modifier.weight(1f))
                Switch(failTest, onCheckedChange = {
                    failTest = it
                    testing = false
                    failed = false
                    onConnectionChange(false)
                })
            }
            if (showError) Text(localizedString(Res.string.invalid_connection), color = MaterialTheme.colorScheme.error)
            if (testing) LinearProgressIndicator(Modifier.fillMaxWidth())
            Text(
                localizedString(
                    if (failed) {
                        Res.string.connection_failure
                    } else if (previewConnected) {
                        Res.string.connected_preview
                    } else {
                        Res.string.not_connected
                    },
                ),
                color = if (failed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(onClick = {
                showError = !setup.isValid
                if (setup.isValid) {
                    failed = false
                    onConnectionChange(false)
                    testing = true
                }
            }, enabled = !testing) {
                Text(localizedString(if (testing) Res.string.testing_connection else Res.string.test_connection))
            }
        } else {
            Text(localizedString(Res.string.connection_notice), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
