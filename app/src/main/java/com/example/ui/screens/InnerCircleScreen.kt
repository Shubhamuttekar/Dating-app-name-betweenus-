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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.PresenceViewModel
import com.example.ui.theme.MidnightPrimary
import com.example.ui.theme.MutedRose
import com.example.ui.theme.OnWarmCreamBackground
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SoftLavenderContainer
import com.example.ui.theme.SurfaceIvory
import com.example.ui.theme.WarmCreamBackground

@Composable
fun InnerCircleScreen(
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
            .testTag("inner_circle_screen_container")
    ) {
        Text(
            text = "Inner Circle",
            style = MaterialTheme.typography.headlineLarge,
            color = MidnightPrimary
        )

        Text(
            text = "Saved connections & resonant matches",
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
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = null,
                        tint = SoftLavender,
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Your Inner Circle is empty",
                        style = MaterialTheme.typography.titleLarge,
                        color = MidnightPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tap the bookmark icon or express interest on a profile during your daily presence review to save them here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnWarmCreamBackground.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(innerCircleProfiles) { profile ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceIvory),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("inner_circle_card_${profile.id}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
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
                                        .size(64.dp)
                                        .clip(CircleShape)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${profile.name}, ${profile.age}",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MidnightPrimary
                                    )

                                    Text(
                                        text = profile.occupation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SoftLavender
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Surface(
                                        shape = CircleShape,
                                        color = SoftLavenderContainer
                                    ) {
                                        Text(
                                            text = "${profile.compatibilityScore}% Intentional Resonance",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MidnightPrimary,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.toggleSaveProfile(profile) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.BookmarkRemove,
                                        contentDescription = "Remove from saved",
                                        tint = MutedRose
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Relationship Intentions
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = MutedRose,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = profile.relationshipIntent,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MidnightPrimary,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = profile.bio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MidnightPrimary.copy(alpha = 0.9f)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MidnightPrimary,
                                    modifier = Modifier.clickable { onSelectProfileForChat(profile.id) }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Message,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Open Dialogue",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
