package com.cramsan.edifikana.client.lib.db.models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FileAttachmentDao {
    @Query("SELECT * FROM FileAttachmentEntity")
    suspend fun getAll(): List<FileAttachmentEntity>

    @Insert
    suspend fun insert(fileAttachmentEntity: FileAttachmentEntity)

    @Delete
    suspend fun delete(fileAttachmentEntity: FileAttachmentEntity)

    @Query("SELECT COUNT(id) FROM FileAttachmentEntity")
    suspend fun getCount(): Int
}
