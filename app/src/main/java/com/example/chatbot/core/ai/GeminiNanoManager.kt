package com.example.chatbot.core.ai

import android.content.Context
import android.util.Log
import com.google.ai.edge.aicore.GenerativeModel
import com.google.ai.edge.aicore.GenerationConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

enum class FeatureStatus {
    AVAILABLE,
    UNAVAILABLE
}

@Singleton
class GeminiNanoManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private var _model: GenerativeModel? = null
    
    private val _status = MutableStateFlow(FeatureStatus.UNAVAILABLE)
    val status: StateFlow<FeatureStatus> = _status.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        initializeModel()
    }

    private fun initializeModel() {
        val config = GenerationConfig.builder()
            .apply {
                context = this@GeminiNanoManager.context
            }
            .build()
        
        try {
            _model = GenerativeModel(config)
            updateStatus()
        } catch (e: Exception) {
            Log.e("GeminiNanoManager", "Error initializing model", e)
            _status.value = FeatureStatus.UNAVAILABLE
        }
    }

    fun updateStatus() {
        val model = _model ?: return
        scope.launch(Dispatchers.IO) {
            try {
                Log.d("GeminiNanoManager", "Preparing inference engine...")
                // Note: prepareInferenceEngine() can take time as it may trigger model download.
                // It requires AICore to be installed and updated on a supported device.
                model.prepareInferenceEngine()
                _status.value = FeatureStatus.AVAILABLE
                Log.d("GeminiNanoManager", "Gemini Nano is ready")
            } catch (e: Exception) {
                Log.e("GeminiNanoManager", "Error preparing inference engine: ${e.message}", e)
                _status.value = FeatureStatus.UNAVAILABLE
                
                if (e.message?.contains("601") == true || e.message?.contains("BINDING_FAILURE") == true) {
                    Log.e("GeminiNanoManager", "AICore binding failure. Ensure:")
                    Log.e("GeminiNanoManager", "1. Device supports Gemini Nano (e.g., Pixel 8+, S24+).")
                    Log.e("GeminiNanoManager", "2. AICore and Private Compute Services apps are updated in Play Store.")
                    Log.e("GeminiNanoManager", "3. 'Enable on-device GenAI Features' is toggled in Developer Options.")
                    Log.e("GeminiNanoManager", "4. Bootloader is locked (required for AICore).")
                }
            }
        }
    }

    fun downloadModel() {
        updateStatus() // prepareInferenceEngine triggers download if needed
    }

    fun getModel(): GenerativeModel? {
        return if (_status.value == FeatureStatus.AVAILABLE) _model else null
    }
}
