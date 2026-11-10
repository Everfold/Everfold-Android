package com.everfold.kotlin.mock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everfold.kotlin.feature.conversation.ChatMessage
import com.everfold.kotlin.feature.conversation.ConversationUiState
import com.everfold.kotlin.feature.conversation.MessageRole
import com.everfold.kotlin.ui.theme.ThemeColorSelection
import com.everfold.kotlin.ui.theme.ThemeMode
import com.everfold.kotlin.ui.theme.ThemePreset
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** In-memory UI fixtures. This class never creates an Agent or opens a connection. */
class MockWorkspace : ViewModel() {
    var conversations by mutableStateOf(sampleConversations())
        private set
    var themeMode by mutableStateOf(ThemeMode.System)
        private set
    var themeColor by mutableStateOf(ThemeColorSelection())
        private set
    var connectionMode by mutableStateOf("本地运行")
        private set
    var remoteAddress by mutableStateOf("https://agent.example.com")
        private set
    var connectionPreview by mutableStateOf(false)
        private set

    private var nextId = 0
    private val replies = mutableMapOf<String, Job>()

    fun createConversation(): String {
        val id = "new-${++nextId}"
        conversations = listOf(ConversationUiState(id, "新会话", "从一个问题或想法开始", "刚刚")) + conversations
        return id
    }

    fun updateDraft(
        id: String,
        value: String,
    ) = update(id) { it.copy(draft = value) }

    fun setTheme(mode: ThemeMode) {
        themeMode = mode
    }

    fun selectThemePreset(preset: ThemePreset) {
        themeColor = themeColor.copy(preset = preset)
    }

    fun applyCustomThemeColor(color: Color) {
        themeColor = ThemeColorSelection(preset = null, customSeed = color)
    }

    fun changeConnectionMode(mode: String) {
        connectionMode = mode
        connectionPreview = false
    }

    fun updateRemoteAddress(value: String) {
        remoteAddress = value
        connectionPreview = false
    }

    fun toggleConnectionPreview() {
        connectionPreview = !connectionPreview
    }

    fun send(id: String) {
        val session = conversations.firstOrNull { it.id == id } ?: return
        val input = session.draft.trim()
        if (input.isEmpty() || session.isResponding) return
        val messageId = "message-${++nextId}"
        update(id) {
            it.copy(
                title = if (it.messages.isEmpty()) input.take(24) else it.title,
                preview = input,
                updatedLabel = "刚刚",
                draft = "",
                isResponding = true,
                responseStopped = false,
                messages =
                    it.messages +
                        listOf(
                            ChatMessage("$messageId-user", MessageRole.User, input),
                            ChatMessage(messageId, MessageRole.Assistant, ""),
                        ),
            )
        }
        replies[id] =
            viewModelScope.launch {
                delay(350)
                for (length in 1..SAMPLE_REPLY.length step 3) {
                    val text = SAMPLE_REPLY.take((length + 2).coerceAtMost(SAMPLE_REPLY.length))
                    update(id) { state ->
                        state.copy(messages = state.messages.map { if (it.id == messageId) it.copy(text = text) else it })
                    }
                    delay(45)
                }
                update(id) { it.copy(isResponding = false, preview = "示例回复已完成") }
                replies.remove(id)
            }
    }

    fun stop(id: String) {
        replies.remove(id)?.cancel()
        update(id) {
            it.copy(
                isResponding = false,
                responseStopped = true,
                preview = "已停止生成",
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
