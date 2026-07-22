package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ConnectionProfileEntity
import com.example.data.MessageEntity
import com.example.data.PresenceDatabase
import com.example.data.PresenceRepository
import com.example.data.ProfilePromptEntity
import com.example.data.ProfileTraitEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UserOwnProfile(
    val name: String = "Seraphina Vance",
    val age: Int = 28,
    val location: String = "San Francisco, CA",
    val occupation: String = "Editorial Designer & Essayist",
    val bio: String = "Seeking depth in an age of speed. I value quiet honesty, long discussions on design, and slow Sunday mornings.",
    val coreValues: String = "Vulnerability, Presence, High Standards, Intentionality",
    val loveLanguages: String = "Quality Time, Words of Affirmation",
    val communicationRhythm: String = "Unhurried & Reflective",
    val relationshipIntent: String = "Marriage-minded • Slow dating",
    val selectedIntents: List<String> = listOf("Marriage-minded", "Slow dating", "Serious relationship"),
    val prompts: List<Pair<String, String>> = listOf(
        "The standard I hold myself to in love..." to "To show up completely, without armor or performance.",
        "My definition of presence..." to "Being so anchored in the moment that the clock disappears."
    ),
    val traits: List<String> = listOf("Editorial Design", "Slow Living", "Philosophy", "Acoustic Jazz", "Matcha Rituals")
)

sealed interface UiEvent {
    data class ShowToast(val message: String) : UiEvent
    data class NavigateToChat(val profileId: String) : UiEvent
}

class PresenceViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        val RELATIONSHIP_INTENTS = listOf(
            "Serious relationship",
            "Marriage-minded",
            "Slow dating",
            "Casual but respectful",
            "LGBTQ+ relationships",
            "Dating after divorce",
            "Introvert-friendly dating"
        )
    }

    private val repository: PresenceRepository
    
    private val _currentProfileIndex = MutableStateFlow(0)
    val currentProfileIndex: StateFlow<Int> = _currentProfileIndex.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    val rawActiveProfiles: StateFlow<List<ConnectionProfileEntity>>
    val innerCircleProfiles: StateFlow<List<ConnectionProfileEntity>>

    private val _selectedIntentFilter = MutableStateFlow("All")
    val selectedIntentFilter: StateFlow<String> = _selectedIntentFilter.asStateFlow()

    val activeProfiles: StateFlow<List<ConnectionProfileEntity>>

    private val _userProfile = MutableStateFlow(UserOwnProfile())
    val userProfile: StateFlow<UserOwnProfile> = _userProfile.asStateFlow()

    private val _activePrompts = MutableStateFlow<List<ProfilePromptEntity>>(emptyList())
    val activePrompts: StateFlow<List<ProfilePromptEntity>> = _activePrompts.asStateFlow()

    private val _activeTraits = MutableStateFlow<List<ProfileTraitEntity>>(emptyList())
    val activeTraits: StateFlow<List<ProfileTraitEntity>> = _activeTraits.asStateFlow()

    init {
        val database = PresenceDatabase.getDatabase(application)
        repository = PresenceRepository(database.presenceDao())

        viewModelScope.launch {
            repository.prepopulateSeedData()
        }

        rawActiveProfiles = repository.activeProfiles.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        activeProfiles = combine(rawActiveProfiles, _selectedIntentFilter) { profiles, filter ->
            if (filter == "All") {
                profiles
            } else {
                profiles.filter { profile ->
                    profile.relationshipIntent.contains(filter, ignoreCase = true)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        innerCircleProfiles = repository.innerCircleProfiles.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Observe current profile to load prompts & traits dynamically
        viewModelScope.launch {
            combine(activeProfiles, _currentProfileIndex) { profiles, index ->
                if (profiles.isNotEmpty() && index < profiles.size) {
                    profiles[index]
                } else null
            }.collect { profile ->
                if (profile != null) {
                    _activePrompts.value = repository.getPromptsList(profile.id)
                    _activeTraits.value = repository.getTraitsList(profile.id)
                }
            }
        }
    }

    fun loadPromptsForProfile(profileId: String): StateFlow<List<ProfilePromptEntity>> {
        val flow = MutableStateFlow<List<ProfilePromptEntity>>(emptyList())
        viewModelScope.launch {
            flow.value = repository.getPromptsList(profileId)
        }
        return flow.asStateFlow()
    }

    fun loadTraitsForProfile(profileId: String): StateFlow<List<ProfileTraitEntity>> {
        val flow = MutableStateFlow<List<ProfileTraitEntity>>(emptyList())
        viewModelScope.launch {
            flow.value = repository.getTraitsList(profileId)
        }
        return flow.asStateFlow()
    }

    fun getMessagesForProfile(profileId: String): Flow<List<MessageEntity>> {
        return repository.getMessages(profileId)
    }

    fun expressInterest(profile: ConnectionProfileEntity) {
        viewModelScope.launch {
            repository.expressInterest(profile.id)
            _eventFlow.emit(UiEvent.ShowToast("Expressed Interest in ${profile.name}"))
            advanceToNextProfile()
        }
    }

    fun passProfile(profile: ConnectionProfileEntity) {
        viewModelScope.launch {
            repository.passProfile(profile.id)
            _eventFlow.emit(UiEvent.ShowToast("Passed with grace"))
            advanceToNextProfile()
        }
    }

    fun toggleSaveProfile(profile: ConnectionProfileEntity) {
        viewModelScope.launch {
            val newSaveState = !profile.isSaved
            repository.saveProfile(profile.id, newSaveState)
            val msg = if (newSaveState) "Added ${profile.name} to Sanctuary" else "Removed from Sanctuary"
            _eventFlow.emit(UiEvent.ShowToast(msg))
        }
    }

    fun sendThoughtfulNote(profile: ConnectionProfileEntity, promptQuestion: String, noteText: String) {
        viewModelScope.launch {
            if (noteText.isNotBlank()) {
                repository.sendMessage(
                    profileId = profile.id,
                    senderName = "You",
                    text = noteText,
                    referencedPrompt = promptQuestion
                )
                _eventFlow.emit(UiEvent.ShowToast("Thoughtful note sent to ${profile.name}"))
                _eventFlow.emit(UiEvent.NavigateToChat(profile.id))
            }
        }
    }

    fun sendMessage(profileId: String, text: String) {
        viewModelScope.launch {
            if (text.isNotBlank()) {
                repository.sendMessage(
                    profileId = profileId,
                    senderName = "You",
                    text = text
                )
            }
        }
    }

    fun setIntentFilter(filter: String) {
        _selectedIntentFilter.value = filter
        _currentProfileIndex.value = 0
    }

    fun updateUserProfile(updated: UserOwnProfile) {
        _userProfile.value = updated
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.ShowToast("Sanctuary Profile Saved"))
        }
    }

    private fun advanceToNextProfile() {
        val size = activeProfiles.value.size
        if (size > 0) {
            _currentProfileIndex.value = (_currentProfileIndex.value + 1) % size
        }
    }

    fun setCurrentProfileIndex(index: Int) {
        if (index >= 0 && index < activeProfiles.value.size) {
            _currentProfileIndex.value = index
        }
    }
}
