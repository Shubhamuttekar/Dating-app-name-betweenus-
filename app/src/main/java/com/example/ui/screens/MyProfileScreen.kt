package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PresenceViewModel
import com.example.ui.UserOwnProfile
import com.example.ui.theme.MidnightPrimary
import com.example.ui.theme.MutedRose
import com.example.ui.theme.OnWarmCreamBackground
import com.example.ui.theme.OutlineSoft
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SoftLavenderContainer
import com.example.ui.theme.SurfaceIvory
import com.example.ui.theme.WarmCreamBackground

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MyProfileScreen(
    viewModel: PresenceViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()

    var isEditing by remember { mutableStateOf(false) }

    var name by remember(userProfile) { mutableStateOf(userProfile.name) }
    var age by remember(userProfile) { mutableStateOf(userProfile.age.toString()) }
    var occupation by remember(userProfile) { mutableStateOf(userProfile.occupation) }
    var location by remember(userProfile) { mutableStateOf(userProfile.location) }
    var bio by remember(userProfile) { mutableStateOf(userProfile.bio) }
    var coreValues by remember(userProfile) { mutableStateOf(userProfile.coreValues) }
    var loveLanguages by remember(userProfile) { mutableStateOf(userProfile.loveLanguages) }
    var communicationRhythm by remember(userProfile) { mutableStateOf(userProfile.communicationRhythm) }
    var selectedIntents by remember(userProfile) { mutableStateOf(userProfile.selectedIntents) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCreamBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("my_profile_screen_container")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "My Sanctuary Profile",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MidnightPrimary
                )
                Text(
                    text = "How you present yourself to intentional matches",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnWarmCreamBackground.copy(alpha = 0.7f)
                )
            }

            Button(
                onClick = {
                    if (isEditing) {
                        viewModel.updateUserProfile(
                            userProfile.copy(
                                name = name,
                                age = age.toIntOrNull() ?: userProfile.age,
                                occupation = occupation,
                                location = location,
                                bio = bio,
                                coreValues = coreValues,
                                loveLanguages = loveLanguages,
                                communicationRhythm = communicationRhythm,
                                relationshipIntent = selectedIntents.joinToString(" • "),
                                selectedIntents = selectedIntents
                            )
                        )
                        isEditing = false
                    } else {
                        isEditing = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEditing) MutedRose else MidnightPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("edit_save_profile_button")
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Filled.Save else Icons.Filled.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isEditing) "Save" else "Edit")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Editorial Card Container
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceIvory),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header avatar area
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(SoftLavenderContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = MidnightPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    if (isEditing) {
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = age,
                                onValueChange = { age = it },
                                label = { Text("Age") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        Column {
                            Text(
                                text = "${userProfile.name}, ${userProfile.age}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MidnightPrimary
                            )
                            Text(
                                text = userProfile.occupation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = SoftLavender
                            )
                            Text(
                                text = userProfile.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = OnWarmCreamBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isEditing) {
                    OutlinedTextField(
                        value = occupation,
                        onValueChange = { occupation = it },
                        label = { Text("Occupation") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Personal Bio Statement") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SoftLavender),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                } else {
                    Text(
                        text = "PERSONAL BIO STATEMENT",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnWarmCreamBackground.copy(alpha = 0.6f),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = userProfile.bio,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MidnightPrimary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Relationship Intentions Section
                Text(
                    text = "MY RELATIONSHIP INTENTIONS",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnWarmCreamBackground.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PresenceViewModel.RELATIONSHIP_INTENTS.forEach { intentOption ->
                        val isSelected = selectedIntents.contains(intentOption)
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) MutedRose else SoftLavenderContainer.copy(alpha = 0.5f),
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, OutlineSoft),
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable(enabled = isEditing) {
                                    selectedIntents = if (isSelected) {
                                        selectedIntents - intentOption
                                    } else {
                                        selectedIntents + intentOption
                                    }
                                }
                                .testTag("my_intent_chip_$intentOption")
                        ) {
                            Text(
                                text = intentOption,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color.White else MidnightPrimary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Trait Chips
                Text(
                    text = "MY DEPTH & TRAITS",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnWarmCreamBackground.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    userProfile.traits.forEach { trait ->
                        Surface(
                            shape = CircleShape,
                            color = SoftLavenderContainer
                        ) {
                            Text(
                                text = trait,
                                style = MaterialTheme.typography.labelMedium,
                                color = MidnightPrimary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Prompts Preview
                Text(
                    text = "PERSONALITY PROMPTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnWarmCreamBackground.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                userProfile.prompts.forEach { (prompt, answer) ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(WarmCreamBackground)
                            .padding(16.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(48.dp)
                                    .background(MutedRose, shape = RoundedCornerShape(2.dp))
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = prompt,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontStyle = FontStyle.Italic
                                    ),
                                    color = SoftLavender
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = answer,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MidnightPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Intentional Alignment Preferences
                Text(
                    text = "INTENTIONAL ALIGNMENT",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnWarmCreamBackground.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isEditing) {
                    OutlinedTextField(
                        value = coreValues,
                        onValueChange = { coreValues = it },
                        label = { Text("Core Values") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = loveLanguages,
                        onValueChange = { loveLanguages = it },
                        label = { Text("Love Languages") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = communicationRhythm,
                        onValueChange = { communicationRhythm = it },
                        label = { Text("Communication Rhythm") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
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
                                text = userProfile.coreValues,
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
                                text = userProfile.loveLanguages,
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
                                text = userProfile.communicationRhythm,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MidnightPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
