package com.example.chatbot.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatbot.R
import com.example.chatbot.core.theme.*
import com.example.chatbot.domain.model.Message
import com.example.chatbot.domain.model.Sender

@Composable
fun ChatBubble(
    message: Message,
    modifier: Modifier = Modifier
) {
    val isUser = message.sender == Sender.USER
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 0.dp,
                        bottomEnd = if (isUser) 0.dp else 16.dp
                    )
                )
                .background(if (isUser) UserBubbleGreen else BotBubbleWhite)
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                color = if (isUser) Color.White else Color.Black,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = message.timestamp,
                color = TimestampGray,
                fontSize = 11.sp
            )
            if (isUser) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_check_double),
                    contentDescription = null,
                    tint = OnlineGreen,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatBubbleUserPreview() {
    ChatBotTheme {
        ChatBubble(
            message = Message(
                text = "Hello, I have a question about my account.",
                sender = Sender.USER,
                timestamp = "10:00 AM"
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatBubbleBotPreview() {
    ChatBotTheme {
        ChatBubble(
            message = Message(
                text = "Hello! I'm your banking assistant. How can I help you today?",
                sender = Sender.BOT,
                timestamp = "10:01 AM"
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
