package com.everfold.kotlin.feature.connections

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.everfold.kotlin.feature.settings.LanguageSettings
import com.everfold.kotlin.feature.settings.ThemeColorSettings
import com.everfold.kotlin.mock.MockWorkspace
import com.everfold.kotlin.ui.localization.AppLanguage
import com.everfold.kotlin.ui.localization.localizedString
import com.everfold.kotlin.ui.theme.ThemeMode
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.account
import everfold.shared.generated.resources.appearance
import everfold.shared.generated.resources.auth_certificate
import everfold.shared.generated.resources.auth_method
import everfold.shared.generated.resources.auth_mtls
import everfold.shared.generated.resources.auth_none
import everfold.shared.generated.resources.auth_password
import everfold.shared.generated.resources.ca_certificate
import everfold.shared.generated.resources.cancel
import everfold.shared.generated.resources.choose_file
import everfold.shared.generated.resources.client_private_key
import everfold.shared.generated.resources.client_public_key
import everfold.shared.generated.resources.cluster_central
import everfold.shared.generated.resources.cluster_cloud
import everfold.shared.generated.resources.cluster_hint
import everfold.shared.generated.resources.cluster_lan
import everfold.shared.generated.resources.connection_failure
import everfold.shared.generated.resources.connection_preview_notice
import everfold.shared.generated.resources.connection_success
import everfold.shared.generated.resources.enter_home
import everfold.shared.generated.resources.feature_agent
import everfold.shared.generated.resources.feature_agent_detail
import everfold.shared.generated.resources.feature_modes
import everfold.shared.generated.resources.feature_modes_detail
import everfold.shared.generated.resources.feature_sessions
import everfold.shared.generated.resources.feature_sessions_detail
import everfold.shared.generated.resources.import_hint
import everfold.shared.generated.resources.import_preview
import everfold.shared.generated.resources.invalid_connection
import everfold.shared.generated.resources.ip_address
import everfold.shared.generated.resources.lan_hint
import everfold.shared.generated.resources.mode_cluster
import everfold.shared.generated.resources.mode_offline
import everfold.shared.generated.resources.next_step
import everfold.shared.generated.resources.no_file
import everfold.shared.generated.resources.offline_hint
import everfold.shared.generated.resources.onboarding_intro
import everfold.shared.generated.resources.onboarding_welcome
import everfold.shared.generated.resources.password
import everfold.shared.generated.resources.port
import everfold.shared.generated.resources.previous_step
import everfold.shared.generated.resources.scan_file
import everfold.shared.generated.resources.scan_preview
import everfold.shared.generated.resources.setup_cluster
import everfold.shared.generated.resources.setup_mode
import everfold.shared.generated.resources.setup_nav_preferences
import everfold.shared.generated.resources.setup_nav_welcome
import everfold.shared.generated.resources.setup_preferences
import everfold.shared.generated.resources.setup_shortcuts
import everfold.shared.generated.resources.setup_sidebar_hint
import everfold.shared.generated.resources.setup_step
import everfold.shared.generated.resources.simulate_failure
import everfold.shared.generated.resources.test_connection
import everfold.shared.generated.resources.testing_connection
import everfold.shared.generated.resources.use_sample
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource

