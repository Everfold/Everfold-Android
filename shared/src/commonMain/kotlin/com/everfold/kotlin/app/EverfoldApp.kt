package com.everfold.kotlin.app

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.everfold.kotlin.app.navigation.AppDestination
import com.everfold.kotlin.feature.connections.ConnectionMode
import com.everfold.kotlin.feature.connections.ConnectionsScreen
import com.everfold.kotlin.feature.conversation.AgentRunState
import com.everfold.kotlin.feature.conversation.ConversationScreen
import com.everfold.kotlin.feature.sessions.SessionsScreen
import com.everfold.kotlin.feature.settings.SettingsScreen
import com.everfold.kotlin.mock.MockWorkspace
import com.everfold.kotlin.ui.components.AppIcon
import com.everfold.kotlin.ui.components.desktopUi
import com.everfold.kotlin.ui.components.horizontalResizePointer
import com.everfold.kotlin.ui.localization.AppLanguage
import com.everfold.kotlin.ui.localization.localizedString
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.connected_preview
import everfold.shared.generated.resources.connection_local
import everfold.shared.generated.resources.new_session
import everfold.shared.generated.resources.not_connected
import everfold.shared.generated.resources.resize_sessions
import everfold.shared.generated.resources.sample_reply

/** UI composition root. Only this layer knows that the data source is a mock. */
@Composable
fun EverfoldApp(
    workspace: MockWorkspace,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier,
) {
    val reply = localizedString(Res.string.sample_reply)
    var destination by rememberSaveable { mutableStateOf(AppDestination.Sessions) }
    var selectedId by rememberSaveable { mutableStateOf("welcome") }
    var conversationOpen by rememberSaveable { mutableStateOf(false) }

    var focusComposer by rememberSaveable { mutableStateOf(true) }
    var sessionsVisible by rememberSaveable { mutableStateOf(true) }
    var sessionWidth by rememberSaveable { mutableFloatStateOf(300f) }
    var searchRequest by rememberSaveable { mutableIntStateOf(0) }
    Surface(
        modifier.fillMaxSize().onPreviewKeyEvent {
            if (it.type == KeyEventType.KeyDown && (it.isCtrlPressed || it.isMetaPressed)) {
                when (it.key) {
                    Key.N -> {
                        focusComposer = true
                        destination = AppDestination.Sessions
                        selectedId = workspace.createConversation()
                        conversationOpen =
                            true
                        true
                    }

                    Key.F -> {
                        focusComposer = false
                        destination = AppDestination.Sessions
                        sessionsVisible = true
                        conversationOpen = false
                        searchRequest++
                        true
                    }

                    Key.B -> {
                        focusComposer = true
                        sessionsVisible = !sessionsVisible
                        true
                    }

                    else -> {
                        false
                    }
                }
            } else {
                false
            }
        },
    ) {
        BoxWithConstraints(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).imePadding()) {
            val wide = maxWidth >= 900.dp
            val rail = maxWidth >= 600.dp
            val maxSessionWidth = (maxWidth.value - 520f).coerceIn(260f, 440f)
            val density = LocalDensity.current.density
            val resizeLabel = localizedString(Res.string.resize_sessions)
            val selected = workspace.conversations.firstOrNull { it.id == selectedId } ?: workspace.conversations.firstOrNull()
            val showingConversation = destination == AppDestination.Sessions && (wide || conversationOpen)
            NavigationBackHandler(
                state = rememberNavigationEventState(NavigationEventInfo.None),
                isBackEnabled = destination != AppDestination.Sessions || (!wide && conversationOpen),
                onBackCompleted = {
                    if (destination != AppDestination.Sessions) {
                        destination = AppDestination.Sessions
                    } else {
                        conversationOpen = false
                    }
                },
            )
            Row(Modifier.fillMaxSize()) {
                if (rail) {
                    NavigationRail(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
                        AppDestination.entries.forEach { item ->
                            NavigationRailItem(
                                selected = destination == item,
                                onClick = { destination = item },
                                icon = { AppIcon(item.icon) },
                                label = { Text(localizedString(item.label)) },
                                modifier = Modifier.padding(bottom = 12.dp),
                            )
                        }
                    }
                }
                Column(Modifier.weight(1f)) {
                    Box(Modifier.weight(1f)) {
                        when (destination) {
                            AppDestination.Sessions -> {
                                Row(Modifier.fillMaxSize()) {
                                    if ((wide && sessionsVisible) || (!wide && !conversationOpen)) {
                                        Surface(
                                            modifier =
                                                if (wide) {
                                                    Modifier
                                                        .width(
                                                            sessionWidth.coerceIn(260f, maxSessionWidth).dp,
                                                        ).fillMaxHeight()
                                                } else {
                                                    Modifier.fillMaxSize()
                                                },
                                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                                        ) {
                                            SessionsScreen(
                                                sessions = workspace.conversations,
                                                searchRequest = searchRequest,
                                                selectedId = if (wide) selected?.id else null,
                                                onSelect = {
                                                    focusComposer = true
                                                    selectedId = it
                                                    conversationOpen = true
                                                },
                                                onRename = workspace::renameConversation,
                                                onDelete = { id ->
                                                    workspace.deleteConversation(id)
                                                    if (selectedId == id) {
                                                        selectedId =
                                                            workspace.conversations
                                                                .firstOrNull()
                                                                ?.id
                                                                .orEmpty()
                                                        conversationOpen = false
                                                    }
                                                },
                                                onTogglePin = workspace::togglePinned,
                                                onCreate = {
                                                    focusComposer = true
                                                    selectedId = workspace.createConversation()
                                                    conversationOpen = true
                                                },
                                            )
                                        }
                                    }
                                    if (wide && sessionsVisible) {
                                        Box(
                                            Modifier
                                                .width(if (desktopUi) 10.dp else 1.dp)
                                                .fillMaxHeight()
                                                .horizontalResizePointer()
                                                .draggable(
                                                    rememberDraggableState { delta ->
                                                        sessionWidth =
                                                            (
                                                                sessionWidth.coerceAtMost(
                                                                    maxSessionWidth,
                                                                ) + delta / density
                                                            ).coerceIn(260f, maxSessionWidth)
                                                    },
                                                    Orientation.Horizontal,
                                                ).semantics { contentDescription = resizeLabel }
                                                .onKeyEvent { event ->
                                                    if (event.type == KeyEventType.KeyDown &&
                                                        (event.key == Key.DirectionLeft || event.key == Key.DirectionRight)
                                                    ) {
                                                        sessionWidth =
                                                            (
                                                                sessionWidth +
                                                                    if (event.key ==
                                                                        Key.DirectionLeft
                                                                    ) {
                                                                        -16f
                                                                    } else {
                                                                        16f
                                                                    }
                                                            ).coerceIn(260f, maxSessionWidth)
                                                        true
                                                    } else {
                                                        false
                                                    }
                                                }.focusable(),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                        }
                                    }
                                    if (showingConversation && selected != null) {
                                        ConversationScreen(
                                            state = selected,
                                            showBack = !wide,
                                            autoFocusInput = focusComposer,
                                            showSessionToggle = wide,
                                            sessionsVisible = sessionsVisible,
                                            onToggleSessions = {
                                                focusComposer = true
                                                sessionsVisible = !sessionsVisible
                                            },
                                            onBack = { conversationOpen = false },
                                            onDraftChange = { workspace.updateDraft(selected.id, it) },
                                            onSend = { workspace.send(selected.id, reply) },
                                            onStop = { workspace.stop(selected.id) },
                                            connectionLabel =
                                                localizedString(
                                                    when {
                                                        workspace.connectionMode == ConnectionMode.Local -> Res.string.connection_local
                                                        workspace.connectionPreview -> Res.string.connected_preview
                                                        else -> Res.string.not_connected
                                                    },
                                                ),
                                            onRetry = { workspace.retry(selected.id, reply) },
                                            onPreviewState = {
                                                if (it == AgentRunState.Running) {
                                                    workspace.retry(selected.id, reply)
                                                } else {
                                                    workspace.previewRunState(selected.id, it)
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                    if (wide && selected == null) {
                                        Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                                            Button(onClick = {
                                                selectedId = workspace.createConversation()
                                                conversationOpen = true
                                            }) {
                                                Text(localizedString(Res.string.new_session))
                                            }
                                        }
                                    }
                                }
                            }

                            AppDestination.Connections -> {
                                ConnectionsScreen(
                                    workspace.connectionMode,
                                    workspace.clusterSetup,
                                    workspace.connectionPreview,
                                    workspace::changeConnectionMode,
                                    workspace::updateClusterSetup,
                                    workspace::updateConnectionPreview,
                                )
                            }

                            AppDestination.Settings -> {
                                SettingsScreen(
                                    language = language,
                                    onLanguageChange = onLanguageChange,
                                    themeMode = workspace.themeMode,
                                    themeColor = workspace.themeColor,
                                    onThemeChange = workspace::setTheme,
                                    onSystemColorChange = workspace::useSystemThemeColor,
                                    onPresetChange = workspace::selectThemePreset,
                                    onCustomColorChange = workspace::applyCustomThemeColor,
                                )
                            }
                        }
                    }
                    if (!rail && !showingConversation) {
                        NavigationBar {
                            AppDestination.entries.forEach { item ->
                                NavigationBarItem(
                                    selected = destination == item,
                                    onClick = { destination = item },
                                    icon = { AppIcon(item.icon) },
                                    label = { Text(localizedString(item.label)) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
