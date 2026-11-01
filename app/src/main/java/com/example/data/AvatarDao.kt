package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.AvatarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AvatarDao {
    @Query("SELECT * FROM avatars ORDER BY createdAt DESC")
    fun getAllAvatars(): Flow<List<AvatarEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvatar(avatar: AvatarEntity)

    @Delete
    suspend fun deleteAvatar(avatar: AvatarEntity)

    @Query("DELETE FROM avatars")
    suspend fun clearAll()
}
