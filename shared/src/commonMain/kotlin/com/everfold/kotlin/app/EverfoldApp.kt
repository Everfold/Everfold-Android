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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
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
import androidx.compose.ui.platform.LocalFocusManager
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
import everfold.shared.generated.resources.not_connected
import everfold.shared.generated.resources.resize_sessions
import everfold.shared.generated.resources.sample_reply
import kotlinx.coroutines.launch

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
    var showArchived by rememberSaveable { mutableStateOf(false) }
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val conversationStates = rememberSaveableStateHolder()
    val sessionListState = rememberSaveableStateHolder()

    var focusComposer by rememberSaveable { mutableStateOf(true) }
    var sessionsVisible by rememberSaveable { mutableStateOf(true) }
    var sessionWidth by rememberSaveable { mutableFloatStateOf(300f) }
    var sessionQuery by rememberSaveable { mutableStateOf("") }
    var searchRequest by rememberSaveable { mutableIntStateOf(0) }
    var composerFocusRequest by rememberSaveable { mutableIntStateOf(0) }
    Surface(modifier.fillMaxSize()) {
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .focusable()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .imePadding(),
        ) {
            val wide = maxWidth >= 900.dp
            val drawerState = remember(wide) { DrawerState(DrawerValue.Closed) }
            val closeDrawer: () -> Unit = {
                focusManager.clearFocus()
                scope.launch { drawerState.close() }
            }
            val openDrawer: () -> Unit = {
                focusManager.clearFocus()
                scope.launch { drawerState.open() }
            }
            val openNewConversation: () -> Unit = {
                focusComposer = true
                destination = AppDestination.Sessions
                sessionQuery = ""
                showArchived = false
                selectedId = null
                closeDrawer()
                composerFocusRequest++
            }

            val maxSessionWidth = (maxWidth.value - 520f).coerceIn(260f, 440f)
            val density = LocalDensity.current.density
            val resizeLabel = localizedString(Res.string.resize_sessions)
            val selected = workspace.conversations.firstOrNull { it.id == selectedId }
            val sessions: @Composable (Modifier) -> Unit = { listModifier ->
                sessionListState.SaveableStateProvider("sessions") {
                    SessionsScreen(
                        sessions = workspace.conversations,
                        selectedId = selected?.id,
                        showArchived = showArchived,
                        onShowArchivedChange = { showArchived = it },
                        onToggleArchive = workspace::toggleArchived,
                        searchRequest = searchRequest,
                        query = sessionQuery,
                        onQueryChange = { sessionQuery = it },
                        onSelect = {
                            focusComposer = true
                            selectedId = it
                            destination = AppDestination.Sessions
                            closeDrawer()
                        },
                        onRename = workspace::renameConversation,
                        onDelete = { id ->
                            workspace.deleteConversation(id)
                            conversationStates.removeState(id)
                            if (selectedId == id) selectedId = null
                        },
                        onTogglePin = workspace::togglePinned,
                        onCreate = openNewConversation,
                        modifier = listModifier,
                    )
                }
            }
            NavigationBackHandler(
                state = rememberNavigationEventState(NavigationEventInfo.None),
                isBackEnabled = (!wide && drawerState.isOpen) || destination != AppDestination.Sessions,
                onBackCompleted = {
                    if (!wide && drawerState.isOpen) {
                        closeDrawer()
                    } else {
                        destination = AppDestination.Sessions
                    }
                },
            )
            ModalNavigationDrawer(
                modifier =
                    Modifier.fillMaxSize().onPreviewKeyEvent {
                        if (it.type == KeyEventType.KeyDown && it.key == Key.Escape &&
                            ((!wide && drawerState.isOpen) || destination != AppDestination.Sessions)
                        ) {
                            if (!wide && drawerState.isOpen) closeDrawer() else destination = AppDestination.Sessions
                            true
                        } else if (it.type == KeyEventType.KeyDown && (it.isCtrlPressed || it.isMetaPressed)) {
                            when (it.key) {
                                Key.N -> {
                                    openNewConversation()
                                    true
                                }

                                Key.F -> {
                                    focusComposer = false
                                    destination = AppDestination.Sessions
                                    sessionsVisible = true
                                    focusManager.clearFocus()
                                    scope.launch {
                                        if (!wide) drawerState.open()
                                        searchRequest++
                                    }
                                    true
                                }

                                Key.B -> {
                                    focusComposer = true
                                    if (wide) {
                                        sessionsVisible = !sessionsVisible
                                    } else if (drawerState.isOpen) {
                                        closeDrawer()
                                    } else {
                                        openDrawer()
                                    }
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
                drawerState = drawerState,
                gesturesEnabled = !wide && drawerState.isOpen,
                drawerContent = {
                    if (!wide) {
                        ModalDrawerSheet(Modifier.widthIn(max = 360.dp).fillMaxHeight()) {
                            sessions(Modifier.weight(1f))
                            HorizontalDivider()
                            listOf(AppDestination.Connections, AppDestination.Settings).forEach { item ->
                                NavigationDrawerItem(
                                    label = { Text(localizedString(item.label)) },
                                    icon = { AppIcon(item.icon) },
                                    selected = destination == item,
                                    onClick = {
                                        destination = item
                                        closeDrawer()
                                    },
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                )
                            }
                        }
                    }
                },
            ) {
                Row(Modifier.fillMaxSize()) {
                    if (wide) {
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
                                        if (wide && sessionsVisible) {
                                            Surface(
                                                modifier = Modifier.width(sessionWidth.coerceIn(260f, maxSessionWidth).dp).fillMaxHeight(),
                                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                            ) { sessions(Modifier.fillMaxSize()) }
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
                                        conversationStates.SaveableStateProvider(selected?.id ?: workspace.newConversation.id) {
                                            ConversationScreen(
                                                state = selected ?: workspace.newConversation,
                                                showPreviewMenu = selected != null,
                                                inputFocusRequest = composerFocusRequest,
                                                compactNavigation = !wide,
                                                autoFocusInput = focusComposer,
                                                sessionsVisible = sessionsVisible,
                                                onToggleSessions = {
                                                    focusComposer = true
                                                    if (wide) sessionsVisible = !sessionsVisible else openDrawer()
                                                },
                                                onNewConversation = openNewConversation,
                                                onDraftChange = {
                                                    if (selected ==
                                                        null
                                                    ) {
                                                        workspace.updateNewConversationDraft(it)
                                                    } else {
                                                        workspace.updateDraft(selected.id, it)
                                                    }
                                                },
                                                onSend = {
                                                    if (selected == null) {
                                                        workspace.submitNewConversation(reply)?.let {
                                                            selectedId = it
                                                            showArchived = false
                                                            sessionQuery = ""
                                                            focusComposer = true
                                                        }
                                                    } else {
                                                        workspace.send(selected.id, reply)
                                                    }
                                                },
                                                onStop = { selected?.let { workspace.stop(it.id) } },
                                                connectionLabel =
                                                    localizedString(
                                                        when {
                                                            workspace.connectionMode == ConnectionMode.Local -> Res.string.connection_local
                                                            workspace.connectionPreview -> Res.string.connected_preview
                                                            else -> Res.string.not_connected
                                                        },
                                                    ),
                                                onRetry = { selected?.let { workspace.retry(it.id, reply) } },
                                                onPreviewState = {
                                                    if (selected != null && it == AgentRunState.Running) {
                                                        workspace.retry(selected.id, reply)
                                                    } else if (selected != null) {
                                                        workspace.previewRunState(selected.id, it)
                                                    }
                                                },
                                                modifier = Modifier.weight(1f),
                                            )
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
                                        onBack = if (wide) null else ({ destination = AppDestination.Sessions }),
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
                                        onBack = if (wide) null else ({ destination = AppDestination.Sessions }),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
