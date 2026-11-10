package com.everfold.kotlin.feature.conversation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.everfold.kotlin.ui.components.AppIcon
import com.everfold.kotlin.ui.components.AppSymbol
import com.everfold.kotlin.ui.components.AppTooltip
import com.everfold.kotlin.ui.components.desktopUi
import com.everfold.kotlin.ui.localization.localizedString
import com.everfold.kotlin.ui.localization.resolve
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.copy_message
import everfold.shared.generated.resources.hide_sessions
import everfold.shared.generated.resources.logo
import everfold.shared.generated.resources.message_placeholder
import everfold.shared.generated.resources.new_session
import everfold.shared.generated.resources.open_workspace
import everfold.shared.generated.resources.send_message
import everfold.shared.generated.resources.send_shortcut
import everfold.shared.generated.resources.show_sessions
import everfold.shared.generated.resources.stop_reply
import everfold.shared.generated.resources.suggestion_ideas
import everfold.shared.generated.resources.suggestion_problem
import everfold.shared.generated.resources.suggestion_week
import everfold.shared.generated.resources.thinking
import everfold.shared.generated.resources.welcome_title
import everfold.shared.generated.resources.you
import org.jetbrains.compose.resources.painterResource

@Composable
fun ConversationScreen(
    state: ConversationUiState,
    compactNavigation: Boolean,
    onNewConversation: () -> Unit,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit,
    connectionLabel: String,
    onRetry: () -> Unit,
    onPreviewState: (AgentRunState) -> Unit,
    autoFocusInput: Boolean,
    sessionsVisible: Boolean,
    onToggleSessions: () -> Unit,
    modifier: Modifier = Modifier,
    showPreviewMenu: Boolean = true,
    inputFocusRequest: Int = 0,
) {
    val inputFocus = remember { FocusRequester() }
    LaunchedEffect(state.id, autoFocusInput, inputFocusRequest) {
        if (desktopUi && autoFocusInput) {
            withFrameNanos { }
            inputFocus.requestFocus()
        }
    }
    var scrollRequest by remember(state.id) { mutableIntStateOf(0) }
    val send = {
        scrollRequest++
        onSend()
    }
    Column(modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            run {
                val label =
                    localizedString(
                        if (compactNavigation) {
                            Res.string.open_workspace
                        } else if (sessionsVisible) {
                            Res.string.hide_sessions
                        } else {
                            Res.string.show_sessions
                        },
                    )
                AppTooltip(label) {
                    IconButton(
                        onClick = onToggleSessions,
                    ) { AppIcon(if (compactNavigation) AppSymbol.Menu else AppSymbol.Sidebar, description = label) }
                }
            }
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(state.title.resolve(), style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    connectionLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (compactNavigation) {
                val label = localizedString(Res.string.new_session)
                AppTooltip(label) {
                    IconButton(onClick = onNewConversation) { AppIcon(AppSymbol.Plus, description = label) }
                }
            }
            if (showPreviewMenu) key(state.id) { AgentPreviewMenu(onPreviewState) }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        key(state.id) { AgentStatusBar(state.runState, onRetry) }
        if (state.messages.isEmpty()) {
            WelcomeContent({
                onDraftChange(it)
                inputFocus.requestFocus()
            }, Modifier.weight(1f))
        } else {
            key(state.id) {
                ConversationMessages(
                    state,
                    scrollRequest,
                    onJumpToLatest = { if (desktopUi) inputFocus.requestFocus() },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(Modifier.widthIn(max = 840.dp).fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
                Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
                    Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.Bottom) {
                        TextField(
                            value = state.draft,
                            onValueChange = onDraftChange,
                            modifier =
                                Modifier.weight(1f).focusRequester(inputFocus).onPreviewKeyEvent {
                                    if (it.type == KeyEventType.KeyDown && it.key == Key.Enter && (it.isCtrlPressed || it.isMetaPressed)) {
                                        if (!state.isResponding && state.draft.isNotBlank()) send()
                                        true
                                    } else {
                                        false
                                    }
                                },
                            placeholder = { Text(localizedString(Res.string.message_placeholder)) },
                            maxLines = 5,
                            colors =
                                TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                ),
                        )
                        AppTooltip(
                            localizedString(
                                when {
                                    state.isResponding -> Res.string.stop_reply
                                    desktopUi -> Res.string.send_shortcut
                                    else -> Res.string.send_message
                                },
                            ),
                        ) {
                            FilledIconButton(
                                onClick = if (state.isResponding) onStop else send,
                                enabled = state.isResponding || state.draft.isNotBlank(),
                                modifier = Modifier.padding(bottom = 4.dp),
                            ) {
                                AppIcon(
                                    if (state.isResponding) AppSymbol.Stop else AppSymbol.Send,
                                    description =
                                        localizedString(
                                            if (state.isResponding) Res.string.stop_reply else Res.string.send_message,
                                        ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun MessageItem(
    message: ChatMessage,
    modifier: Modifier = Modifier,
    onPromptHeightChange: (Int) -> Unit = {},
) {
    val user = message.role == MessageRole.User
    Column(modifier, horizontalAlignment = if (user) Alignment.End else Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (!user) AppIcon(AppSymbol.Spark, modifier = Modifier.size(18.dp))
            Text(
                if (user) localizedString(Res.string.you) else "Everfold",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(10.dp))
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (user) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
        ) {
            if (user) {
                UserPrompt(message, onPromptHeightChange)
            } else {
                SelectionContainer {
                    if (message.text.isEmpty()) {
                        Text(
                            message.text.ifEmpty { localizedString(Res.string.thinking) },
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    } else {
                        MessageMarkdown(message.text)
                    }
                }
            }
        }
        CopyTextButton(message.text, localizedString(Res.string.copy_message))
    }
}

@Composable
private fun WelcomeContent(
    onSuggestion: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val suggestions =
        listOf(
            localizedString(Res.string.suggestion_ideas),
            localizedString(Res.string.suggestion_week),
            localizedString(Res.string.suggestion_problem),
        )
    LazyColumn(
        modifier.fillMaxWidth(),
        contentPadding = PaddingValues(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        item {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(75.dp),
                alpha = 0.75f,
            )
        }
        item {
            Text(
                localizedString(Res.string.welcome_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
            )
        }
        items(suggestions) { suggestion ->
            OutlinedButton(onClick = { onSuggestion(suggestion) }, modifier = Modifier.widthIn(max = 420.dp).fillMaxWidth()) {
                Text(suggestion)
            }
        }
    }
}
