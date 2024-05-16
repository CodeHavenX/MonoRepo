package com.cramsan.edifikana.client.android.db.models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TimeCardRecordDao {
    @Query("SELECT * FROM TimeCardRecordEntity")
    suspend fun getAll(): List<TimeCardRecordEntity>

    @Query("SELECT * FROM TimeCardRecordEntity WHERE employeeDocumentId = :employeeDocumentId")
    suspend fun getAll(employeeDocumentId: String): List<TimeCardRecordEntity>

    @Query("SELECT * FROM TimeCardRecordEntity WHERE id = :id")
    suspend fun get(id: String): TimeCardRecordEntity?

    @Insert
    suspend fun insert(timeCardRecordEntity: TimeCardRecordEntity)

    @Delete
    suspend fun delete(timeCardRecordEntity: TimeCardRecordEntity)
}
