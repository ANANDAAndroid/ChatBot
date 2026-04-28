package com.example.chatbot.domain.model

import java.util.UUID

enum class Sender {
    USER, BOT
}

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val sender: Sender,
    val timestamp: String
)