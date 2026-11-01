package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "avatars")
data class AvatarEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val imageUrl: String,
    val prompt: String,
    val styleTitle: String,
    val framing: String,
    val lighting: String,
    val aspectRatio: String = "9:16",
    val width: Int = 720,
    val height: Int = 1280,
    val seed: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
