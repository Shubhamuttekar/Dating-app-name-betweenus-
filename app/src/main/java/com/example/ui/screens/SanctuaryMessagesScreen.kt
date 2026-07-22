package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.ui.theme.OnWarmCreamBackground
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SoftLavenderContainer
import com.example.ui.theme.SurfaceIvory
import com.example.ui.theme.WarmCreamBackground

@Composable
fun SanctuaryMessagesScreen(
    viewModel: PresenceViewModel,
    onSelectProfileForChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val innerCircleProfiles by viewModel.innerCircleProfiles.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCreamBackground)
            .padding(20.dp)
            .testTag("messages_screen_container")
    ) {
        Text(
            text = "Intentional Conversations",
            style = MaterialTheme.typography.headlineLarge,
            color = MidnightPrimary
        )

        Text(
            text = "Dialogue rooted in vulnerability and depth",
            style = MaterialTheme.typography.bodyMedium,
            color = OnWarmCreamBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (innerCircleProfiles.isEmpty()) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceIvory),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Message,
                        contentDescription = null,
                        tint = SoftLavender,
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No active conversations yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = MidnightPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "When you express interest or send a thoughtful note on a profile prompt, your dialogue will appear here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnWarmCreamBackground.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(innerCircleProfiles) { profile ->
                    val messages by viewModel.getMessagesForProfile(profile.id).collectAsState(initial = emptyList<MessageEntity>())
                    val lastMessage = messages.lastOrNull()

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceIvory),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectProfileForChat(profile.id) }
                            .testTag("conversation_item_${profile.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                    .size(58.dp)
                                    .clip(CircleShape)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = profile.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MidnightPrimary
                                        )
                                        Text(
                                            text = profile.relationshipIntent,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SoftLavender
                                        )
                                    }

                                    Surface(
                                        shape = CircleShape,
                                        color = SoftLavenderContainer
                                    ) {
                                        Text(
                                            text = "${profile.compatibilityScore}% Resonance",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MidnightPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = lastMessage?.messageText ?: profile.bio,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnWarmCreamBackground.copy(alpha = 0.8f),
                                    maxLines = 2
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = "Open Chat",
                                tint = SoftLavender
                            )
                        }
                    }
                }
            }
        }
    }
}
