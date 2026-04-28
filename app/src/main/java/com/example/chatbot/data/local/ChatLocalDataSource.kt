package com.example.chatbot.data.local

import com.example.chatbot.data.model.ChatNode
import com.example.chatbot.data.model.ChatOption

object ChatLocalDataSource {
    val chatNodes = listOf(
        // ================= ROOT NODES =================
        ChatNode(
            id = "banking_root",
            service = "banking",
            message = "Welcome to Banking Assistant 👋 How can I help you today?",
            options = listOf(
                ChatOption("Account Balance", "account_balance"),
                ChatOption("Loan Info", "loan_info"),
                ChatOption("Transfer Funds", "transfer_funds"),
                ChatOption("Mini Statement", "mini_statement"),
            )
        ),
        ChatNode(
            id = "shopping_root",
            service = "shopping",
            message = "Welcome to Shopping Assistant 🛍️ What are you looking for?",
            options = listOf(
                ChatOption("Find Products", "find_products"),
                ChatOption("Latest Offers", "latest_offers"),
                ChatOption("Cart Status", "cart_status"),
                ChatOption("Order History", "order_history")
            )
        ),
        ChatNode(
            id = "delivery_root",
            service = "delivery",
            message = "Welcome to Delivery Assistant 🚚 How can I help?",
            options = listOf(
                ChatOption("Track Order", "track_order"),
                ChatOption("Report Issue", "report_issue"),
                ChatOption("Change Address", "change_address"),
                ChatOption("Delivery Status", "delivery_status")
            )
        ),
        ChatNode(
            id = "travel_root",
            service = "travel",
            message = "Welcome to Travel & Booking ✈️ What would you like to do?",
            options = listOf(
                ChatOption("Book Flight", "book_flight"),
                ChatOption("Hotel Deals", "hotel_deals"),
                ChatOption("My Trips", "my_trips"),
                ChatOption("Cancel Booking", "cancel_booking")
            )
        ),

        // ================= BANKING FLOW =================
        ChatNode(
            id = "account_balance",
            message = "Your current balance is $5,420.50. Would you like a detailed statement?",
            options = listOf(
                ChatOption("Detailed Statement", "detailed_statement"),
                ChatOption("Main Menu", "banking_root")
            )
        ),
        ChatNode(
            id = "detailed_statement",
            message = "I can generate a statement for you. Which format would you like?",
            options = listOf(
                ChatOption("PDF Version", "pdf_version"),
                ChatOption("Excel Format", "excel_version")
            )
        ),
        ChatNode(
            id = "pdf_version",
            message = "Statement generated for October. Select the duration for the report.",
            options = listOf(
                ChatOption("Last 3 Months", "last_3_months"),
                ChatOption("Last 6 Months", "last_6_months")
            )
        ),
        ChatNode(
            id = "last_6_months",
            message = "Report for the last 6 months is ready. Send it to your registered email?",
            options = listOf(
                ChatOption("Send to Email", "send_email"),
                ChatOption("Download Now", "download_now")
            )
        ),
        ChatNode(
            id = "send_email",
            message = "Sent! 📧 Check your inbox. Would you like to verify the receipt status?",
            options = listOf(
                ChatOption("Verify Receipt", "verify_receipt"),
                ChatOption("Main Menu", "banking_root")
            )
        ),
        ChatNode(
            id = "verify_receipt",
            message = "Email server confirms delivery. Receipt verified.",
            options = listOf(
                ChatOption("Main Menu", "banking_root"),
                ChatOption("That's all", "satisfaction_check"),
            )
        ),

        ChatNode(
            id = "loan_info",
            message = "We offer Home, Personal, and Auto loans. Interested in checking eligibility?",
            options = listOf(
                ChatOption("Check Eligibility", "check_eligibility"),
                ChatOption("Interest Rates", "interest_rates"),
            )
        ),
        ChatNode(
            id = "check_eligibility",
            message = "Which type of loan are you looking for?",
            options = listOf(
                ChatOption("Home Loan", "home_loan"),
                ChatOption("Personal Loan", "personal_loan")
            )
        ),
        ChatNode(
            id = "home_loan",
            message = "For Home Loans, I can calculate your EMI. Proceed?",
            options = listOf(
                ChatOption("Calculate EMI", "calculate_emi"),
                ChatOption("Requirements", "requirements")
            )
        ),
        ChatNode(
            id = "calculate_emi",
            message = "Estimated EMI is $1,467/mo. Book consultation?",
            options = listOf(
                ChatOption("Book Appointment", "book_appointment"),
                ChatOption("Apply Now", "apply_now")
            )
        ),
        ChatNode(
            id = "book_appointment",
            message = "Appointment booked for tomorrow at 11 AM.",
            options = listOf(
                ChatOption("Add to Calendar", "add_calendar"),
                ChatOption("Main Menu", "banking_root")
            )
        ),
        ChatNode(
            id = "add_calendar",
            message = "Event added successfully.",
            options = listOf(
                ChatOption("Main Menu", "banking_root"),
                ChatOption("That's all", "satisfaction_check"),
            )
        ),

        // ================= SHOPPING FLOW =================
        ChatNode(
            id = "find_products",
            message = "What are you looking for today?",
            options = listOf(
                ChatOption("Electronics", "electronics"),
                ChatOption("Fashion", "fashion")
            )
        ),
        ChatNode(
            id = "electronics",
            message = "We have Smartphones. Want to explore?",
            options = listOf(
                ChatOption("Smartphones", "smartphones"),
                ChatOption("Laptops", "laptops")
            )
        ),
        ChatNode(
            id = "smartphones",
            message = "iPhone 15 is available. View bundles?",
            options = listOf(
                ChatOption("iPhone 15", "iphone_15"),
                ChatOption("Samsung S24", "samsung_s24")
            )
        ),
        ChatNode(
            id = "iphone_15",
            message = "Bundle: iPhone 15 + Charger for $899.",
            options = listOf(
                ChatOption("Add to Cart", "add_to_cart"),
                ChatOption("More Info", "more_info")
            )
        ),
        ChatNode(
            id = "add_to_cart",
            message = "Added to cart 🛒",
            options = listOf(
                ChatOption("Proceed to Checkout", "checkout"),
                ChatOption("Keep Shopping", "shopping_root")
            )
        ),
        ChatNode(
            id = "checkout",
            message = "Proceed to secure payment.",
            options = listOf(
                ChatOption("Main Menu", "shopping_root"),
                ChatOption("That's all", "satisfaction_check"),
            )
        ),

        // ================= DELIVERY FLOW =================
        ChatNode(
            id = "track_order",
            message = "Order #12345 is in transit.",
            options = listOf(
                ChatOption("Shipping Log", "shipping_log"),
                ChatOption("Delivery Status", "delivery_status")
            )
        ),
        ChatNode(
            id = "shipping_log",
            message = "Oct 15: Dispatched\nOct 16: Hub\nOct 17: Out for delivery.",
            options = listOf(
                ChatOption("Track Live", "track_live"),
                ChatOption("Change Address", "change_address")
            )
        ),
        ChatNode(
            id = "track_live",
            message = "Driver is 5 mins away.",
            options = listOf(
                ChatOption("Contact Agent", "contact_agent"),
                ChatOption("Report Delay", "report_delay")
            )
        ),
        ChatNode(
            id = "contact_agent",
            message = "Agent: John.",
            options = listOf(
                ChatOption("Leave Message", "leave_message"),
                ChatOption("Call Agent", "call_agent")
            )
        ),
        ChatNode(
            id = "leave_message",
            message = "Message sent.",
            options = listOf(
                ChatOption("Request Photo Proof", "photo_proof"),
                ChatOption("Main Menu", "delivery_root")
            )
        ),
        ChatNode(
            id = "photo_proof",
            message = "Driver will take a photo.",
            options = listOf(
                ChatOption("Main Menu", "delivery_root"),
                ChatOption("That's all", "satisfaction_check"),
            )
        ),

        // ================= TRAVEL FLOW =================
        ChatNode(
            id = "hotel_deals",
            message = "We have deals in Europe and Asia.",
            options = listOf(
                ChatOption("Europe", "europe"),
                ChatOption("Asia", "asia")
            )
        ),
        ChatNode(
            id = "europe",
            message = "Top deals in Paris.",
            options = listOf(
                ChatOption("Paris", "paris"),
                ChatOption("London", "london")
            )
        ),
        ChatNode(
            id = "paris",
            message = "Recommended: Ritz Paris.",
            options = listOf(
                ChatOption("Ritz Paris", "ritz_paris"),
                ChatOption("Hotel Le Meurice", "meurice")
            )
        ),
        ChatNode(
            id = "ritz_paris",
            message = "Luxury Suite: $450/night.",
            options = listOf(
                ChatOption("Confirm Booking", "confirm_booking"),
                ChatOption("View Gallery", "gallery")
            )
        ),
        ChatNode(
            id = "confirm_booking",
            message = "Booking Confirmed 🏨",
            options = listOf(
                ChatOption("Book Airport Transfer", "airport_transfer"),
                ChatOption("View Itinerary", "itinerary")
            )
        ),
        ChatNode(
            id = "airport_transfer",
            message = "Transfer booked successfully.",
            options = listOf(
                ChatOption("Main Menu", "travel_root"),
                ChatOption("That's all", "satisfaction_check"),
            )
        ),

        ChatNode(
            id = "interest_rates",
            message = "Our current rates are: Home (8.5%), Personal (11.0%), Auto (9.2%). Need more details?",
            options = listOf(
                ChatOption("Home Loan Details", "home_loan"),
                ChatOption("Personal Loan Details", "personal_loan"),
                ChatOption("Main Menu", "banking_root")
            )
        ),
        ChatNode(
            id = "requirements",
            message = "General requirements: ID Proof, Income Proof (3 months), and Credit Score > 700. Check eligibility?",
            options = listOf(
                ChatOption("Check Eligibility", "check_eligibility"),
                ChatOption("Main Menu", "banking_root")
            )
        ),
        ChatNode(
            id = "apply_now",
            message = "Please visit our website or nearby branch to complete the formal application.",
            options = listOf(
                ChatOption("Main Menu", "banking_root"),
                ChatOption("That's all", "satisfaction_check")
            )
        ),
        ChatNode(
            id = "mini_statement",
            message = "Fetching your last 5 transactions... \n1. $50 (Grocery)\n2. $120 (Electric)\n3. $200 (Atm)\n4. $15 (Coffee)\n5. $1000 (Salary).",
            options = listOf(
                ChatOption("Detailed Statement", "detailed_statement"),
                ChatOption("Main Menu", "banking_root")
            )
        ),
        ChatNode(
            id = "transfer_funds",
            message = "To whom would you like to transfer funds? Select a saved contact or enter new.",
            options = listOf(
                ChatOption("Saved Contact", "saved_contact"),
                ChatOption("Main Menu", "banking_root")
            )
        ),
        ChatNode(
            id = "saved_contact",
            message = "Transfer to John Doe ($200) was successful.",
            options = listOf(
                ChatOption("Transfer More", "transfer_funds"),
                ChatOption("Main Menu", "banking_root")
            )
        ),
        ChatNode(
            id = "excel_version",
            message = "Excel statement generated. Send to your email?",
            options = listOf(
                ChatOption("Send to Email", "send_email"),
                ChatOption("Download Now", "download_now")
            )
        ),
        ChatNode(
            id = "download_now",
            message = "Download link: https://chatbot.example/statement/october.xlsx",
            options = listOf(
                ChatOption("Main Menu", "banking_root"),
                ChatOption("That's all", "satisfaction_check")
            )
        ),
        ChatNode(
            id = "last_3_months",
            message = "Statement for last 3 months is ready. Send it to your email?",
            options = listOf(
                ChatOption("Send to Email", "send_email"),
                ChatOption("Download Now", "download_now")
            )
        ),
        ChatNode(
            id = "personal_loan",
            message = "Personal Loan rates start from 11.0%. Calculate EMI?",
            options = listOf(
                ChatOption("Calculate EMI", "calculate_emi"),
                ChatOption("Requirements", "requirements")
            )
        ),

        // ================= SHOPPING FLOW =================
        ChatNode(
            id = "latest_offers",
            message = "Flat 20% OFF on all Smartphones! Check them out?",
            options = listOf(
                ChatOption("View Smartphones", "smartphones"),
                ChatOption("Main Menu", "shopping_root")
            )
        ),
        ChatNode(
            id = "cart_status",
            message = "You have 1 item in your cart: iPhone 15 ($899). Proceed?",
            options = listOf(
                ChatOption("Proceed to Checkout", "checkout"),
                ChatOption("Keep Shopping", "shopping_root")
            )
        ),
        ChatNode(
            id = "order_history",
            message = "Last Order: #98765 (Delivered). View details?",
            options = listOf(
                ChatOption("View Details", "order_details"),
                ChatOption("Main Menu", "shopping_root")
            )
        ),
        ChatNode(
            id = "order_details",
            message = "Order #98765: Samsung Galaxy Watch ($299) - Delivered on Oct 10.",
            options = listOf(
                ChatOption("Main Menu", "shopping_root"),
                ChatOption("That's all", "satisfaction_check")
            )
        ),
        ChatNode(
            id = "fashion",
            message = "We have the latest Fall Collection. View catalog?",
            options = listOf(
                ChatOption("View catalog", "fashion_catalog"),
                ChatOption("Main Menu", "shopping_root")
            )
        ),
        ChatNode(
            id = "fashion_catalog",
            message = "Fall Collection 2024: Hoodies, Jackets, and more.",
            options = listOf(
                ChatOption("Main Menu", "shopping_root"),
                ChatOption("That's all", "satisfaction_check")
            )
        ),
        ChatNode(
            id = "laptops",
            message = "MacBook Air M2 is available. View specs?",
            options = listOf(
                ChatOption("View Specs", "laptop_specs"),
                ChatOption("Main Menu", "shopping_root")
            )
        ),
        ChatNode(
            id = "laptop_specs",
            message = "M2 Chip, 8GB RAM, 256GB SSD - $999.",
            options = listOf(
                ChatOption("Add to Cart", "add_to_cart"),
                ChatOption("Main Menu", "shopping_root")
            )
        ),
        ChatNode(
            id = "samsung_s24",
            message = "Samsung Galaxy S24: $799. Pre-order now?",
            options = listOf(
                ChatOption("Pre-order", "add_to_cart"),
                ChatOption("Main Menu", "shopping_root")
            )
        ),
        ChatNode(
            id = "more_info",
            message = "iPhone 15 features a 48MP camera and USB-C. Any other questions?",
            options = listOf(
                ChatOption("Main Menu", "shopping_root"),
                ChatOption("That's all", "satisfaction_check")
            )
        ),

        // ================= DELIVERY FLOW =================
        ChatNode(
            id = "report_issue",
            message = "I'm sorry to hear that. What's the problem?",
            options = listOf(
                ChatOption("Damaged Item", "damaged_item"),
                ChatOption("Missing Package", "missing_package"),
                ChatOption("Main Menu", "delivery_root")
            )
        ),
        ChatNode(
            id = "damaged_item",
            message = "Please upload a photo of the damaged item via our app for a refund.",
            options = listOf(
                ChatOption("Main Menu", "delivery_root"),
                ChatOption("That's all", "satisfaction_check")
            )
        ),
        ChatNode(
            id = "missing_package",
            message = "Our team will investigate the delivery. We'll contact you in 24 hours.",
            options = listOf(
                ChatOption("Main Menu", "delivery_root"),
                ChatOption("That's all", "satisfaction_check")
            )
        ),
        ChatNode(
            id = "change_address",
            message = "Please enter your new delivery address.",
            options = listOf(
                ChatOption("Main Menu", "delivery_root"),
                ChatOption("That's all", "satisfaction_check")
            )
        ),
        ChatNode(
            id = "delivery_status",
            message = "Your package is currently being sorted at the local hub.",
            options = listOf(
                ChatOption("Track Order", "track_order"),
                ChatOption("Main Menu", "delivery_root")
            )
        ),
        ChatNode(
            id = "report_delay",
            message = "Reported. We'll notify the logistics team immediately.",
            options = listOf(
                ChatOption("Main Menu", "delivery_root"),
                ChatOption("That's all", "satisfaction_check")
            )
        ),
        ChatNode(
            id = "call_agent",
            message = "Calling John (+123456789)...",
            options = listOf(
                ChatOption("Main Menu", "delivery_root"),
                ChatOption("That's all", "satisfaction_check")
            )
        ),

        // ================= TRAVEL FLOW =================
        ChatNode(
            id = "book_flight",
            message = "Where are you heading? London or Paris?",
            options = listOf(
                ChatOption("London", "london"),
                ChatOption("Paris", "paris")
            )
        ),
        ChatNode(
            id = "london",
            message = "Flight to London: $350. Confirm?",
            options = listOf(
                ChatOption("Confirm Flight", "confirm_booking"),
                ChatOption("Main Menu", "travel_root")
            )
        ),
        ChatNode(
            id = "asia",
            message = "Top deals in Tokyo and Bali.",
            options = listOf(
                ChatOption("Tokyo", "tokyo"),
                ChatOption("Bali", "bali")
            )
        ),
        ChatNode(
            id = "tokyo",
            message = "Recommended: Park Hyatt Tokyo.",
            options = listOf(
                ChatOption("Confirm Booking", "confirm_booking"),
                ChatOption("Main Menu", "travel_root")
            )
        ),
        ChatNode(
            id = "bali",
            message = "Recommended: Maya Ubud Resort.",
            options = listOf(
                ChatOption("Confirm Booking", "confirm_booking"),
                ChatOption("Main Menu", "travel_root")
            )
        ),
        ChatNode(
            id = "my_trips",
            message = "Next Trip: Paris (Dec 20). View details?",
            options = listOf(
                ChatOption("View Details", "itinerary"),
                ChatOption("Main Menu", "travel_root")
            )
        ),
        ChatNode(
            id = "cancel_booking",
            message = "Select booking to cancel.",
            options = listOf(
                ChatOption("Paris Trip", "cancel_paris"),
                ChatOption("Main Menu", "travel_root")
            )
        ),
        ChatNode(
            id = "cancel_paris",
            message = "Trip cancelled. Refund processed.",
            options = listOf(
                ChatOption("Main Menu", "travel_root"),
                ChatOption("That's all", "satisfaction_check")
            )
        ),
        ChatNode(
            id = "gallery",
            message = "Showing photos of Ritz Paris... (Imagine beautiful photos).",
            options = listOf(
                ChatOption("Confirm Booking", "confirm_booking"),
                ChatOption("Main Menu", "travel_root")
            )
        ),
        ChatNode(
            id = "itinerary",
            message = "Itinerary: Day 1: Louvre, Day 2: Eiffel Tower.",
            options = listOf(
                ChatOption("Main Menu", "travel_root"),
                ChatOption("That's all", "satisfaction_check")
            )
        ),
        ChatNode(
            id = "meurice",
            message = "Hotel Le Meurice: $400/night. Confirm?",
            options = listOf(
                ChatOption("Confirm Booking", "confirm_booking"),
                ChatOption("Main Menu", "travel_root")
            )
        ),
        ChatNode(
            id = "satisfaction_check",
            message = "Was this helpful? Is there anything else I can do for you?",
            options = listOf(
                ChatOption("Start Again", "start_again"),
                ChatOption("End Chat", "end_chat"),
            )
        ),
        ChatNode(
            id = "end_chat",
            message = "Thank you for chatting! 👋",
            options = listOf(
                ChatOption("Exit", null),
            )
        ),
    )
}
