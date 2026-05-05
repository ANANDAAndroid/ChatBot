package com.example.chatbot.test

import android.util.Log
import com.example.chatbot.data.local.ChatLocalDataSource
import com.example.chatbot.data.model.ChatOption
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.FunctionCallPart
import com.google.ai.client.generativeai.type.FunctionResponsePart
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationAgent @Inject constructor() {
    private var lastOptions: List<ChatOption> = emptyList()

    data class AgentResponse(
        val text: String,
        val options: List<ChatOption> = emptyList()
    )

    private val generativeModel = GenerativeModel(
        modelName = "models/gemini-2.0-flash",
        apiKey = "AIzaSyAoPVLt7qjMlfj-BBh07lN5Uoy7HjY1iZ4",
        systemInstruction = content {
            text("""
            You are a smart navigation agent. 
            1. When a user asks about a service (banking, travel, etc.), use 'findService'.
            2. Use 'getChatNode' to retrieve the content of specific IDs.
            3. Use 'getServices' to see all available services if the user asks for something else.
            4. Your goal is to guide the user through the predefined 'ChatNode' flow. Always mention the available options or suggest a service.
            5. If the user asks something NOT in the data, explain that you can help with the specific services you have (banking, shopping, delivery, travel) and ask them to choose one.
        """.trimIndent())
        },
        tools = listOf(Tool(listOf(ChatTools.getChatNodeTool, ChatTools.findServiceTool, ChatTools.getServicesTool)))
    )

    suspend fun sendMessage(userPrompt: String): AgentResponse {
        val chat = generativeModel.startChat()
        lastOptions = emptyList()
        return try {
            var response = chat.sendMessage(userPrompt)

            while (response.functionCalls.isNotEmpty()) {
                val responses = response.functionCalls.map { call ->
                    val result = handleAgentAction(call)
                    FunctionResponsePart(call.name, result)
                }

                response = chat.sendMessage(
                    content("function") {
                        for (fResponse in responses) {
                            part(fResponse)
                        }
                    }
                )
            }

            AgentResponse(
                text = response.text ?: "No response from agent.",
                options = lastOptions
            )
        } catch (e: Exception) {
            Log.e("NavigationAgent", "Error in sendMessage", e)
            AgentResponse(text = "Error: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    private fun handleAgentAction(call: FunctionCallPart): JSONObject {
        return when (call.name) {
            "getChatNode" -> {
                val id = call.args["nodeId"] ?: ""
                val node = ChatLocalDataSource.chatNodes.find { it.id == id }
                lastOptions = node?.options ?: emptyList()

                JSONObject().apply {
                    put("message", node?.message ?: "Node not found")
                    put("options", JSONArray(node?.options?.map { "${it.text} (ID: ${it.nextNodeId})" } ?: emptyList<String>()))
                }
            }

            "findService" -> {
                val service = call.args["serviceName"] ?: ""
                val rootNode = ChatLocalDataSource.chatNodes.find {
                    it.service == service.lowercase() && it.id.endsWith("_root")
                }
                lastOptions = rootNode?.options ?: emptyList()

                JSONObject().apply {
                    put("rootId", rootNode?.id ?: "none")
                    put("welcomeMessage", rootNode?.message ?: "How can I help?")
                    put("options", JSONArray(rootNode?.options?.map { "${it.text} (ID: ${it.nextNodeId})" } ?: emptyList<String>()))
                }
            }

            "getServices" -> {
                val services = ChatLocalDataSource.chatNodes.filter { it.id.endsWith("_root") }
                JSONObject().apply {
                    put("availableServices", JSONArray(services.map { "${it.service} (Root ID: ${it.id})" }))
                }
            }

            else -> JSONObject().put("error", "Unknown tool")
        }
    }
}
