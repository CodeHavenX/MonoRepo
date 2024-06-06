package com.cramsan.edifikana.client.lib.db.models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EventLogRecordDao {
    @Query("SELECT * FROM EventLogRecordEntity")
    suspend fun getAll(): List<EventLogRecordEntity>

    @Query("SELECT * FROM EventLogRecordEntity WHERE id = :id")
    suspend fun get(id: String): EventLogRecordEntity?

    @Insert
    suspend fun insert(timeCardRecordEntity: EventLogRecordEntity)

    @Delete
    suspend fun delete(timeCardRecordEntity: EventLogRecordEntity)

    @Query("SELECT COUNT(id) FROM EventLogRecordEntity")
    suspend fun getCount(): Int
}
