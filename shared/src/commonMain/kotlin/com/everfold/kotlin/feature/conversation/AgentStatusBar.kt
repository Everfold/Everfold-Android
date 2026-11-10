package com.everfold.kotlin.feature.conversation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.everfold.kotlin.ui.components.AppIcon
import com.everfold.kotlin.ui.components.AppSymbol
import com.everfold.kotlin.ui.components.AppTooltip
import com.everfold.kotlin.ui.localization.localizedString
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.agent_completed
import everfold.shared.generated.resources.agent_continue
import everfold.shared.generated.resources.agent_failed
import everfold.shared.generated.resources.agent_failed_hint
import everfold.shared.generated.resources.agent_idle
import everfold.shared.generated.resources.agent_retry
import everfold.shared.generated.resources.agent_running
import everfold.shared.generated.resources.agent_step_prepare
import everfold.shared.generated.resources.agent_step_reply
import everfold.shared.generated.resources.agent_stopped
import everfold.shared.generated.resources.agent_waiting
import everfold.shared.generated.resources.agent_waiting_hint
import everfold.shared.generated.resources.execution_details
import everfold.shared.generated.resources.preview_agent_states

@Composable
internal fun AgentStatusBar(
    state: AgentRunState,
    onRetry: () -> Unit,
) {
    if (state == AgentRunState.Idle) return

    var expanded by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                localizedString(state.label),
                Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge,
                color = if (state == AgentRunState.Failed) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TextButton(onClick = { expanded = !expanded }) { Text(localizedString(Res.string.execution_details)) }
        }
        if (state == AgentRunState.Running) LinearProgressIndicator(Modifier.fillMaxWidth())
        if (state == AgentRunState.Waiting || state == AgentRunState.Failed) {
            Text(
                localizedString(
                    if (state ==
                        AgentRunState.Waiting
                    ) {
                        Res.string.agent_waiting_hint
                    } else {
                        Res.string.agent_failed_hint
                    },
                ),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        if (state == AgentRunState.Waiting || state == AgentRunState.Failed || state == AgentRunState.Stopped) {
            TextButton(onClick = onRetry) {
                Text(
                    localizedString(
                        if (state ==
                            AgentRunState.Waiting || state == AgentRunState.Stopped
                        ) {
                            Res.string.agent_continue
                        } else {
                            Res.string.agent_retry
                        },
                    ),
                )
            }
        }
        if (expanded) {
            Text("1. " + localizedString(Res.string.agent_step_prepare), style = MaterialTheme.typography.bodySmall)
            Text(
                "2. " + localizedString(Res.string.agent_step_reply) + " · " + localizedString(state.label),
                Modifier.padding(bottom = 12.dp),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
internal fun AgentPreviewMenu(onSelect: (AgentRunState) -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val label = localizedString(Res.string.preview_agent_states)
    Box {
        AppTooltip(label) {
            IconButton(onClick = { expanded = true }) { AppIcon(AppSymbol.More, description = label) }
        }
        DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
            AgentRunState.entries.forEach { state ->
                DropdownMenuItem(text = { Text(localizedString(state.label)) }, onClick = {
                    expanded = false
                    onSelect(state)
                })
            }
        }
    }
}

internal val AgentRunState.label
    get() =
        when (this) {
            AgentRunState.Idle -> Res.string.agent_idle
            AgentRunState.Running -> Res.string.agent_running
            AgentRunState.Waiting -> Res.string.agent_waiting
            AgentRunState.Completed -> Res.string.agent_completed
            AgentRunState.Stopped -> Res.string.agent_stopped
            AgentRunState.Failed -> Res.string.agent_failed
        }
