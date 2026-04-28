package com.example.chatbot.core.navigation

import kotlinx.serialization.Serializable

/**
 * Simplified type-safe navigation routes.
 */
sealed interface Route {
    @Serializable
    data object Splash : Route

    @Serializable
    data object Menu : Route

    @Serializable
    data class Chat(val serviceTitle: String) : Route
}
