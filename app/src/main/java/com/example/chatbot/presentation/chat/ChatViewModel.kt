package com.example.chatbot.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.data.local.ChatLocalDataSource
import com.example.chatbot.data.mapper.toDomainChatNode
import com.example.chatbot.domain.model.*
import com.example.chatbot.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    private var currentServiceTitle: String = ""

    fun initChat(serviceTitle: String) {
        currentServiceTitle = serviceTitle
        if (_uiState.value.messages.isEmpty()) {
            val rootId = getRootId(serviceTitle)
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                // Seed data if collection is empty (handled inside repository)
                repository.seedChatNodes(ChatLocalDataSource.chatNodes.map { it.toDomainChatNode() })
                
                // Load root node without typing delay
                val node = repository.getChatNode(rootId)
                _uiState.update { it.copy(isLoading = false) }
                
                if (node != null) {
                    addBotMessage(node.message, node.options)
                }
            }
        }
    }

    private fun loadNode(nodeId: String, withDelay: Boolean = true) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBotTyping = true) }
            if (withDelay) {
                delay(1500) // Simulate typing delay
            }
            val node = repository.getChatNode(nodeId)
            _uiState.update { it.copy(isBotTyping = false) }
            
            if (node != null) {
                addBotMessage(node.message, node.options)
            } else {
                // If a node is missing, gracefully return to the service root
                loadNode(getRootId(currentServiceTitle), withDelay = false)
            }
        }
    }

    private fun getRootId(serviceTitle: String): String {
        return when (serviceTitle) {
            "Banking Assistance" -> "banking_root"
            "Delivery Assistance" -> "delivery_root"
            "Shopping Assistant" -> "shopping_root"
            "Travel & Booking" -> "travel_root"
            else -> "banking_root"
        }
    }

    fun onQuickReplyClicked(option: ChatOption) {
        val userMessage = Message(
            text = option.text,
            sender = Sender.USER,
            timestamp = getCurrentTime()
        )

        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                quickReplies = emptyList()
            )
        }

        viewModelScope.launch {
            delay(400) // Slight delay before bot starts "typing"
            when (option.nextNodeId) {
                "start_again" -> {
                    loadNode(getRootId(currentServiceTitle), withDelay = true)
                }
                null -> {
                    // Handle end of chat (Exit)
                }
                else -> {
                    loadNode(option.nextNodeId, withDelay = true)
                }
            }
        }
    }

    private fun addBotMessage(text: String, replies: List<ChatOption> = emptyList()) {
        val botMessage = Message(
            text = text,
            sender = Sender.BOT,
            timestamp = getCurrentTime()
        )
        _uiState.update {
            it.copy(
                messages = it.messages + botMessage,
                quickReplies = replies
            )
        }
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }
}
