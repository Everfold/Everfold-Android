package com.everfold.kotlin.feature.sessions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.everfold.kotlin.feature.conversation.ConversationUiState
import com.everfold.kotlin.ui.components.AppIcon
import com.everfold.kotlin.ui.components.AppSymbol
import com.everfold.kotlin.ui.localization.localizedString
import com.everfold.kotlin.ui.localization.resolve
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.nav_sessions
import everfold.shared.generated.resources.new_session
import everfold.shared.generated.resources.no_sessions
import everfold.shared.generated.resources.recent_sessions
import everfold.shared.generated.resources.replying
import everfold.shared.generated.resources.search_sessions
import everfold.shared.generated.resources.sessions_subtitle

@Composable
fun SessionsScreen(
    sessions: List<ConversationUiState>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    onCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val titles = sessions.map { it.title.resolve() }
    val filtered = sessions.filterIndexed { index, _ -> titles[index].contains(query, ignoreCase = true) }
    Column(modifier.padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    localizedString(Res.string.nav_sessions),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    localizedString(Res.string.sessions_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            FilledTonalIconButton(onClick = onCreate) { AppIcon(AppSymbol.Plus, description = localizedString(Res.string.new_session)) }
        }
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            placeholder = { Text(localizedString(Res.string.search_sessions)) },
            leadingIcon = { AppIcon(AppSymbol.Search) },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
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
                    onClick = { onSelect(session.id) },
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
                        Text(
                            session.title.resolve(),
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
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
                        localizedString(Res.string.no_sessions),
                        Modifier.padding(vertical = 24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
