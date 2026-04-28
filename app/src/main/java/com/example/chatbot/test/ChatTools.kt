package com.example.chatbot.test

import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.defineFunction

object ChatTools {
    val getChatNodeTool = defineFunction(
        name = "getChatNode",
        description = "Retrieves specific chat message and options using a Node ID.",
        parameters = listOf(
            Schema.str("nodeId", "The ID of the node to fetch, e.g., 'banking_root'")
        )
    )

    val findServiceTool = defineFunction(
        name = "findService",
        description = "Finds the root node ID for a specific service (banking, shopping, delivery, travel).",
        parameters = listOf(
            Schema.str("serviceName", "The service name requested by the user")
        )
    )

    val getServicesTool = defineFunction(
        name = "getServices",
        description = "Retrieves all available services and their root node IDs.",
        parameters = emptyList()
    )
}