@Composable
internal fun ClusterConnectionForm(
    form: ClusterSetup,
    wide: Boolean,
    onChange: (ClusterSetup) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FlowRow(Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ClusterMethod.entries.forEach { method ->
                val label =
                    when (method) {
                        ClusterMethod.Lan -> Res.string.cluster_lan
                        ClusterMethod.Central -> Res.string.cluster_central
                        ClusterMethod.Cloud -> Res.string.cluster_cloud
                    }
                FilterChip(
                    selected = method == form.method,
                    onClick = { onChange(form.copy(method = method)) },
                    label = { Text(localizedString(label)) },
                )
            }
        }
        if (form.method != ClusterMethod.Cloud) {
            if (form.method == ClusterMethod.Lan) Text(localizedString(Res.string.lan_hint), style = MaterialTheme.typography.bodySmall)
            SetupColumns(
                wide,
                first = { SetupField(Res.string.ip_address, form.ip, { onChange(form.copy(ip = it)) }) },
                second = { SetupField(Res.string.port, form.port, { onChange(form.copy(port = it)) }) },
            )
        }
        if (form.method == ClusterMethod.Central) {
            Text(localizedString(Res.string.auth_method), style = MaterialTheme.typography.titleMedium)
            FlowRow(Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ClusterAuth.entries.forEach { auth ->
                    val label =
                        when (auth) {
                            ClusterAuth.None -> Res.string.auth_none
                            ClusterAuth.Password -> Res.string.auth_password
                            ClusterAuth.Certificate -> Res.string.auth_certificate
                            ClusterAuth.Mtls -> Res.string.auth_mtls
                        }
                    FilterChip(
                        selected = form.auth == auth,
                        onClick = { onChange(form.copy(auth = auth)) },
                        label = { Text(localizedString(label)) },
                    )
                }
            }
        }
        if (form.method == ClusterMethod.Cloud || (form.method == ClusterMethod.Central && form.auth == ClusterAuth.Password)) {
            SetupColumns(
                wide,
                first = { SetupField(Res.string.account, form.account, { onChange(form.copy(account = it)) }) },
                second = { SetupField(Res.string.password, form.password, { onChange(form.copy(password = it)) }, secret = true) },
            )
        }
        if (form.method == ClusterMethod.Central) {
            when (form.auth) {
                ClusterAuth.Certificate -> {
                    FileImportField(
                        Res.string.auth_certificate,
                        form.serverCertificate,
                        "server.pem",
                        { onChange(form.copy(serverCertificate = it)) },
                    )
                }

                ClusterAuth.Mtls -> {
                    FileImportField(Res.string.ca_certificate, form.caCertificate, "ca.pem", { onChange(form.copy(caCertificate = it)) })
                    FileImportField(
                        Res.string.client_public_key,
                        form.clientPublicKey,
                        "client-public.pem",
                        { onChange(form.copy(clientPublicKey = it)) },
                    )
                    FileImportField(
                        Res.string.client_private_key,
                        form.clientPrivateKey,
                        "client-private.pem",
                        { onChange(form.copy(clientPrivateKey = it)) },
                    )
                }

                else -> {
                    Unit
                }
            }
        }
    }
}

@Composable
private fun SetupField(
    label: StringResource,
    value: String,
    onChange: (String) -> Unit,
    secret: Boolean = false,
) {
    OutlinedTextField(value, onChange, modifier = Modifier.fillMaxWidth(), label = {
        Text(localizedString(label))
    }, singleLine = true, visualTransformation = if (secret) PasswordVisualTransformation() else VisualTransformation.None)
}

@Composable
private fun FileImportField(
    label: StringResource,
    value: String?,
    sampleName: String,
    onImport: (String) -> Unit,
) {
    var importing by remember { mutableStateOf(false) }
    var scanning by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(localizedString(label), style = MaterialTheme.typography.titleSmall)
        Text(value ?: localizedString(Res.string.no_file), style = MaterialTheme.typography.bodySmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = {
                scanning = false
                importing = true
            }) { Text(localizedString(Res.string.choose_file)) }
            OutlinedButton(onClick = {
                scanning = true
                importing = true
            }) { Text(localizedString(Res.string.scan_file)) }
        }
    }
    if (importing) {
        AlertDialog(
            onDismissRequest = { importing = false },
            title = { Text(localizedString(if (scanning) Res.string.scan_preview else Res.string.import_preview)) },
            text = { Text(localizedString(Res.string.import_hint)) },
            confirmButton = {
                TextButton(onClick = {
                    onImport(sampleName)
                    importing = false
                }) { Text(localizedString(Res.string.use_sample)) }
            },
            dismissButton = { TextButton(onClick = { importing = false }) { Text(localizedString(Res.string.cancel)) } },
        )
    }
}

@Composable
private fun SetupColumns(
    wide: Boolean,
    first: @Composable () -> Unit,
    second: @Composable () -> Unit,
) {
    FlowRow(
        Modifier.fillMaxWidth(),
        maxItemsInEachRow = if (wide) 2 else 1,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) { first() }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) { second() }
    }
}
