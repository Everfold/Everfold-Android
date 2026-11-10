package com.everfold.kotlin.feature.conversation

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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.everfold.kotlin.ui.components.AppIcon
import com.everfold.kotlin.ui.components.AppSymbol
import com.everfold.kotlin.ui.localization.localizedString
import com.everfold.kotlin.ui.localization.resolve
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.back_sessions
import everfold.shared.generated.resources.conversation_notice
import everfold.shared.generated.resources.conversation_space
import everfold.shared.generated.resources.generating_reply
import everfold.shared.generated.resources.message_placeholder
import everfold.shared.generated.resources.preview_badge
import everfold.shared.generated.resources.send_message
import everfold.shared.generated.resources.stop_reply
import everfold.shared.generated.resources.stopped_hint
import everfold.shared.generated.resources.suggestion_ideas
import everfold.shared.generated.resources.suggestion_problem
import everfold.shared.generated.resources.suggestion_week
import everfold.shared.generated.resources.thinking
import everfold.shared.generated.resources.welcome_subtitle
import everfold.shared.generated.resources.welcome_title
import everfold.shared.generated.resources.you

@Composable
fun ConversationScreen(
    state: ConversationUiState,
    showBack: Boolean,
    onBack: () -> Unit,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            if (showBack) IconButton(onClick = onBack) { AppIcon(AppSymbol.Back, description = localizedString(Res.string.back_sessions)) }
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(state.title.resolve(), style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    if (state.isResponding) {
                        localizedString(
                            Res.string.generating_reply,
                        )
                    } else {
                        localizedString(Res.string.conversation_space)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Surface(color = MaterialTheme.colorScheme.surfaceContainerHigh, shape = RoundedCornerShape(8.dp)) {
                Text(
                    localizedString(Res.string.preview_badge),
                    Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        if (state.messages.isEmpty()) {
            WelcomeContent(onDraftChange, Modifier.weight(1f))
        } else {
            key(state.id) {
                val listState = rememberLazyListState()
                // Reversed layout anchors the latest message at the bottom without scrolling on every token.
                LaunchedEffect(state.messages.size, state.responseStopped) {
                    listState.scrollToItem(0)
                }
                LazyColumn(
                    state = listState,
                    reverseLayout = true,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    if (state.responseStopped) {
                        item {
                            Text(
                                localizedString(Res.string.stopped_hint),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                    items(state.messages.asReversed(), key = { it.id }) { message ->
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                            MessageItem(message, Modifier.widthIn(max = 760.dp).fillMaxWidth())
                        }
                    }
                }
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
                                Modifier.weight(1f).onPreviewKeyEvent {
                                    if (it.type == KeyEventType.KeyDown && it.key == Key.Enter && (it.isCtrlPressed || it.isMetaPressed)) {
                                        if (!state.isResponding && state.draft.isNotBlank()) onSend()
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
                        FilledIconButton(
                            onClick = if (state.isResponding) onStop else onSend,
                            enabled = state.isResponding || state.draft.isNotBlank(),
                            modifier = Modifier.padding(bottom = 4.dp),
                        ) {
                            AppIcon(
                                if (state.isResponding) AppSymbol.Stop else AppSymbol.Send,
                                description =
                                    if (state.isResponding) {
                                        localizedString(
                                            Res.string.stop_reply,
                                        )
                                    } else {
                                        localizedString(Res.string.send_message)
                                    },
                            )
                        }
                    }
                }
                Text(
                    localizedString(Res.string.conversation_notice),
                    Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun MessageItem(
    message: ChatMessage,
    modifier: Modifier = Modifier,
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
            SelectionContainer {
                Text(
                    message.text.ifEmpty { localizedString(Res.string.thinking) },
                    Modifier.padding(if (user) 16.dp else 0.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { Spacer(Modifier.height(24.dp)) }
        item { AppIcon(AppSymbol.Spark, modifier = Modifier.size(40.dp)) }
        item {
            Text(
                localizedString(Res.string.welcome_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium,
            )
        }
        item { Text(localizedString(Res.string.welcome_subtitle), color = MaterialTheme.colorScheme.onSurfaceVariant) }
        items(suggestions) { suggestion ->
            OutlinedButton(onClick = { onSuggestion(suggestion) }, modifier = Modifier.widthIn(max = 420.dp).fillMaxWidth()) {
                Text(suggestion)
            }
        }
    }
}
