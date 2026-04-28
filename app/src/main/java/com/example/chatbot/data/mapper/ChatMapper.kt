package com.example.chatbot.data.mapper

import com.example.chatbot.data.model.ChatNode as DataChatNode
import com.example.chatbot.data.model.ChatOption as DataChatOption
import com.example.chatbot.domain.model.ChatNode as DomainChatNode
import com.example.chatbot.domain.model.ChatOption as DomainChatOption

fun DataChatNode.toDomainChatNode(): DomainChatNode {
    return DomainChatNode(
        id = this.id,
        service = this.service,
        message = this.message,
        options = this.options.map { it.toDomainChatOption() },
    )
}

fun DataChatOption.toDomainChatOption(): DomainChatOption {
    return DomainChatOption(
        text = this.text,
        nextNodeId = this.nextNodeId
    )
}

fun DomainChatNode.toDataChatNode(): DataChatNode {
    return DataChatNode(
        id = this.id,
        service = this.service,
        message = this.message,
        options = this.options.map { it.toDataChatOption() }
    )
}

fun DomainChatOption.toDataChatOption(): DataChatOption {
    return DataChatOption(
        text = this.text,
        nextNodeId = this.nextNodeId
    )
}
