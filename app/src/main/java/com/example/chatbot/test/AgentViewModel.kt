package com.example.chatbot.test

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.chatbot.core.ai.GeminiNanoManager
import com.google.ai.edge.aicore.content
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AgentViewModel @Inject constructor(
    private val geminiNanoManager: GeminiNanoManager
) : ViewModel() {

    suspend fun sendMessage(userPrompt: String): String {
        val model = geminiNanoManager.getModel() ?: return "Gemini Nano is not ready."
        
        return try {
            val result = model.generateContent(
                content { text(userPrompt) }
            )
            result.text ?: "No response from model."
        } catch (e: Exception) {
            Log.e("AgentViewModel", "Error in sendMessage", e)
            "Error: ${e.localizedMessage ?: "Unknown error"}"
        }
    }
}
