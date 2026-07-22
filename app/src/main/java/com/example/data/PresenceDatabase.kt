package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ConnectionProfileEntity::class,
        ProfilePromptEntity::class,
        ProfileTraitEntity::class,
        MessageEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class PresenceDatabase : RoomDatabase() {
    abstract fun presenceDao(): PresenceDao

    companion object {
        @Volatile
        private var INSTANCE: PresenceDatabase? = null

        fun getDatabase(context: Context): PresenceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PresenceDatabase::class.java,
                    "presence_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
