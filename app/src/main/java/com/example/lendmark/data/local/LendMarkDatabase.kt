package com.example.lendmark.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RecentRoomEntity::class], version = 1)
abstract class LendMarkDatabase : RoomDatabase() {
    abstract fun recentRoomDao(): RecentRoomDao
}
