package com.example.lendmark.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_rooms")
data class RecentRoomEntity(
    @PrimaryKey val roomId: String,
    val buildingId: String,
    val roomName: String,
    val viewedAt: Long
)
