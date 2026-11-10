package com.everfold.kotlin.app

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.everfold.kotlin.app.navigation.AppDestination
import com.everfold.kotlin.feature.connections.ConnectionsScreen
import com.everfold.kotlin.feature.conversation.ConversationScreen
import com.everfold.kotlin.feature.sessions.SessionsScreen
import com.everfold.kotlin.feature.settings.SettingsScreen
import com.everfold.kotlin.mock.MockWorkspace
import com.everfold.kotlin.ui.components.AppIcon
import com.everfold.kotlin.ui.localization.AppLanguage
import com.everfold.kotlin.ui.localization.localizedString
import everfold.shared.generated.resources.Res
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

    Surface(modifier.fillMaxSize()) {
        BoxWithConstraints(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).imePadding()) {
            val wide = maxWidth >= 900.dp
            val rail = maxWidth >= 600.dp
            val selected = workspace.conversations.firstOrNull { it.id == selectedId } ?: workspace.conversations.first()
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
                        Text(
                            "E",
                            Modifier.padding(top = 20.dp, bottom = 32.dp),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
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
                                    if (wide || !conversationOpen) {
                                        Surface(
                                            modifier = if (wide) Modifier.width(300.dp).fillMaxHeight() else Modifier.fillMaxSize(),
                                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                                        ) {
                                            SessionsScreen(
                                                sessions = workspace.conversations,
                                                selectedId = if (wide) selected.id else null,
                                                onSelect = {
                                                    selectedId = it
                                                    conversationOpen = true
                                                },
                                                onCreate = {
                                                    selectedId = workspace.createConversation()
                                                    conversationOpen = true
                                                },
                                            )
                                        }
                                    }
                                    if (wide) VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    if (showingConversation) {
                                        ConversationScreen(
                                            state = selected,
                                            showBack = !wide,
                                            onBack = { conversationOpen = false },
                                            onDraftChange = { workspace.updateDraft(selected.id, it) },
                                            onSend = { workspace.send(selected.id, reply) },
                                            onStop = { workspace.stop(selected.id) },
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                }
                            }

                            AppDestination.Connections -> {
                                ConnectionsScreen(
                                    workspace.connectionMode,
                                    workspace.remoteAddress,
                                    workspace.connectionPreview,
                                    workspace::changeConnectionMode,
                                    workspace::updateRemoteAddress,
                                    workspace::toggleConnectionPreview,
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
