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
    val isResponding: Boolean = false,
    val responseStopped: Boolean = false,
)
