package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.AvatarEntity

@Database(entities = [AvatarEntity::class], version = 1, exportSchema = false)
abstract class LunaDatabase : RoomDatabase() {
    abstract fun avatarDao(): AvatarDao

    companion object {
        @Volatile
        private var INSTANCE: LunaDatabase? = null

        fun getDatabase(context: Context): LunaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LunaDatabase::class.java,
                    "luna_avatar_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
