package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ConnectionProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val age: Int,
    val location: String,
    val occupation: String,
    val bio: String,
    val compatibilityScore: Int,
    val imageResName: String,
    val isSaved: Boolean = false,
    val isLiked: Boolean = false,
    val isPassed: Boolean = false,
    val coreValues: String, // Comma separated values
    val loveLanguages: String,
    val communicationRhythm: String,
    val relationshipIntent: String = "Serious relationship"
)

@Entity(tableName = "profile_prompts")
data class ProfilePromptEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val profileId: String,
    val question: String,
    val answer: String
)

@Entity(tableName = "profile_traits")
data class ProfileTraitEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val profileId: String,
    val label: String
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: String,
    val senderName: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromUser: Boolean,
    val referencedPrompt: String? = null
)
