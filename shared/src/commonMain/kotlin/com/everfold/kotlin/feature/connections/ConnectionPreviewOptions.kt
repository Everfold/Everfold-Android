package com.everfold.kotlin.feature.connections

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.everfold.kotlin.ui.localization.localizedString
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.connection_success
import everfold.shared.generated.resources.failure_certificate_hint
import everfold.shared.generated.resources.failure_credentials_hint
import everfold.shared.generated.resources.failure_timeout_hint
import everfold.shared.generated.resources.preview_certificate
import everfold.shared.generated.resources.preview_credentials
import everfold.shared.generated.resources.preview_result
import everfold.shared.generated.resources.preview_success
import everfold.shared.generated.resources.preview_timeout

internal enum class ConnectionPreviewResult { Success, Timeout, Credentials, Certificate }

internal val ConnectionPreviewResult.description
    get() =
        when (this) {
            ConnectionPreviewResult.Success -> Res.string.connection_success
            ConnectionPreviewResult.Timeout -> Res.string.failure_timeout_hint
            ConnectionPreviewResult.Credentials -> Res.string.failure_credentials_hint
            ConnectionPreviewResult.Certificate -> Res.string.failure_certificate_hint
        }

private val ConnectionPreviewResult.label
    get() =
        when (this) {
            ConnectionPreviewResult.Success -> Res.string.preview_success
            ConnectionPreviewResult.Timeout -> Res.string.preview_timeout
            ConnectionPreviewResult.Credentials -> Res.string.preview_credentials
            ConnectionPreviewResult.Certificate -> Res.string.preview_certificate
        }

@Composable
internal fun ConnectionPreviewOptions(
    selected: ConnectionPreviewResult,
    onSelect: (ConnectionPreviewResult) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(localizedString(Res.string.preview_result) + " · " + localizedString(selected.label))
        }
        DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
            ConnectionPreviewResult.entries.forEach { result ->
                DropdownMenuItem(text = { Text(localizedString(result.label)) }, onClick = {
                    onSelect(result)
                    expanded = false
                })
            }
        }
    }
}
