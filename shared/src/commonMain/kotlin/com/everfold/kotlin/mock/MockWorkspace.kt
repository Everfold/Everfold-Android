package com.everfold.kotlin.mock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everfold.kotlin.feature.connections.ClusterMethod
import com.everfold.kotlin.feature.connections.ClusterSetup
import com.everfold.kotlin.feature.connections.ConnectionMode
import com.everfold.kotlin.feature.conversation.AgentRunState
import com.everfold.kotlin.feature.conversation.ChatMessage
import com.everfold.kotlin.feature.conversation.ConversationUiState
import com.everfold.kotlin.feature.conversation.MessageRole
import com.everfold.kotlin.ui.localization.UiText
import com.everfold.kotlin.ui.theme.ThemeColorMode
import com.everfold.kotlin.ui.theme.ThemeColorSelection
import com.everfold.kotlin.ui.theme.ThemeMode
import com.everfold.kotlin.ui.theme.ThemePreset
import everfold.shared.generated.resources.Res
import everfold.shared.generated.resources.just_now
import everfold.shared.generated.resources.new_conversation
import everfold.shared.generated.resources.new_conversation_hint
import everfold.shared.generated.resources.reply_complete
import everfold.shared.generated.resources.reply_stopped
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** In-memory UI fixtures. This class never creates an Agent or opens a connection. */
class MockWorkspace : ViewModel() {
    var initialized by mutableStateOf(false)
        private set
    var clusterSetup by mutableStateOf(ClusterSetup())
        private set

    fun completeSetup(setup: ClusterSetup?) {
        clusterSetup = setup ?: ClusterSetup()
        connectionMode = if (setup == null) ConnectionMode.Local else ConnectionMode.Remote
        connectionPreview = setup != null
        initialized = true
    }

    var conversations by mutableStateOf(sampleConversations())
        private set
    var themeMode by mutableStateOf(ThemeMode.System)
        private set
    var themeColor by mutableStateOf(ThemeColorSelection())
        private set
    var connectionMode by mutableStateOf(ConnectionMode.Local)
        private set
    var connectionPreview by mutableStateOf(false)
        private set

    private var nextId = 0
    private val replies = mutableMapOf<String, Job>()

    fun createConversation(): String {
        val id = "new-${++nextId}"
        conversations =
            listOf(
                ConversationUiState(
                    id,
                    UiText.Resource(Res.string.new_conversation),
                    UiText.Resource(Res.string.new_conversation_hint),
                    UiText.Resource(Res.string.just_now),
                ),
            ) +
            conversations
        return id
    }

    fun updateDraft(
        id: String,
        value: String,
    ) = update(id) { it.copy(draft = value) }

    fun setTheme(mode: ThemeMode) {
        themeMode = mode
    }

    fun useSystemThemeColor() {
        themeColor = themeColor.copy(mode = ThemeColorMode.System)
    }

    fun selectThemePreset(preset: ThemePreset) {
        themeColor = themeColor.copy(mode = ThemeColorMode.Preset, preset = preset)
    }

    fun applyCustomThemeColor(color: Color) {
        themeColor = themeColor.copy(mode = ThemeColorMode.Custom, customSeed = color)
    }

    fun changeConnectionMode(mode: ConnectionMode) {
        connectionMode = mode
        connectionPreview = false
    }

    fun updateClusterSetup(value: ClusterSetup) {
        clusterSetup = value
        connectionPreview = false
    }

    fun updateConnectionPreview(connected: Boolean) {
        connectionPreview = connected && connectionMode == ConnectionMode.Remote
    }

    fun send(
        id: String,
        reply: String,
    ) {
        val session = conversations.firstOrNull { it.id == id } ?: return
        val input = session.draft.trim()
        if (input.isEmpty() || session.isResponding) return
        val messageId = "message-${++nextId}"
        update(id) {
            it.copy(
                title = if (it.messages.isEmpty()) UiText.Literal(input.take(24)) else it.title,
                preview = UiText.Literal(input),
                updatedLabel = UiText.Resource(Res.string.just_now),
                draft = "",
                runState = AgentRunState.Running,
                messages =
                    it.messages +
                        listOf(
                            ChatMessage("$messageId-user", MessageRole.User, input),
                            ChatMessage(messageId, MessageRole.Assistant, ""),
                        ),
            )
        }
        streamReply(id, messageId, reply)
    }

    fun retry(
        id: String,
        reply: String,
    ) {
        val session = conversations.firstOrNull { it.id == id } ?: return
        if (session.isResponding) return
        val messageId = "message-${++nextId}"
        update(id) { it.copy(runState = AgentRunState.Running, messages = it.messages + ChatMessage(messageId, MessageRole.Assistant, "")) }
        streamReply(id, messageId, reply)
    }

    fun previewRunState(
        id: String,
        state: AgentRunState,
    ) {
        replies.remove(id)?.cancel()
        update(id) {
            it.copy(
                runState = state,
                messages =
                    it.messages.filterNot { message ->
                        message.role == MessageRole.Assistant &&
                            message.text.isEmpty()
                    },
            )
        }
    }

    private fun streamReply(
        id: String,
        messageId: String,
        reply: String,
    ) {
        replies[id] =
            viewModelScope.launch {
                delay(350)
                for (length in 1..reply.length step 3) {
                    val text = reply.take((length + 2).coerceAtMost(reply.length))
                    update(id) { state ->
                        state.copy(messages = state.messages.map { if (it.id == messageId) it.copy(text = text) else it })
                    }
                    delay(45)
                }
                update(id) { it.copy(runState = AgentRunState.Completed, preview = UiText.Resource(Res.string.reply_complete)) }
                replies.remove(id)
            }
    }

    fun stop(id: String) {
        replies.remove(id)?.cancel()
        update(id) {
            it.copy(
                runState = AgentRunState.Stopped,
                preview = UiText.Resource(Res.string.reply_stopped),
                messages = it.messages.filterNot { message -> message.role == MessageRole.Assistant && message.text.isEmpty() },
            )
        }
    }

    private fun update(
        id: String,
        transform: (ConversationUiState) -> ConversationUiState,
    ) {
        conversations = conversations.map { if (it.id == id) transform(it) else it }
    }
}
