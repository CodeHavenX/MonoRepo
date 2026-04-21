package com.cramsan.edifikana.client.lib.db.models

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query

/**
 * Data access object for time card records.
 */
@Dao
interface TimeCardRecordDao {

    /**
     * Get all time card records.
     */
    @Query("SELECT * FROM TimeCardRecordEntity")
    suspend fun getAll(): List<TimeCardRecordEntity>

    /**
     * Get all time card records for a specific employee document.
     */
    @Query("SELECT * FROM TimeCardRecordEntity WHERE employeeDocumentId = :employeeDocumentId")
    suspend fun getAll(employeeDocumentId: String): List<TimeCardRecordEntity>

    /**
     * Get a specific time card record.
     */
    @Query("SELECT * FROM TimeCardRecordEntity WHERE id = :id")
    suspend fun get(id: String): TimeCardRecordEntity?

    /**
     * Insert a new time card record.
     */
    @Insert
    suspend fun insert(timeCardRecordEntity: TimeCardRecordEntity)

    /**
     * Delete a time card record.
     */
    @Delete
    suspend fun delete(timeCardRecordEntity: TimeCardRecordEntity)
}
