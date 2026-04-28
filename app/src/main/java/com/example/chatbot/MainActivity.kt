package com.example.chatbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.chatbot.core.theme.ChatBotTheme
import com.example.chatbot.core.navigation.AppNavigation
import com.example.chatbot.test.AgentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val agentViewModel: AgentViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            //val res = agentViewModel.sendMessage("What is 15 times 4, and how is the weather in Mumbai?")
            //val res = agentViewModel.sendMessage2("I want to book a travel ticket")

            //println("resr........$res")
        }


        enableEdgeToEdge()
        setContent {
            ChatBotTheme {
                AppNavigation()
            }
        }
    }
}
