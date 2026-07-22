package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

data class ProfileWithDetails(
    val profile: ConnectionProfileEntity,
    val prompts: List<ProfilePromptEntity>,
    val traits: List<ProfileTraitEntity>
)

@Dao
interface PresenceDao {
    @Query("SELECT * FROM profiles WHERE isPassed = 0")
    fun getAllActiveProfiles(): Flow<List<ConnectionProfileEntity>>

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: String): ConnectionProfileEntity?

    @Query("SELECT * FROM profile_prompts WHERE profileId = :profileId")
    fun getPromptsForProfile(profileId: String): Flow<List<ProfilePromptEntity>>

    @Query("SELECT * FROM profile_prompts WHERE profileId = :profileId")
    suspend fun getPromptsListForProfile(profileId: String): List<ProfilePromptEntity>

    @Query("SELECT * FROM profile_traits WHERE profileId = :profileId")
    fun getTraitsForProfile(profileId: String): Flow<List<ProfileTraitEntity>>

    @Query("SELECT * FROM profile_traits WHERE profileId = :profileId")
    suspend fun getTraitsListForProfile(profileId: String): List<ProfileTraitEntity>

    @Query("SELECT * FROM profiles WHERE isLiked = 1 OR isSaved = 1")
    fun getInnerCircleProfiles(): Flow<List<ConnectionProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<ConnectionProfileEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompts(prompts: List<ProfilePromptEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTraits(traits: List<ProfileTraitEntity>)

    @Query("UPDATE profiles SET isLiked = :isLiked WHERE id = :profileId")
    suspend fun updateLikeStatus(profileId: String, isLiked: Boolean)

    @Query("UPDATE profiles SET isSaved = :isSaved WHERE id = :profileId")
    suspend fun updateSaveStatus(profileId: String, isSaved: Boolean)

    @Query("UPDATE profiles SET isPassed = 1 WHERE id = :profileId")
    suspend fun passProfile(profileId: String)

    @Query("SELECT * FROM messages WHERE profileId = :profileId ORDER BY timestamp ASC")
    fun getMessagesForProfile(profileId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT COUNT(*) FROM profiles")
    suspend fun getProfileCount(): Int
}
