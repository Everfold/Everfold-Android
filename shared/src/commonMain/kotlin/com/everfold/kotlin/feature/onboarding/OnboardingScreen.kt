package com.everfold.kotlin.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
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
import everfold.shared.generated.resources.setup_preferences
import everfold.shared.generated.resources.setup_step
import everfold.shared.generated.resources.simulate_failure
import everfold.shared.generated.resources.test_connection
import everfold.shared.generated.resources.testing_connection
import everfold.shared.generated.resources.use_sample
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource

private enum class SetupStep { Welcome, Preferences, Mode, Cluster }

private enum class ConnectionTest { Idle, Testing, Success, Failure }

@Composable
internal fun OnboardingScreen(
    workspace: MockWorkspace,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onComplete: (ClusterSetup?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var step by remember { mutableStateOf(SetupStep.Welcome) }
    var cluster by remember { mutableStateOf(false) }
    var form by remember { mutableStateOf(ClusterSetup()) }
    var test by remember { mutableStateOf(ConnectionTest.Idle) }
    var failTest by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    LaunchedEffect(test, form, failTest, step) {
        if (test == ConnectionTest.Testing) {
            delay(900)
            test = if (failTest) ConnectionTest.Failure else ConnectionTest.Success
        }
    }
    val back = {
        if (step == SetupStep.Cluster) test = ConnectionTest.Idle
        step = SetupStep.entries[(step.ordinal - 1).coerceAtLeast(0)]
    }
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = step != SetupStep.Welcome,
        onBackCompleted = back,
    )
    Surface(modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).imePadding(), contentAlignment = Alignment.TopCenter) {
            Column(Modifier.widthIn(max = 760.dp).fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp)) {
                Text("Everfold", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Text(localizedString(Res.string.setup_step, step.ordinal + 1, if (cluster) 4 else 3), Modifier.padding(vertical = 8.dp))
                LinearProgressIndicator(progress = { (step.ordinal + 1) / (if (cluster) 4f else 3f) }, modifier = Modifier.fillMaxWidth())
                key(step) {
                    Column(
                        Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        val title =
                            when (step) {
                                SetupStep.Welcome -> Res.string.onboarding_welcome
                                SetupStep.Preferences -> Res.string.setup_preferences
                                SetupStep.Mode -> Res.string.setup_mode
                                SetupStep.Cluster -> Res.string.setup_cluster
                            }
                        Text(localizedString(title), style = MaterialTheme.typography.headlineMedium)
                        when (step) {
                            SetupStep.Welcome -> {
                                WelcomeFeatures()
                            }

                            SetupStep.Preferences -> {
                                LanguageSettings(language, onLanguageChange)
                                Text(localizedString(Res.string.appearance), style = MaterialTheme.typography.titleMedium)
                                Column(Modifier.selectableGroup()) {
                                    ThemeMode.entries.forEach { mode ->
                                        SetupChoice(mode.label, workspace.themeMode == mode, { workspace.setTheme(mode) })
                                    }
                                }
                                ThemeColorSettings(
                                    workspace.themeColor,
                                    workspace::useSystemThemeColor,
                                    workspace::selectThemePreset,
                                    workspace::applyCustomThemeColor,
                                )
                            }

                            SetupStep.Mode -> {
                                Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    SetupChoice(Res.string.mode_offline, !cluster, { cluster = false }, Res.string.offline_hint)
                                    SetupChoice(Res.string.mode_cluster, cluster, { cluster = true }, Res.string.cluster_hint)
                                }
                            }

                            SetupStep.Cluster -> {
                                ClusterConnectionForm(form, onChange = {
                                    form = it
                                    test = ConnectionTest.Idle
                                    showError = false
                                })
                                Text(localizedString(Res.string.connection_preview_notice), style = MaterialTheme.typography.bodySmall)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(localizedString(Res.string.simulate_failure), Modifier.weight(1f))
                                    Switch(failTest, onCheckedChange = {
                                        failTest = it
                                        test = ConnectionTest.Idle
                                    })
                                }
                                if (showError) Text(localizedString(Res.string.invalid_connection), color = MaterialTheme.colorScheme.error)
                                when (test) {
                                    ConnectionTest.Testing -> {
                                        LinearProgressIndicator(Modifier.fillMaxWidth())
                                    }

                                    ConnectionTest.Success -> {
                                        Text(
                                            localizedString(Res.string.connection_success),
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                    }

                                    ConnectionTest.Failure -> {
                                        Text(
                                            localizedString(Res.string.connection_failure),
                                            color = MaterialTheme.colorScheme.error,
                                        )
                                    }

                                    ConnectionTest.Idle -> {
                                        Unit
                                    }
                                }
                                OutlinedButton(onClick = {
                                    showError = !form.isValid
                                    if (form.isValid) test = ConnectionTest.Testing
                                }, enabled = test != ConnectionTest.Testing) {
                                    Text(
                                        localizedString(
                                            if (test ==
                                                ConnectionTest.Testing
                                            ) {
                                                Res.string.testing_connection
                                            } else {
                                                Res.string.test_connection
                                            },
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider()
                Row(
                    Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = back, enabled = step != SetupStep.Welcome) { Text(localizedString(Res.string.previous_step)) }
                    val finish = step == SetupStep.Cluster || (step == SetupStep.Mode && !cluster)
                    Button(onClick = {
                        if (finish) onComplete(if (cluster) form else null) else step = SetupStep.entries[step.ordinal + 1]
                    }, enabled = step != SetupStep.Cluster || test == ConnectionTest.Success) {
                        Text(localizedString(if (finish) Res.string.enter_home else Res.string.next_step))
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeFeatures() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(localizedString(Res.string.onboarding_intro), style = MaterialTheme.typography.bodyLarge)
        listOf(
            Res.string.feature_sessions to Res.string.feature_sessions_detail,
            Res.string.feature_agent to Res.string.feature_agent_detail,
            Res.string.feature_modes to Res.string.feature_modes_detail,
        ).forEach { (title, detail) ->
            Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(localizedString(title), style = MaterialTheme.typography.titleMedium)
                    Text(localizedString(detail), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun SetupChoice(
    label: StringResource,
    selected: Boolean,
    onClick: () -> Unit,
    detail: StringResource? = null,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            Modifier.fillMaxWidth().selectable(selected, role = Role.RadioButton, onClick = onClick).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RadioButton(selected, onClick = null)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(localizedString(label), style = MaterialTheme.typography.titleSmall)
                if (detail != null) Text(localizedString(detail), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ClusterConnectionForm(
    form: ClusterSetup,
    onChange: (ClusterSetup) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ClusterMethod.entries.forEach { method ->
                val label =
                    when (method) {
                        ClusterMethod.Lan -> Res.string.cluster_lan
                        ClusterMethod.Central -> Res.string.cluster_central
                        ClusterMethod.Cloud -> Res.string.cluster_cloud
                    }
                SetupChoice(label, method == form.method, { onChange(form.copy(method = method)) })
            }
        }
        if (form.method != ClusterMethod.Cloud) {
            if (form.method == ClusterMethod.Lan) Text(localizedString(Res.string.lan_hint), style = MaterialTheme.typography.bodySmall)
            SetupField(Res.string.ip_address, form.ip, { onChange(form.copy(ip = it)) })
            SetupField(Res.string.port, form.port, { onChange(form.copy(port = it)) })
        }
        if (form.method == ClusterMethod.Central) {
            Text(localizedString(Res.string.auth_method), style = MaterialTheme.typography.titleMedium)
            Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ClusterAuth.entries.forEach { auth ->
                    val label =
                        when (auth) {
                            ClusterAuth.None -> Res.string.auth_none
                            ClusterAuth.Password -> Res.string.auth_password
                            ClusterAuth.Certificate -> Res.string.auth_certificate
                            ClusterAuth.Mtls -> Res.string.auth_mtls
                        }
                    SetupChoice(label, form.auth == auth, { onChange(form.copy(auth = auth)) })
                }
            }
        }
        if (form.method == ClusterMethod.Cloud || (form.method == ClusterMethod.Central && form.auth == ClusterAuth.Password)) {
            SetupField(Res.string.account, form.account, { onChange(form.copy(account = it)) })
            SetupField(Res.string.password, form.password, { onChange(form.copy(password = it)) }, secret = true)
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
