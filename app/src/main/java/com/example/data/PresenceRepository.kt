package com.example.data

import kotlinx.coroutines.flow.Flow

class PresenceRepository(private val dao: PresenceDao) {

    val activeProfiles: Flow<List<ConnectionProfileEntity>> = dao.getAllActiveProfiles()
    val innerCircleProfiles: Flow<List<ConnectionProfileEntity>> = dao.getInnerCircleProfiles()

    fun getPrompts(profileId: String): Flow<List<ProfilePromptEntity>> = dao.getPromptsForProfile(profileId)
    suspend fun getPromptsList(profileId: String): List<ProfilePromptEntity> = dao.getPromptsListForProfile(profileId)

    fun getTraits(profileId: String): Flow<List<ProfileTraitEntity>> = dao.getTraitsForProfile(profileId)
    suspend fun getTraitsList(profileId: String): List<ProfileTraitEntity> = dao.getTraitsListForProfile(profileId)

    fun getMessages(profileId: String): Flow<List<MessageEntity>> = dao.getMessagesForProfile(profileId)

    suspend fun expressInterest(profileId: String) {
        dao.updateLikeStatus(profileId, true)
    }

    suspend fun saveProfile(profileId: String, isSaved: Boolean) {
        dao.updateSaveStatus(profileId, isSaved)
    }

    suspend fun passProfile(profileId: String) {
        dao.passProfile(profileId)
    }

    suspend fun sendMessage(profileId: String, senderName: String, text: String, referencedPrompt: String? = null) {
        val message = MessageEntity(
            profileId = profileId,
            senderName = senderName,
            messageText = text,
            isFromUser = true,
            referencedPrompt = referencedPrompt
        )
        dao.insertMessage(message)
        // Mark as liked / active connection when user sends a thoughtful note or message
        dao.updateLikeStatus(profileId, true)
    }

