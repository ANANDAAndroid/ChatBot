package com.example.chatbot.presentation.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatbot.R
import com.example.chatbot.core.theme.ActionGreen
import com.example.chatbot.core.theme.BackgroundLight
import com.example.chatbot.core.theme.CardIconBg
import com.example.chatbot.core.theme.ChatBotTheme
import com.example.chatbot.core.theme.DarkGray
import com.example.chatbot.core.theme.DeepGreen
import com.example.chatbot.core.theme.TextPrimary
import com.example.chatbot.core.theme.TextSecondary

data class ServiceItem(
    val title: String,
    val description: String,
    val actionText: String,
    val iconRes: Int,
)

@Composable
fun MenuScreen(
    modifier: Modifier = Modifier,
    onServiceClick: (ServiceItem) -> Unit = {}
) {
    val services = listOf(
        ServiceItem(
            title = stringResource(R.string.banking_assistance),
            description = stringResource(R.string.banking_description),
            actionText = stringResource(R.string.start_conversation),
            iconRes = R.drawable.ic_bank
        ),
        ServiceItem(
            title = stringResource(R.string.delivery_assistance),
            description = stringResource(R.string.delivery_description),
            actionText = stringResource(R.string.track_orders),
            iconRes = R.drawable.ic_truck
        ),
        ServiceItem(
            title = stringResource(R.string.shopping_assistant),
            description = stringResource(R.string.shopping_description),
            actionText = stringResource(R.string.personal_shopper),
            iconRes = R.drawable.ic_shopping_bag
        ),
        ServiceItem(
            title = stringResource(R.string.travel_booking),
            description = stringResource(R.string.travel_description),
            actionText = stringResource(R.string.book_trip),
            iconRes = R.drawable.ic_airplane
        )
    )

    MenuScreenContent(
        services = services,
        onServiceClick = onServiceClick,
        modifier = modifier
    )
}

@Composable
fun MenuScreenContent(
    services: List<ServiceItem>,
    onServiceClick: (ServiceItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.images),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(R.string.service_hub),
                style = MaterialTheme.typography.titleLarge,
                color = DeepGreen,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(R.string.how_can_i_help),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            lineHeight = 40.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.mint_flow_assistant_ready),
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        services.forEach { service ->
            ServiceCard(
                service = service,
                onClick = { onServiceClick(service) }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ServiceCard(
    service: ServiceItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardIconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(service.iconRes),
                    contentDescription = null,
                    tint = DeepGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = service.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = service.description,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = service.actionText,
                    style = MaterialTheme.typography.labelLarge,
                    color = ActionGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_forward),
                    contentDescription = null,
                    tint = ActionGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    ChatBotTheme {
        MenuScreen()
    }
}
