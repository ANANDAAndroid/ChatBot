package com.example.chatbot.test

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.FunctionCallPart
import com.google.ai.client.generativeai.type.FunctionResponsePart
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import org.json.JSONObject

@HiltViewModel
class AgentViewModel @Inject constructor() : ViewModel() {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash", // Use Flash for speed in agentic loops
        apiKey = "AIzaSyAdmSkPWMJgqnJFlbZZ89W6DW3quDfPPO0",
        tools = listOf(Tool(listOf(AgentTools.weatherTool, AgentTools.calcTool)))
    )

    suspend fun sendMessage(userPrompt: String): String {
        val chat = generativeModel.startChat()
        return try {
            // 1. Send the initial message
            var response = chat.sendMessage(userPrompt)

            // 2. The "Agentic Loop": Execute functions until the model gives a final text answer
            while (response.functionCalls.isNotEmpty()) {
                val responses = response.functionCalls.map { call ->
                    val result = handleFunctionCall(call)
                    FunctionResponsePart(call.name, result)
                }

                // 3. Send tool results back to the model to get the next step
                // The role MUST be "function" for tool responses
                response = chat.sendMessage(
                    content("function") {
                        for (fResponse in responses) {
                            part(fResponse)
                        }
                    }
                )
            }

            response.text ?: "No response from agent."
        } catch (e: Exception) {
            Log.e("AgentViewModel", "Error in sendMessage", e)
            "Error: ${e.localizedMessage ?: "Unknown error"}"
        }
    }

    private fun handleFunctionCall(call: FunctionCallPart): JSONObject {
        return when (call.name) {
            "getWeather" -> {
                val city = call.args["location"] ?: "Unknown"
                JSONObject().put("temp", "${(20..30).random()}°C in $city")
            }
            "calculate" -> {
                val a = (call.args["a"] as? String)?.toDoubleOrNull() ?: 0.0
                val b = (call.args["b"] as? String)?.toDoubleOrNull() ?: 0.0
                val op = call.args["op"] ?: ""
                val res = when(op) {
                    "add" -> a + b
                    "mul" -> a * b
                    else -> 0.0
                }
                JSONObject().put("result", res)
            }
            else -> JSONObject().put("error", "Unknown tool")
        }
    }
}
