package com.example.chatbot.domain.model

data class ChatNode(
    val id: String,
    val service: String? = null,
    val message: String,
    val options: List<ChatOption> = emptyList()
)

data class ChatOption(
    val text: String,
    val nextNodeId: String?
)