    suspend fun prepopulateSeedData() {
        if (dao.getProfileCount() > 0) return

        val profiles = listOf(
            ConnectionProfileEntity(
                id = "p1",
                name = "Eleanor Vance",
                age = 29,
                location = "San Francisco, CA",
                occupation = "Architectural Historian & Curator",
                bio = "Interested in quiet corners, timber architecture, vintage espresso machines, and conversations that stretch past midnight.",
                compatibilityScore = 96,
                imageResName = "img_profile_1_1784722323703",
                isSaved = false,
                isLiked = false,
                isPassed = false,
                coreValues = "Authenticity, Intentional Depth, Intellectual Curiosity, Stillness",
                loveLanguages = "Quality Time, Words of Affirmation",
                communicationRhythm = "Unhurried & Thoughtful (1-2 reflective messages daily)",
                relationshipIntent = "Marriage-minded • Slow dating"
            ),
            ConnectionProfileEntity(
                id = "p2",
                name = "Julian Thorne",
                age = 32,
                location = "Brooklyn, NY",
                occupation = "Acoustic Designer & Pianist",
                bio = "Designing soundscapes for quiet reflection. Looking for someone who values presence over performance and quiet joy over chaos.",
                compatibilityScore = 91,
                imageResName = "img_profile_2_1784722340707",
                isSaved = true,
                isLiked = true,
                isPassed = false,
                coreValues = "Creativity, Emotional Clarity, Mutual Growth, Harmony",
                loveLanguages = "Shared Experiences, Physical Touch",
                communicationRhythm = "Present in the Evening, Deep In-Person Gatherings",
                relationshipIntent = "Serious relationship • Introvert-friendly dating"
            ),
            ConnectionProfileEntity(
                id = "p3",
                name = "Clara Solloway",
                age = 28,
                location = "Seattle, WA",
                occupation = "Botanical Researcher & Author",
                bio = "Studying alpine flora and writing essays on slow living. My ideal morning begins with dark roast and a book that makes me pause.",
                compatibilityScore = 94,
                imageResName = "img_profile_3_1784722357876",
                isSaved = false,
                isLiked = false,
                isPassed = false,
                coreValues = "Mindfulness, Vulnerability, Generosity, Nature Resonance",
                loveLanguages = "Acts of Service, Quality Time",
                communicationRhythm = "Reflective Notes & Morning Checking In",
                relationshipIntent = "Slow dating • LGBTQ+ relationships"
            ),
            ConnectionProfileEntity(
                id = "p4",
                name = "Marcus Sterling",
                age = 35,
                location = "Austin, TX",
                occupation = "Landscape Architect & Craftsman",
                bio = "Rebuilding life with gratitude and clear priorities. Seeking a genuine partnership built on open communication and calm strength.",
                compatibilityScore = 89,
                imageResName = "img_profile_2_1784722340707",
                isSaved = false,
                isLiked = false,
                isPassed = false,
                coreValues = "Resilience, Open Communication, Stability, Mutual Respect",
                loveLanguages = "Acts of Service, Words of Affirmation",
                communicationRhythm = "Steady & Direct",
                relationshipIntent = "Dating after divorce • Serious relationship"
            ),
            ConnectionProfileEntity(
                id = "p5",
                name = "Aria Chen",
                age = 27,
                location = "Portland, OR",
                occupation = "Ceramics Artist & Audio Essayist",
                bio = "Creating functional pottery and exploring ambient sound. I enjoy unhurried tea rituals and intentional human connection.",
                compatibilityScore = 93,
                imageResName = "img_profile_1_1784722323703",
                isSaved = false,
                isLiked = false,
                isPassed = false,
                coreValues = "Artistic Freedom, Introvert Harmony, Presence, Curiosity",
                loveLanguages = "Quality Time, Gift Giving",
                communicationRhythm = "Low pressure & Deep when present",
                relationshipIntent = "Casual but respectful • Introvert-friendly dating"
            )
        )

        val prompts = listOf(
            // Eleanor
            ProfilePromptEntity(
                profileId = "p1",
                question = "The standard I hold myself to in love...",
                answer = "To offer an anchor without demanding a leash. Love should feel like coming home to yourself, only wider."
            ),
            ProfilePromptEntity(
                profileId = "p1",
                question = "A non-negotiable rhythm in my week...",
                answer = "Sunday afternoons spent listening to vinyl without checking my phone once."
            ),
            ProfilePromptEntity(
                profileId = "p1",
                question = "The deepest compliment you could give me...",
                answer = "Telling me that being around me makes you feel calmer and more honest."
            ),

            // Julian
            ProfilePromptEntity(
                profileId = "p2",
                question = "A question that changed my perspective on life...",
                answer = "Are you responding to who they are, or who you need them to be in this moment?"
            ),
            ProfilePromptEntity(
                profileId = "p2",
                question = "My definition of an intentional date...",
                answer = "A dusk walk through a quiet gallery followed by tea where neither of us feels rushed to fill the silences."
            ),

            // Clara
            ProfilePromptEntity(
                profileId = "p3",
                question = "What emotional maturity looks like to me...",
                answer = "Being able to say 'I need a moment to understand what I'm feeling' instead of reacting out of discomfort."
            ),
            ProfilePromptEntity(
                profileId = "p3",
                question = "A small detail I notice about people immediately...",
                answer = "How gently they speak when they disagree, and whether they listen to understand rather than respond."
            )
        )

        val traits = listOf(
            // Eleanor
            ProfileTraitEntity(profileId = "p1", label = "Acoustic Vinyl"),
            ProfileTraitEntity(profileId = "p1", label = "Midcentury Modern"),
            ProfileTraitEntity(profileId = "p1", label = "Intentional Living"),
            ProfileTraitEntity(profileId = "p1", label = "Deep Conversation"),
            ProfileTraitEntity(profileId = "p1", label = "Espresso Chemistry"),

            // Julian
            ProfileTraitEntity(profileId = "p2", label = "Neoclassical Piano"),
            ProfileTraitEntity(profileId = "p2", label = "Soundscape Design"),
            ProfileTraitEntity(profileId = "p2", label = "Quiet Walks"),
            ProfileTraitEntity(profileId = "p2", label = "Emotional Clarity"),

            // Clara
            ProfileTraitEntity(profileId = "p3", label = "Alpine Botany"),
            ProfileTraitEntity(profileId = "p3", label = "Essay Writing"),
            ProfileTraitEntity(profileId = "p3", label = "Morning Tea Ritual"),
            ProfileTraitEntity(profileId = "p3", label = "Vulnerability First")
        )

        val initialMessages = listOf(
            MessageEntity(
                profileId = "p2",
                senderName = "Julian Thorne",
                messageText = "Your perspective on architectural history really resonated with me. Sound and structure share so much of the same rhythm.",
                timestamp = System.currentTimeMillis() - 86400000 * 2,
                isFromUser = false
            ),
            MessageEntity(
                profileId = "p2",
                senderName = "You",
                messageText = "I couldn't agree more Julian. Acoustics shape how we inhabit a space emotionally before we even notice the walls.",
                timestamp = System.currentTimeMillis() - 86400000,
                isFromUser = true,
                referencedPrompt = "Soundscape Design & Structural Chemistry"
            )
        )

        dao.insertProfiles(profiles)
        dao.insertPrompts(prompts)
        dao.insertTraits(traits)
        initialMessages.forEach { dao.insertMessage(it) }
    }
}
