package com.cramsan.edifikana.client.lib.db.models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * Data access object for event log records.
 */
@Dao
interface EventLogRecordDao {

    /**
     * Get all event log records.
     */
    @Query("SELECT * FROM EventLogRecordEntity")
    suspend fun getAll(): List<EventLogRecordEntity>

    /**
     * Get a specific event log record.
     */
    @Query("SELECT * FROM EventLogRecordEntity WHERE id = :id")
    suspend fun get(id: String): EventLogRecordEntity?

    /**
     * Insert a new event log record.
     */
    @Insert
    suspend fun insert(timeCardRecordEntity: EventLogRecordEntity)

    /**
     * Delete an event log record.
     */
    @Delete
    suspend fun delete(timeCardRecordEntity: EventLogRecordEntity)

    /**
     * Get the count of event log records.
     */
    @Query("SELECT COUNT(id) FROM EventLogRecordEntity")
    suspend fun getCount(): Int
}
