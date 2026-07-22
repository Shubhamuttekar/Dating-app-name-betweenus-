package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.ConnectionProfileEntity
import com.example.data.ProfilePromptEntity
import com.example.data.ProfileTraitEntity
import com.example.ui.PresenceViewModel
import com.example.ui.components.TrustOrbVisual
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.MidnightPrimary
import com.example.ui.theme.MutedRose
import com.example.ui.theme.MutedRoseAccent
import com.example.ui.theme.OnWarmCreamBackground
import com.example.ui.theme.OutlineSoft
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SoftLavenderContainer
import com.example.ui.theme.SoftLavenderLight
import com.example.ui.theme.SurfaceIvory
import com.example.ui.theme.WarmCreamBackground

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    viewModel: PresenceViewModel,
    modifier: Modifier = Modifier
) {
    val activeProfiles by viewModel.activeProfiles.collectAsState()
    val currentIndex by viewModel.currentProfileIndex.collectAsState()
    val prompts by viewModel.activePrompts.collectAsState()
    val traits by viewModel.activeTraits.collectAsState()

    var showSendNoteDialog by remember { mutableStateOf(false) }
    var selectedPromptQuestion by remember { mutableStateOf<String?>(null) }
    var noteText by remember { mutableStateOf("") }

    val currentProfile = if (activeProfiles.isNotEmpty() && currentIndex < activeProfiles.size) {
        activeProfiles[currentIndex]
    } else null

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCreamBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("discover_screen_container")
    ) {
        // Editorial Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Midnight Presence",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MidnightPrimary
                )
                Text(
                    text = "Curated for Intentional Depth",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnWarmCreamBackground.copy(alpha = 0.7f)
                )
            }

            if (currentProfile != null) {
                IconButton(
                    onClick = { viewModel.toggleSaveProfile(currentProfile) },
                    modifier = Modifier.testTag("save_profile_button")
                ) {
                    Icon(
                        imageVector = if (currentProfile.isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Bookmark profile",
                        tint = if (currentProfile.isSaved) MutedRose else MidnightPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Animated Trust Orb Visual Header Card
        TrustOrbVisual(
            heightDp = 170
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Relationship Intent Filter Bar
        val selectedFilter by viewModel.selectedIntentFilter.collectAsState()

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.FilterList,
                    contentDescription = null,
                    tint = SoftLavender,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "FILTER BY RELATIONSHIP INTENT",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnWarmCreamBackground.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("All") + PresenceViewModel.RELATIONSHIP_INTENTS
                filters.forEach { filter ->
                    val isSelected = filter == selectedFilter
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) MidnightPrimary else SurfaceIvory,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, OutlineSoft),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { viewModel.setIntentFilter(filter) }
                            .testTag("filter_chip_$filter")
                    ) {
                        Text(
                            text = filter,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) Color.White else MidnightPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (currentProfile != null) {
            // Main Editorial Profile Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceIvory),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, shape = RoundedCornerShape(24.dp), ambientColor = MidnightPrimary.copy(alpha = 0.1f))
                    .testTag("profile_card_${currentProfile.id}")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Portrait Photo with Compatibility Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        val context = LocalContext.current
                        val drawableResId = remember(currentProfile.imageResName) {
                            context.resources.getIdentifier(
                                currentProfile.imageResName,
                                "drawable",
                                context.packageName
                            )
                        }

                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(if (drawableResId != 0) drawableResId else currentProfile.imageResName)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile photo of ${currentProfile.name}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Gradient overlay at bottom
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.75f)
                                        ),
                                        startY = 180f
                                    )
                                )
                        )

                        // Compatibility Resonance Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MidnightDark.copy(alpha = 0.85f),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = SoftLavenderLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${currentProfile.compatibilityScore}% Resonance",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White
                                )
                            }
                        }

                        // Name & Details on bottom of photo
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "${currentProfile.name}, ${currentProfile.age}",
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Work,
                                    contentDescription = null,
                                    tint = SoftLavenderLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentProfile.occupation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = MutedRoseAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentProfile.location,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Bio Statement
                    Text(
                        text = currentProfile.bio,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MidnightPrimary,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Relationship Intention Badge / Card Section
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SoftLavenderContainer.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SoftLavender.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("relationship_intent_section")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = MutedRose,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "RELATIONSHIP INTENTIONS",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MidnightPrimary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val intents = currentProfile.relationshipIntent.split("•").map { it.trim() }
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                intents.forEach { intent ->
                                    val isMatch = selectedFilter != "All" && intent.equals(selectedFilter, ignoreCase = true)
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isMatch) MutedRose else MidnightPrimary,
                                        border = if (isMatch) androidx.compose.foundation.BorderStroke(2.dp, SoftLavenderLight) else null,
                                        modifier = Modifier.testTag("relationship_intent_chip_$intent")
                                    ) {
                                        Text(
                                            text = intent,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Interest & Trait Chips
                    if (traits.isNotEmpty()) {
                        Text(
                            text = "DEPTH & INTERESTS",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnWarmCreamBackground.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            traits.forEach { trait ->
                                Surface(
                                    shape = CircleShape,
                                    color = SoftLavenderContainer,
                                    modifier = Modifier.testTag("trait_chip_${trait.label}")
                                ) {
                                    Text(
                                        text = trait.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MidnightPrimary,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))
                    }

                    // Personality Prompts section (with Muted Rose 2dp left border & Playfair Display font)
                    if (prompts.isNotEmpty()) {
                        Text(
                            text = "PERSONALITY PROMPTS",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnWarmCreamBackground.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        prompts.forEach { prompt ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(WarmCreamBackground.copy(alpha = 0.5f))
                                    .clickable {
                                        selectedPromptQuestion = prompt.question
                                        showSendNoteDialog = true
                                    }
                                    .padding(16.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    // 2dp Left border line in Muted Rose
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(56.dp)
                                            .background(MutedRose, shape = RoundedCornerShape(2.dp))
                                    )

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = prompt.question,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontFamily = FontFamily.Serif,
                                                fontStyle = FontStyle.Italic
                                            ),
                                            color = SoftLavender
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = prompt.answer,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MidnightPrimary
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Tap to reply with a thoughtful note →",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MutedRose
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))
                    }

                    // Core Values & Intentional Alignment
                    Text(
                        text = "INTENTIONAL ALIGNMENT",
                        style = MaterialTheme.typography.labelMedium,
                        color = OnWarmCreamBackground.copy(alpha = 0.6f),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = WarmCreamBackground,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Core Values",
                                style = MaterialTheme.typography.labelLarge,
                                color = MidnightPrimary
                            )
                            Text(
                                text = currentProfile.coreValues,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MidnightPrimary.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Love Languages",
                                style = MaterialTheme.typography.labelLarge,
                                color = MidnightPrimary
                            )
                            Text(
                                text = currentProfile.loveLanguages,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MidnightPrimary.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Communication Rhythm",
                                style = MaterialTheme.typography.labelLarge,
                                color = MidnightPrimary
                            )
                            Text(
                                text = currentProfile.communicationRhythm,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MidnightPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Minimal Bottom Action Buttons Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Pass with Grace
                        IconButton(
                            onClick = { viewModel.passProfile(currentProfile) },
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(WarmCreamBackground)
                                .border(1.dp, MidnightPrimary.copy(alpha = 0.15f), CircleShape)
                                .testTag("pass_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Pass with grace",
                                tint = MidnightPrimary.copy(alpha = 0.7f)
                            )
                        }

                        // Send Thoughtful Note
                        Button(
                            onClick = {
                                selectedPromptQuestion = null
                                showSendNoteDialog = true
                            },
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SoftLavenderContainer,
                                contentColor = MidnightPrimary
                            ),
                            modifier = Modifier
                                .height(56.dp)
                                .weight(1f)
                                .padding(horizontal = 10.dp)
                                .testTag("note_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Send Note",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        // Express Interest
                        IconButton(
                            onClick = { viewModel.expressInterest(currentProfile) },
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MutedRose)
                                .testTag("express_interest_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Express interest",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        } else {
            // Empty State
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceIvory),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.FilterList,
                        contentDescription = null,
                        tint = SoftLavender,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (selectedFilter != "All") "No Matches for '$selectedFilter'" else "You've reviewed today's presence",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MidnightPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (selectedFilter != "All") 
                            "No active profiles currently match the '$selectedFilter' intention filter. Try resetting your filter to view all presence."
                        else 
                            "New intentional matches refresh daily at midnight. Visit your Sanctuary to revisit saved profiles.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnWarmCreamBackground.copy(alpha = 0.7f)
                    )

                    if (selectedFilter != "All") {
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.setIntentFilter("All") },
                            colors = ButtonDefaults.buttonColors(containerColor = MidnightPrimary),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("reset_filter_button")
                        ) {
                            Text("Show All Profiles")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Dialog for sending thoughtful note
    if (showSendNoteDialog && currentProfile != null) {
        Surface(
            color = Color.Black.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceIvory),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("send_note_dialog")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Send Thoughtful Note",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MidnightPrimary
                        )

                        Text(
                            text = "To ${currentProfile.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftLavender
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (selectedPromptQuestion != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = WarmCreamBackground,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Replying to: \"$selectedPromptQuestion\"",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontStyle = FontStyle.Italic
                                    ),
                                    color = MidnightPrimary,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            placeholder = {
                                Text(
                                    "Write something genuine and intentional...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = OnWarmCreamBackground.copy(alpha = 0.5f)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SoftLavender,
                                unfocusedBorderColor = OutlineSoft
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .testTag("note_input_field")
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    showSendNoteDialog = false
                                    noteText = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MidnightPrimary)
                            ) {
                                Text("Cancel")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    if (noteText.isNotBlank()) {
                                        viewModel.sendThoughtfulNote(
                                            profile = currentProfile,
                                            promptQuestion = selectedPromptQuestion ?: "Direct Note",
                                            noteText = noteText
                                        )
                                        showSendNoteDialog = false
                                        noteText = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MutedRose, contentColor = Color.White),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.testTag("submit_note_button")
                            ) {
                                Text("Send Note")
                            }
                        }
                    }
                }
            }
        }
    }
}
