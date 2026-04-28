package com.example.chatbot.test

import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.defineFunction

object AgentTools {
    // Tool 1: A mock weather function
    val weatherTool = defineFunction(
        name = "getWeather",
        description = "Returns the current temperature for a city.",
        parameters = listOf(
            Schema.str("location", "The city name, e.g., London")
        )
    )

    // Tool 2: A simple calculator function
    val calcTool = defineFunction(
        name = "calculate",
        description = "Performs basic math operations.",
        parameters = listOf(
            Schema.double("a", "First number"),
            Schema.double("b", "Second number"),
            Schema.str("op", "Operation: add, sub, mul, div")
        )
    )
}