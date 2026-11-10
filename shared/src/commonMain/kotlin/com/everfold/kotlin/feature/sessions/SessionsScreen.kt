package com.everfold.kotlin.feature.sessions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.everfold.kotlin.feature.conversation.ConversationUiState
import com.everfold.kotlin.ui.components.AppIcon
import com.everfold.kotlin.ui.components.AppSymbol
import com.everfold.kotlin.ui.components.AppTooltip
import com.everfold.kotlin.ui.components.desktopUi
import com.everfold.kotlin.ui.localization.localizedString
import com.everfold.kotlin.ui.localization.resolve
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.cancel
import everfold.shared.generated.resources.clear_search
import everfold.shared.generated.resources.delete_session
import everfold.shared.generated.resources.delete_session_notice
import everfold.shared.generated.resources.empty_sessions
import everfold.shared.generated.resources.nav_sessions
import everfold.shared.generated.resources.new_session
import everfold.shared.generated.resources.new_session_shortcut
import everfold.shared.generated.resources.no_sessions
import everfold.shared.generated.resources.pin_session
import everfold.shared.generated.resources.pinned_session
import everfold.shared.generated.resources.recent_sessions
import everfold.shared.generated.resources.rename_session
import everfold.shared.generated.resources.replying
import everfold.shared.generated.resources.save_session
import everfold.shared.generated.resources.search_sessions
import everfold.shared.generated.resources.session_actions
import everfold.shared.generated.resources.session_name
import everfold.shared.generated.resources.unpin_session

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SessionsScreen(
    sessions: List<ConversationUiState>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    onCreate: () -> Unit,
    onRename: (String, String) -> Unit,
    onDelete: (String) -> Unit,
    onTogglePin: (String) -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    searchRequest: Int = 0,
) {
    val searchFocus = remember { FocusRequester() }
    LaunchedEffect(searchRequest) {
        if (searchRequest > 0) {
            withFrameNanos { }
            searchFocus.requestFocus()
        }
    }
    var renaming by remember { mutableStateOf<String?>(null) }
    var deleting by remember { mutableStateOf<String?>(null) }
    var name by rememberSaveable { mutableStateOf("") }
    var menuId by remember { mutableStateOf<String?>(null) }
    val titles = sessions.map { it.title.resolve() }
    val filtered = sessions.filterIndexed { index, _ -> titles[index].contains(query, ignoreCase = true) }
    Column(modifier.padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                localizedString(Res.string.nav_sessions),
                Modifier.weight(1f),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
            AppTooltip(localizedString(if (desktopUi) Res.string.new_session_shortcut else Res.string.new_session)) {
                FilledTonalIconButton(onClick = onCreate) { AppIcon(AppSymbol.Plus, description = localizedString(Res.string.new_session)) }
            }
        }
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            placeholder = { Text(localizedString(Res.string.search_sessions)) },
            leadingIcon = { AppIcon(AppSymbol.Search) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    TextButton(
                        onClick = { onQueryChange("") },
                    ) { Text(localizedString(Res.string.clear_search)) }
                }
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().focusRequester(searchFocus),
        )
        Text(
            localizedString(Res.string.recent_sessions, filtered.size),
            Modifier.padding(top = 24.dp, bottom = 12.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
            items(filtered, key = { it.id }) { session ->
                Surface(
                    modifier =
                        Modifier
                            .combinedClickable(onClick = { onSelect(session.id) }, onLongClick = { menuId = session.id })
                            .pointerInput(session.id) {
                                awaitPointerEventScope {
                                    while (true) {
                                        val event = awaitPointerEvent(PointerEventPass.Initial)
                                        if (event.type == PointerEventType.Press && event.buttons.isSecondaryPressed) {
                                            menuId = session.id
                                            event.changes.forEach { it.consume() }
                                        }
                                    }
                                }
                            },
                    shape = RoundedCornerShape(16.dp),
                    color =
                        if (session.id ==
                            selectedId
                        ) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerLow
                        },
                ) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    session.title.resolve(),
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                if (session.isPinned) {
                                    Text(
                                        localizedString(Res.string.pinned_session),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                            Box {
                                IconButton(onClick = { menuId = session.id }) {
                                    AppIcon(AppSymbol.More, description = localizedString(Res.string.session_actions))
                                }
                                DropdownMenu(menuId == session.id, onDismissRequest = { menuId = null }) {
                                    DropdownMenuItem(text = { Text(localizedString(Res.string.rename_session)) }, onClick = {
                                        name = titles[sessions.indexOf(session)]
                                        renaming = session.id
                                        menuId = null
                                    })
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                localizedString(if (session.isPinned) Res.string.unpin_session else Res.string.pin_session),
                                            )
                                        },
                                        onClick = {
                                            onTogglePin(session.id)
                                            menuId = null
                                        },
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                localizedString(Res.string.delete_session),
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        },
                                        onClick = {
                                            deleting = session.id
                                            menuId = null
                                        },
                                    )
                                }
                            }
                        }
                        Text(
                            session.preview.resolve(),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            if (session.isResponding) localizedString(Res.string.replying) else session.updatedLabel.resolve(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
            if (filtered.isEmpty()) {
                item {
                    Text(
                        localizedString(if (query.isBlank()) Res.string.empty_sessions else Res.string.no_sessions),
                        Modifier.padding(vertical = 24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(onClick = { if (query.isBlank()) onCreate() else onQueryChange("") }) {
                        Text(localizedString(if (query.isBlank()) Res.string.new_session else Res.string.clear_search))
                    }
                }
            }
        }
    }
    renaming?.let { id ->
        AlertDialog(
            onDismissRequest = { renaming = null },
            title = { Text(localizedString(Res.string.rename_session)) },
            text = {
                OutlinedTextField(
                    name,
                    onValueChange = { name = it },
                    singleLine = true,
                    label = { Text(localizedString(Res.string.session_name)) },
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onRename(id, name)
                    renaming = null
                }, enabled = name.isNotBlank()) { Text(localizedString(Res.string.save_session)) }
            },
            dismissButton = { TextButton(onClick = { renaming = null }) { Text(localizedString(Res.string.cancel)) } },
        )
    }
    deleting?.let { id ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text(localizedString(Res.string.delete_session)) },
            text = { Text(localizedString(Res.string.delete_session_notice)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(id)
                    deleting = null
                }) { Text(localizedString(Res.string.delete_session), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text(localizedString(Res.string.cancel)) } },
        )
    }
}
