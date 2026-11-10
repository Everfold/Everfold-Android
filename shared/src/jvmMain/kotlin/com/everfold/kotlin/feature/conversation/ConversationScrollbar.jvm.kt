package com.everfold.kotlin.feature.conversation

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal actual fun ConversationScrollbar(
    state: LazyListState,
    modifier: Modifier,
) {
    if (state.canScrollBackward || state.canScrollForward) {
        VerticalScrollbar(rememberScrollbarAdapter(state), modifier.fillMaxHeight(), reverseLayout = true)
    }
}
