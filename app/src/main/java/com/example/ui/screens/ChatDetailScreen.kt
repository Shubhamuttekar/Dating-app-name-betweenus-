package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.MessageEntity
import com.example.ui.PresenceViewModel
import com.example.ui.theme.MidnightPrimary
import com.example.ui.theme.MutedRose
import com.example.ui.theme.MutedRoseContainer
import com.example.ui.theme.OnWarmCreamBackground
import com.example.ui.theme.OutlineSoft
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SoftLavenderContainer
import com.example.ui.theme.SurfaceIvory
import com.example.ui.theme.WarmCreamBackground

@Composable
fun ChatDetailScreen(
    profileId: String,
    viewModel: PresenceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeProfiles by viewModel.activeProfiles.collectAsState()
    val innerProfiles by viewModel.innerCircleProfiles.collectAsState()
    val allProfiles = (activeProfiles + innerProfiles).distinctBy { it.id }

    val profile = allProfiles.find { it.id == profileId }
    val messages by viewModel.getMessagesForProfile(profileId).collectAsState(initial = emptyList<MessageEntity>())

    var textInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCreamBackground)
            .testTag("chat_detail_container")
    ) {
        // Chat Header
        Surface(
            color = SurfaceIvory,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("chat_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MidnightPrimary
                    )
                }

                if (profile != null) {
                    val context = LocalContext.current
                    val drawableResId = remember(profile.imageResName) {
                        context.resources.getIdentifier(
                            profile.imageResName,
                            "drawable",
                            context.packageName
                        )
                    }

                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(if (drawableResId != 0) drawableResId else profile.imageResName)
                            .crossfade(true)
                            .build(),
                        contentDescription = profile.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MidnightPrimary
                        )

                        Text(
                            text = "${profile.compatibilityScore}% Resonance • ${profile.location}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftLavender
                        )
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.isFromUser

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                ) {
                    if (msg.referencedPrompt != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SoftLavenderContainer,
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .fillMaxWidth(0.8f)
                        ) {
                            Text(
                                text = "Prompt: \"${msg.referencedPrompt}\"",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic
                                ),
                                color = MidnightPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isUser) 18.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 18.dp
                        ),
                        color = if (isUser) MidnightPrimary else SurfaceIvory,
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth(0.82f)
                    ) {
                        Text(
                            text = msg.messageText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isUser) Color.White else MidnightPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }

        // Input Field Bar
        Surface(
            color = SurfaceIvory,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = {
                        Text(
                            "Type a thoughtful reply...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnWarmCreamBackground.copy(alpha = 0.5f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SoftLavender,
                        unfocusedBorderColor = OutlineSoft
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendMessage(profileId, textInput)
                            textInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MutedRose)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
