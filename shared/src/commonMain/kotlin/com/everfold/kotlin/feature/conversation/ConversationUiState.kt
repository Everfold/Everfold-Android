package com.everfold.kotlin.feature.conversation

import com.everfold.kotlin.ui.localization.UiText

enum class MessageRole { User, Assistant }

data class ChatMessage(
    val id: String,
    val role: MessageRole,
    val text: String,
)

data class ConversationUiState(
    val id: String,
    val title: UiText,
    val preview: UiText,
    val updatedLabel: UiText,
    val messages: List<ChatMessage> = emptyList(),
    val draft: String = "",
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val runState: AgentRunState = AgentRunState.Idle,
) {
    val isResponding: Boolean get() = runState == AgentRunState.Running
    val responseStopped: Boolean get() = runState == AgentRunState.Stopped
}

enum class AgentRunState { Idle, Running, Waiting, Completed, Stopped, Failed }
