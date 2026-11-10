package com.everfold.kotlin.feature.conversation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.everfold.kotlin.ui.localization.localizedString
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.jump_to_latest
import everfold.shared.generated.resources.stopped_hint
import kotlinx.coroutines.launch

@Composable
internal fun ConversationMessages(
    state: ConversationUiState,
    scrollRequest: Int,
    onJumpToLatest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val list = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var followLatest by remember { mutableStateOf(true) }
    val atLatest by remember { derivedStateOf { list.firstVisibleItemIndex == 0 && list.firstVisibleItemScrollOffset == 0 } }
    // Only user scrolling changes follow mode. Layout changes while streaming must not opt the reader back in.
    LaunchedEffect(list) {
        snapshotFlow { if (list.isScrollInProgress) list.firstVisibleItemIndex == 0 && list.firstVisibleItemScrollOffset == 0 else null }
            .collect { atBottom -> if (atBottom != null) followLatest = atBottom }
    }
    LaunchedEffect(scrollRequest) {
        if (scrollRequest > 0) {
            followLatest = true
            list.scrollToItem(0)
        }
    }
    LaunchedEffect(state.messages.lastOrNull(), state.responseStopped) {
        if (followLatest && !list.isScrollInProgress) list.scrollToItem(0)
    }
    Box(modifier.fillMaxWidth()) {
        LazyColumn(
            state = list,
            reverseLayout = true,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            if (state.responseStopped) {
                item(key = "stopped") {
                    Text(localizedString(Res.string.stopped_hint), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            items(state.messages.asReversed(), key = { it.id }) { message ->
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                    MessageItem(message, Modifier.widthIn(max = 760.dp).fillMaxWidth())
                }
            }
        }
        ConversationScrollbar(list, Modifier.align(Alignment.CenterEnd).padding(vertical = 8.dp))
        if (!atLatest) {
            FilledTonalButton(
                onClick = {
                    scope.launch {
                        list.scrollToItem(0)
                        followLatest = true
                        onJumpToLatest()
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp),
            ) { Text(localizedString(Res.string.jump_to_latest)) }
        }
    }
}

@Composable
internal expect fun ConversationScrollbar(
    state: LazyListState,
    modifier: Modifier = Modifier,
)
