package com.example.chatbot.domain.repository

import com.example.chatbot.domain.model.ChatNode
import com.example.chatbot.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getChatNode(nodeId: String): ChatNode?
    suspend fun seedChatNodes(nodes: List<ChatNode>)
}
