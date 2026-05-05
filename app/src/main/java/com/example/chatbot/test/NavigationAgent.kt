package com.example.chatbot.test

import android.util.Log
import com.example.chatbot.core.ai.GeminiNanoManager
import com.example.chatbot.data.local.ChatLocalDataSource
import com.example.chatbot.data.model.ChatOption
import com.google.ai.edge.aicore.content
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationAgent @Inject constructor(
    private val geminiNanoManager: GeminiNanoManager
) {
    private var lastOptions: List<ChatOption> = emptyList()
    private var currentNodeId: String? = null

    data class AgentResponse(
        val text: String,
        val options: List<ChatOption> = emptyList()
    )

    private val systemInstruction = """
        You are a smart navigation agent for a multi-service app (Banking, Shopping, Delivery, Travel).
        1. Your goal is to guide the user through the predefined 'ChatNode' flow.
        2. When a user asks for something, respond with 'ACTION: findService(serviceName="...")' or 'ACTION: getChatNode(nodeId="...")'.
        3. For follow-up questions or "system updates" (e.g., "which hub?", "any update?", "check again"), respond with 'ACTION: getSystemUpdate(context="...")'.
        4. If the user provides information requested (like an address or name), acknowledge it and move to the next step or 'satisfaction_check'.
        5. If the user asks something NOT in the data, explain what you can help with and ask them to choose.
        6. Always maintain a helpful, professional tone. When you have a final message, just write the text.
    """.trimIndent()

    suspend fun sendMessage(userPrompt: String): AgentResponse {
        val model = geminiNanoManager.getModel()
        
        lastOptions = emptyList()
        
        // Build prompt with context if we are in a specific flow
        val contextInfo = if (currentNodeId != null) "Conversation State: User is currently at step '$currentNodeId'\n" else ""
        var currentPrompt = "System: $systemInstruction\n\n$contextInfo User: $userPrompt"
        
        if (model == null) {
            return handleFallback(userPrompt)
        }
        
        return try {
            var responseText = ""
            var iterations = 0
            val maxIterations = 5

            while (iterations < maxIterations) {
                val result = model.generateContent(
                    content { text(currentPrompt) }
                )
                val text = result.text ?: "No response from model."
                
                if (text.startsWith("ACTION:")) {
                    val actionResult = handleManualAction(text)
                    currentPrompt += "\nModel: $text\nObservation: $actionResult"
                    iterations++
                } else {
                    responseText = text
                    break
                }
            }

            AgentResponse(
                text = responseText.ifEmpty { "Agent reached max iterations." },
                options = lastOptions
            )
        } catch (e: Exception) {
            Log.e("NavigationAgent", "Error in sendMessage", e)
            handleFallback(userPrompt)
        }
    }

    fun setCurrentNode(nodeId: String?) {
        this.currentNodeId = nodeId
    }

    private fun handleFallback(userPrompt: String): AgentResponse {
        val prompt = userPrompt.lowercase()

        // 1. Handle specific "input" states where the user is providing data
        when (currentNodeId) {
            "change_address" -> {
                currentNodeId = "satisfaction_check"
                val nextNode = ChatLocalDataSource.chatNodes.find { it.id == currentNodeId }
                lastOptions = nextNode?.options ?: emptyList()
                val formattedAddress = userPrompt.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                return AgentResponse(
                    text = "Thank you. I have updated your delivery address to: $formattedAddress. ${nextNode?.message}",
                    options = lastOptions
                )
            }
            "transfer_funds" -> {
                currentNodeId = "saved_contact"
                val nextNode = ChatLocalDataSource.chatNodes.find { it.id == currentNodeId }
                lastOptions = nextNode?.options ?: emptyList()
                val formattedName = userPrompt.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                return AgentResponse(
                    text = "Transfer to $formattedName was successful. What else can I help with?",
                    options = lastOptions
                )
            }
            "book_flight" -> {
                val matchedId = when {
                    prompt.contains("london") -> "london"
                    prompt.contains("paris") -> "paris"
                    else -> null
                }
                if (matchedId != null) {
                    currentNodeId = matchedId
                    val node = ChatLocalDataSource.chatNodes.find { it.id == matchedId }
                    lastOptions = node?.options ?: emptyList()
                    return AgentResponse(text = node?.message ?: "", options = lastOptions)
                }
            }
            "delivery_status", "track_order", "shipping_log" -> {
                if (prompt.contains("hub") || prompt.contains("where") || prompt.contains("update")) {
                    return AgentResponse(
                        text = "Checking system... 🔄\nYour package is currently at the Regional Hub (North District). It was sorted at 10:45 AM and is scheduled to be out for delivery by evening.",
                        options = listOf(ChatOption("Track Live", "track_live"), ChatOption("Main Menu", "delivery_root"))
                    )
                }
            }
            "satisfaction_check" -> {
                if (prompt.contains("yes") || prompt.contains("helpful") || prompt.contains("thanks") || prompt.contains("thank you")) {
                    currentNodeId = "end_chat"
                    val node = ChatLocalDataSource.chatNodes.find { it.id == currentNodeId }
                    lastOptions = node?.options ?: emptyList()
                    return AgentResponse(text = "You're welcome! ${node?.message}", options = lastOptions)
                }
                if (prompt.contains("no") || prompt.contains("nothing else") || prompt.contains("exit")) {
                    currentNodeId = "end_chat"
                    val node = ChatLocalDataSource.chatNodes.find { it.id == currentNodeId }
                    lastOptions = node?.options ?: emptyList()
                    return AgentResponse(text = node?.message ?: "", options = lastOptions)
                }
            }
        }

        // 1.5 Handle generic acknowledgments (OK, Cool, Fine)
        val acknowledgments = listOf("ok", "okay", "fine", "cool", "sure", "got it")
        if (acknowledgments.any { it == prompt }) {
            val node = ChatLocalDataSource.chatNodes.find { it.id == currentNodeId }
            if (node != null && node.options.isNotEmpty()) {
                lastOptions = node.options
                return AgentResponse(
                    text = "I see. Please select an option to continue:",
                    options = lastOptions
                )
            }
        }

        // 2. Check if the prompt matches any option in the CURRENT node specifically
        val currentNode = ChatLocalDataSource.chatNodes.find { it.id == currentNodeId }
        val matchedOption = currentNode?.options?.find { 
            prompt.contains(it.text.lowercase()) || (it.nextNodeId != null && prompt.contains(it.nextNodeId.lowercase()))
        }
        if (matchedOption != null && matchedOption.nextNodeId != null) {
            currentNodeId = matchedOption.nextNodeId
            val node = ChatLocalDataSource.chatNodes.find { it.id == currentNodeId }
            lastOptions = node?.options ?: emptyList()
            return AgentResponse(text = node?.message ?: "", options = lastOptions)
        }

        // 3. Global Keyword Mapping (Exhaustive search across all services)
        val keywordMap = mapOf(
            // Banking
            "balance" to "account_balance", "bal" to "account_balance",
            "mini statement" to "mini_statement", "transactions" to "mini_statement",
            "detailed statement" to "detailed_statement", "statement" to "detailed_statement",
            "pdf" to "pdf_version", "excel" to "excel_version", "email" to "send_email",
            "verify" to "verify_receipt", "receipt" to "verify_receipt",
            "loan" to "loan_info", "interest" to "interest_rates", "rate" to "interest_rates",
            "eligib" to "check_eligibility", "home loan" to "home_loan", "personal loan" to "personal_loan",
            "emi" to "calculate_emi", "calculate" to "calculate_emi", "requirement" to "requirements",
            "document" to "requirements", "appointment" to "book_appointment", "calendar" to "add_calendar",
            "transfer" to "transfer_funds", "pay" to "transfer_funds", "send money" to "transfer_funds",
            "3 months" to "last_3_months", "6 months" to "last_6_months", "download" to "download_now",

            // Shopping
            "product" to "find_products", "electronic" to "electronics", "fashion" to "fashion",
            "clothing" to "fashion", "catalog" to "fashion_catalog", "smartphone" to "smartphones",
            "phone" to "smartphones", "iphone" to "iphone_15", "samsung" to "samsung_s24",
            "laptop" to "laptops", "macbook" to "laptops", "specs" to "laptop_specs",
            "bundle" to "iphone_15", "cart" to "cart_status", "checkout" to "checkout",
            "buy" to "checkout", "order history" to "order_history", "previous order" to "order_history",
            "order detail" to "order_details", "offer" to "latest_offers", "discount" to "latest_offers",
            "pre-order" to "samsung_s24", "add to cart" to "add_to_cart",

            // Delivery
            "track" to "track_order", "shipment" to "track_order", "shipping log" to "shipping_log",
            "hub" to "shipping_log", "live" to "track_live", "driver" to "track_live", "agent" to "contact_agent",
            "support" to "contact_agent", "call" to "call_agent", "message" to "leave_message",
            "photo" to "photo_proof", "proof" to "photo_proof", "issue" to "report_issue",
            "damaged" to "damaged_item", "broken" to "damaged_item", "missing" to "missing_package",
            "lost" to "missing_package", "address" to "change_address", "location" to "change_address",
            "status" to "delivery_status", "delay" to "report_delay", "late" to "report_delay",

            // Travel
            "flight" to "book_flight", "plane" to "book_flight", "london" to "london", "paris" to "paris",
            "hotel" to "hotel_deals", "deal" to "hotel_deals", "europe" to "europe", "asia" to "asia",
            "tokyo" to "tokyo", "bali" to "bali", "ritz" to "ritz_paris", "meurice" to "meurice",
            "gallery" to "gallery", "itinerary" to "itinerary", "trip" to "my_trips", "cancel" to "cancel_booking",
            "confirm" to "confirm_booking", "airport transfer" to "airport_transfer",

            // General
            "banking" to "banking_root", "shopping" to "shopping_root", "delivery" to "delivery_root",
            "travel" to "travel_root", "start" to "start_again", "restart" to "start_again",
            "end" to "end_chat", "exit" to "end_chat", "bye" to "end_chat"
        )

        val matchedNodeId = keywordMap.entries.find { prompt.contains(it.key) }?.value
        
        // 4. Content Search: If no keywords match, look for the text inside ANY node's message
        // Improved: Avoid matching very short strings (like "ok") inside longer words (like "looking")
        val contentMatchNode = if (matchedNodeId == null && prompt.length > 2) {
            ChatLocalDataSource.chatNodes.find { node ->
                val nodeMessage = node.message.lowercase()
                // Use regex for whole-word matching to be more accurate
                val regex = Regex("\\b${Regex.escape(prompt)}\\b")
                regex.containsMatchIn(nodeMessage) && 
                node.id != "banking_root" && node.id != "shopping_root" && 
                node.id != "delivery_root" && node.id != "travel_root"
            }
        } else null

        val finalNodeId = matchedNodeId ?: contentMatchNode?.id

        if (finalNodeId != null) {
            currentNodeId = finalNodeId
            val node = ChatLocalDataSource.chatNodes.find { it.id == finalNodeId }
            lastOptions = node?.options ?: emptyList()
            return AgentResponse(text = node?.message ?: "", options = lastOptions)
        }
        
        // 5. Service Root fallback
        val services = listOf("banking", "shopping", "delivery", "travel")
        val matchedService = services.find { prompt.contains(it) }
        
        if (matchedService != null) {
            val rootNode = ChatLocalDataSource.chatNodes.find { 
                it.service == matchedService && it.id.endsWith("_root") 
            }
            if (rootNode != null) {
                currentNodeId = rootNode.id
                lastOptions = rootNode.options
                return AgentResponse(text = rootNode.message, options = lastOptions)
            }
        }

        // 6. Default response
        return AgentResponse(
            text = "I'm sorry, I couldn't find specific information for that. You can ask about our Banking, Shopping, Delivery, or Travel services.",
            options = listOf(
                ChatOption("Banking Assistance", "banking_root"),
                ChatOption("Shopping Assistant", "shopping_root"),
                ChatOption("Delivery Assistant", "delivery_root"),
                ChatOption("Travel & Booking", "travel_root")
            )
        )
    }

    private fun handleManualAction(actionText: String): String {
        // Simple regex-based action parsing for Gemini Nano
        return when {
            actionText.contains("getChatNode") -> {
                val nodeId = Regex("nodeId=\"([^\"]+)\"").find(actionText)?.groupValues?.get(1) ?: ""
                currentNodeId = nodeId
                val node = ChatLocalDataSource.chatNodes.find { it.id == nodeId }
                lastOptions = node?.options ?: emptyList()
                JSONObject().apply {
                    put("message", node?.message ?: "Node not found")
                    put("options", JSONArray(node?.options?.map { "${it.text} (ID: ${it.nextNodeId})" } ?: emptyList<String>()))
                }.toString()
            }

            actionText.contains("findService") -> {
                val serviceName = Regex("serviceName=\"([^\"]+)\"").find(actionText)?.groupValues?.get(1) ?: ""
                val rootNode = ChatLocalDataSource.chatNodes.find {
                    it.service == serviceName.lowercase() && it.id.endsWith("_root")
                }
                currentNodeId = rootNode?.id
                lastOptions = rootNode?.options ?: emptyList()
                JSONObject().apply {
                    put("rootId", rootNode?.id ?: "none")
                    put("welcomeMessage", rootNode?.message ?: "How can I help?")
                    put("options", JSONArray(rootNode?.options?.map { "${it.text} (ID: ${it.nextNodeId})" } ?: emptyList<String>()))
                }.toString()
            }

            actionText.contains("getServices") -> {
                val services = ChatLocalDataSource.chatNodes.filter { it.id.endsWith("_root") }
                JSONObject().apply {
                    put("availableServices", JSONArray(services.map { "${it.service} (Root ID: ${it.id})" }))
                }.toString()
            }

            actionText.contains("getSystemUpdate") -> {
                val context = Regex("context=\"([^\"]+)\"").find(actionText)?.groupValues?.get(1) ?: ""
                // Simulate a system check based on the current node
                when (currentNodeId) {
                    "delivery_status", "track_order", "shipping_log" -> {
                        JSONObject().apply {
                            put("status", "Latest Update")
                            put("detail", "Package is at the Regional Hub (North District). Sorted at 10:45 AM today.")
                            put("next_step", "Out for delivery by evening.")
                        }.toString()
                    }
                    "account_balance" -> {
                        JSONObject().apply {
                            put("status", "Real-time Balance")
                            put("amount", "$5,420.50")
                            put("last_sync", "2 minutes ago")
                        }.toString()
                    }
                    else -> "System is up to date. No new changes for $context."
                }
            }

            else -> "Unknown tool"
        }
    }
}
