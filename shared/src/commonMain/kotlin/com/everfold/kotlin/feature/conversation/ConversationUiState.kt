package com.everfold.kotlin.feature.conversation

enum class MessageRole { User, Assistant }

data class ChatMessage(
    val id: String,
    val role: MessageRole,
    val text: String,
)

data class ConversationUiState(
    val id: String,
    val title: String,
    val preview: String,
    val updatedLabel: String,
    val messages: List<ChatMessage> = emptyList(),
    val draft: String = "",
    val isResponding: Boolean = false,
    val responseStopped: Boolean = false,
)
