package com.everfold.kotlin.feature.onboarding

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.everfold.kotlin.feature.connections.ClusterConnectionForm
import com.everfold.kotlin.feature.connections.ClusterSetup
import com.everfold.kotlin.feature.connections.ConnectionPreviewOptions
import com.everfold.kotlin.feature.connections.ConnectionPreviewResult
import com.everfold.kotlin.feature.connections.description
import com.everfold.kotlin.feature.settings.AppearanceSettings
import com.everfold.kotlin.feature.settings.LanguageSettings
import com.everfold.kotlin.feature.settings.ThemeColorSettings
import com.everfold.kotlin.mock.MockWorkspace
import com.everfold.kotlin.ui.localization.AppLanguage
import com.everfold.kotlin.ui.localization.localizedString
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.connection_preview_notice
import everfold.shared.generated.resources.connection_success
import everfold.shared.generated.resources.enter_home
import everfold.shared.generated.resources.next_step
import everfold.shared.generated.resources.onboarding_welcome
import everfold.shared.generated.resources.previous_step
import everfold.shared.generated.resources.retry_connection
import everfold.shared.generated.resources.setup_cluster
import everfold.shared.generated.resources.setup_mode
import everfold.shared.generated.resources.setup_nav_preferences
import everfold.shared.generated.resources.setup_nav_welcome
import everfold.shared.generated.resources.setup_preferences
import everfold.shared.generated.resources.setup_shortcuts
import everfold.shared.generated.resources.setup_sidebar_hint
import everfold.shared.generated.resources.setup_step
import everfold.shared.generated.resources.test_connection
import everfold.shared.generated.resources.testing_connection
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
    var furthestStep by remember { mutableIntStateOf(0) }
    val focusRequester = remember { FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }
    var cluster by remember { mutableStateOf(false) }
    var form by remember { mutableStateOf(ClusterSetup()) }
    var test by remember { mutableStateOf(ConnectionTest.Idle) }
    var previewResult by remember { mutableStateOf(ConnectionPreviewResult.Success) }
    var showError by remember { mutableStateOf(false) }
    LaunchedEffect(test, form, previewResult, step) {
        if (test == ConnectionTest.Testing) {
            delay(900)
            test = if (previewResult == ConnectionPreviewResult.Success) ConnectionTest.Success else ConnectionTest.Failure
        }
    }
    val navigate: (SetupStep) -> Unit = {
        if (test == ConnectionTest.Testing) test = ConnectionTest.Idle
        step = it
        furthestStep = maxOf(furthestStep, it.ordinal)
    }
    val back = { navigate(SetupStep.entries[(step.ordinal - 1).coerceAtLeast(0)]) }
    val finish = step == SetupStep.Cluster || (step == SetupStep.Mode && !cluster)
    val advance = {
        when {
            step == SetupStep.Cluster && test != ConnectionTest.Success -> {
                showError = !form.isValid
                if (form.isValid && test != ConnectionTest.Testing) test = ConnectionTest.Testing
            }

            finish -> {
                onComplete(if (cluster) form else null)
            }

            else -> {
                navigate(SetupStep.entries[step.ordinal + 1])
            }
        }
    }
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = step != SetupStep.Welcome,
        onBackCompleted = back,
    )
    Surface(
        modifier
            .fillMaxSize()
            .onPreviewKeyEvent {
                if (it.type != KeyEventType.KeyDown) {
                    false
                } else if ((it.isCtrlPressed || it.isMetaPressed) && it.key == Key.Enter) {
                    if (test != ConnectionTest.Testing) advance()
                    true
                } else if (it.isAltPressed && it.key == Key.DirectionLeft && step != SetupStep.Welcome) {
                    back()
                    true
                } else {
                    false
                }
            }.onFocusChanged { hasFocus = it.hasFocus }
            .focusRequester(focusRequester)
            .focusable(),
    ) {
        BoxWithConstraints(
            Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).imePadding(),
        ) {
            val wide = maxWidth >= 1000.dp
            val contentWidth = maxWidth - (if (wide) 280.dp + 64.dp else 48.dp) - 12.dp
            val preferenceColumns =
                when {
                    contentWidth >= 1200.dp -> 3
                    contentWidth >= 720.dp -> 2
                    else -> 1
                }
            LaunchedEffect(wide) {
                withFrameNanos { }
                if (!hasFocus) focusRequester.requestFocus()
            }
            Row(Modifier.fillMaxSize()) {
                if (wide) {
                    SetupSidebar(step, furthestStep, cluster, navigate)
                }
                Column(Modifier.weight(1f).fillMaxHeight().padding(horizontal = if (wide) 32.dp else 24.dp, vertical = 20.dp)) {
                    if (!wide) Text("Everfold", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Text(localizedString(Res.string.setup_step, step.ordinal + 1, if (cluster) 4 else 3), Modifier.padding(vertical = 8.dp))
                    if (!wide) {
                        LinearProgressIndicator(
                            progress = { (step.ordinal + 1) / (if (cluster) 4f else 3f) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    key(step) {
                        OnboardingScrollArea(Modifier.weight(1f, fill = !wide)) {
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
                                    WelcomeFeatures(contentWidth >= 840.dp)
                                }

                                SetupStep.Preferences -> {
                                    OnboardingPreferences(
                                        workspace,
                                        language,
                                        onLanguageChange,
                                        preferenceColumns,
                                        appearanceHorizontal =
                                            (contentWidth - 24.dp * (preferenceColumns - 1)) / preferenceColumns >=
                                                (380 * LocalDensity.current.fontScale).dp,
                                    )
                                }

                                SetupStep.Mode -> {
                                    ModeSelection(cluster, { cluster = it }, contentWidth >= 600.dp)
                                }

                                SetupStep.Cluster -> {
                                    ClusterConnectionForm(form, wide = contentWidth >= 600.dp, showErrors = showError, onChange = {
                                        if (form.method != it.method || form.auth != it.auth) showError = false
                                        form = it
                                        test = ConnectionTest.Idle
                                    })
                                    Text(localizedString(Res.string.connection_preview_notice), style = MaterialTheme.typography.bodySmall)
                                    ConnectionPreviewOptions(previewResult, onSelect = {
                                        previewResult = it
                                        test = ConnectionTest.Idle
                                    })
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
                                                localizedString(previewResult.description),
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }

                                        ConnectionTest.Idle -> {
                                            Unit
                                        }
                                    }
                                }
                            }
                        }
                    }
                    HorizontalDivider()
                    Row(
                        Modifier.fillMaxWidth().padding(top = 12.dp),
                        horizontalArrangement = if (wide) Arrangement.spacedBy(12.dp, Alignment.End) else Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(onClick = back, enabled = step != SetupStep.Welcome) { Text(localizedString(Res.string.previous_step)) }
                        val actionLabel =
                            when {
                                step == SetupStep.Cluster && test == ConnectionTest.Testing -> Res.string.testing_connection
                                step == SetupStep.Cluster && test == ConnectionTest.Failure -> Res.string.retry_connection
                                step == SetupStep.Cluster && test != ConnectionTest.Success -> Res.string.test_connection
                                finish -> Res.string.enter_home
                                else -> Res.string.next_step
                            }
                        Button(onClick = advance, enabled = test != ConnectionTest.Testing) {
                            Text(localizedString(actionLabel))
                        }
                    }
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
private fun SetupSidebar(
    step: SetupStep,
    furthestStep: Int,
    cluster: Boolean,
    onNavigate: (SetupStep) -> Unit,
) {
    PermanentDrawerSheet(
        Modifier.width(280.dp).fillMaxHeight(),
        drawerShape = RectangleShape,
        drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        windowInsets = WindowInsets(0),
    ) {
        Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Everfold", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Text(localizedString(Res.string.setup_sidebar_hint), style = MaterialTheme.typography.bodyMedium)
            SetupStep.entries.take(if (cluster) 4 else 3).forEach { destination ->
                val label =
                    when (destination) {
                        SetupStep.Welcome -> Res.string.setup_nav_welcome
                        SetupStep.Preferences -> Res.string.setup_nav_preferences
                        SetupStep.Mode -> Res.string.setup_mode
                        SetupStep.Cluster -> Res.string.setup_cluster
                    }
                val enabled = destination.ordinal <= furthestStep
                NavigationDrawerItem(
                    label = { Text(localizedString(label)) },
                    selected = destination == step,
                    onClick = { if (enabled) onNavigate(destination) },
                    modifier =
                        Modifier.alpha(if (enabled) 1f else 0.38f).focusProperties { canFocus = enabled }.semantics {
                            if (!enabled) disabled()
                        },
                    icon = { Text((destination.ordinal + 1).toString()) },
                )
            }
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            Text(
                localizedString(Res.string.setup_shortcuts),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun OnboardingPreferences(
    workspace: MockWorkspace,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    columns: Int,
    appearanceHorizontal: Boolean,
) {
    FlowRow(
        Modifier.fillMaxWidth(),
        maxItemsInEachRow = columns,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            LanguageSettings(language, onLanguageChange)
            if (columns == 2) OnboardingAppearance(workspace, appearanceHorizontal)
        }
        if (columns != 2) {
            Column(Modifier.weight(1f)) {
                OnboardingAppearance(workspace, appearanceHorizontal)
            }
        }
        Column(Modifier.weight(1f)) {
            ThemeColorSettings(
                workspace.themeColor,
                workspace::useSystemThemeColor,
                workspace::selectThemePreset,
                workspace::applyCustomThemeColor,
            )
        }
    }
}

@Composable
private fun OnboardingAppearance(
    workspace: MockWorkspace,
    horizontal: Boolean,
) {
    AppearanceSettings(workspace.themeMode, workspace::setTheme, horizontal)
}
