package com.example.chatbot.data.repository

import com.example.chatbot.data.mapper.toDataChatNode
import com.example.chatbot.data.mapper.toDomainChatNode
import com.example.chatbot.domain.model.ChatNode
import com.example.chatbot.domain.repository.ChatRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.example.chatbot.data.model.ChatNode as DataChatNode
import com.example.chatbot.data.model.ChatOption as DataChatOption

class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    override suspend fun getChatNode(nodeId: String): ChatNode? {
        return try {
            val doc = firestore.collection("chat_nodes").document(nodeId).get().await()
            if (doc.exists()) {
                val dataNode = DataChatNode(
                    id = doc.id,
                    message = doc.getString("message") ?: "",
                    service = doc.getString("service"),
                    options = (doc.get("options") as? List<*>)?.mapNotNull { item ->
                        if (item is Map<*, *>) {
                            DataChatOption(
                                text = item["text"] as? String ?: "",
                                nextNodeId = item["nextNodeId"] as? String
                            )
                        } else null
                    } ?: emptyList()
                )
                dataNode.toDomainChatNode()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun seedChatNodes(nodes: List<ChatNode>) {
        try {
            // Check if data already exists to avoid re-seeding every time
            val snapshot = firestore.collection("chat_nodes").limit(1).get().await()
            if (!snapshot.isEmpty) return

            val batch = firestore.batch()
            nodes.forEach { node ->
                val nodeRef = firestore.collection("chat_nodes").document(node.id)
                val dataNode = node.toDataChatNode()
                batch.set(nodeRef, dataNode)
            }
            batch.commit().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        
    }
}
