package com.example.chatbot.presentation.chat

import com.example.chatbot.domain.model.ChatOption
import com.example.chatbot.domain.model.Message

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val quickReplies: List<ChatOption> = emptyList(),
    val isBotTyping: Boolean = false,
    val isLoading: Boolean = false
)
