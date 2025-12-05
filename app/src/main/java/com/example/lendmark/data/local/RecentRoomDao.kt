package com.example.lendmark.data.local

import androidx.room.*

@Dao
interface RecentRoomDao {

    @Query("SELECT * FROM recent_rooms ORDER BY viewedAt DESC LIMIT 10")
    suspend fun getRecentRooms(): List<RecentRoomEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentRoom(room: RecentRoomEntity)

    @Query("DELETE FROM recent_rooms WHERE roomId NOT IN (SELECT roomId FROM recent_rooms ORDER BY viewedAt DESC LIMIT 10)")
    suspend fun trimRecentRooms()
}
