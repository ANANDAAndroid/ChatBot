package com.example.chatbot.presentation.chat

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chatbot.domain.model.ChatOption
import com.example.chatbot.R
import com.example.chatbot.core.theme.BackgroundLight
import com.example.chatbot.core.theme.ChatBotTheme
import com.example.chatbot.core.component.ChatBubble
import com.example.chatbot.core.component.QuickReplyChip
import com.example.chatbot.core.theme.InputFieldGray
import com.example.chatbot.core.theme.OnlineGreen
import com.example.chatbot.core.theme.DeepGreen

@Composable
fun ChatScreen(
    serviceTitle: String,
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(serviceTitle) {
        viewModel.initChat(serviceTitle)
    }

    ChatScreenContent(
        serviceTitle = serviceTitle,
        uiState = uiState,
        onQuickReplyClick = { option ->
            if (option.text == "Exit") {
                onBackClick()
            } else {
                viewModel.onQuickReplyClicked(option)
            }
        },
        onInputTextChanged = { viewModel.onInputTextChanged(it) },
        onSendMessage = { viewModel.onSendMessage() },
        onBackClick = onBackClick
    )
}

@Composable
fun ChatScreenContent(
    serviceTitle: String,
    uiState: ChatUiState,
    onQuickReplyClick: (ChatOption) -> Unit,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    onBackClick: () -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size, uiState.isBotTyping) {
        if (uiState.messages.isNotEmpty() || uiState.isBotTyping) {
            val lastIndex = if (uiState.isBotTyping) {
                uiState.messages.size // The typing indicator item
            } else {
                uiState.messages.size - 1
            }
            listState.animateScrollToItem(lastIndex)
        }
    }

    Scaffold(
        topBar = { 
            ChatTopBar(
                title = serviceTitle,
                onBackClick = onBackClick
            ) 
        },
        bottomBar = {
            Column {
                if (uiState.quickReplies.isNotEmpty()) {
                    CustomFlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.quickReplies.forEach { option ->
                            QuickReplyChip(
                                text = option.text,
                                onClick = { onQuickReplyClick(option) }
                            )
                        }
                    }
                }
                ChatInputBar(
                    text = uiState.inputText,
                    onTextChanged = onInputTextChanged,
                    onSendClick = onSendMessage
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = DeepGreen
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(uiState.messages) { message ->
                    ChatBubble(message = message)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                if (uiState.isBotTyping) {
                    item {
                        TypingIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TypingIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "dots")
                
                val dot1Alpha by infiniteTransition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = 600
                            0.2f at 0
                            1f at 200
                            1f at 400
                            0.2f at 600
                        },
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "dot1"
                )
                
                val dot2Alpha by infiniteTransition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = 600
                            0.2f at 0
                            0.2f at 200
                            1f at 400
                            1f at 600
                        },
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "dot2"
                )
                
                val dot3Alpha by infiniteTransition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = 600
                            0.2f at 0
                            0.2f at 400
                            1f at 600
                        },
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "dot3"
                )

                Dot(alpha = dot1Alpha)
                Dot(alpha = dot2Alpha)
                Dot(alpha = dot3Alpha)
            }
        }
    }
}

@Composable
fun Dot(alpha: Float) {
    Box(
        modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(Color.Gray.copy(alpha = alpha))
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CustomFlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable FlowRowScope.() -> Unit
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    title: String,
    onBackClick: () -> Unit = {}
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.Black
                )
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                ) {
                    Image(
                        painter = painterResource(R.drawable.images),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = stringResource(R.string.online),
                        fontSize = 12.sp,
                        color = OnlineGreen
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
fun ChatInputBar(
    text: String,
    onTextChanged: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
    hint: String = stringResource(R.string.type_message_hint)
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = onTextChanged,
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp)),
            placeholder = {
                Text(
                    text = hint,
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = InputFieldGray,
                unfocusedContainerColor = InputFieldGray,
                disabledContainerColor = InputFieldGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = { onSendClick() }
            ),
            maxLines = 4
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onSendClick,
            enabled = text.trim().isNotEmpty(),
            modifier = Modifier
                .size(48.dp)
                .background(
                    if (text.trim().isNotEmpty()) DeepGreen else Color.Gray,
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = stringResource(R.string.send),
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatBotTheme {
        ChatScreenContent(
            serviceTitle = "Banking Assistance",
            uiState = ChatUiState(
                messages = emptyList(),
                quickReplies = listOf(
                    ChatOption("Account Balance", "account_balance"),
                    ChatOption("Mini Statement", "mini_statement"),
                    ChatOption("Debit Card Services", "debit_card"),
                    ChatOption("Loan Info", "loan_info")
                )
            ),
            onQuickReplyClick = {},
            onInputTextChanged = {},
            onSendMessage = {}
        ) {
        }
    }
}
