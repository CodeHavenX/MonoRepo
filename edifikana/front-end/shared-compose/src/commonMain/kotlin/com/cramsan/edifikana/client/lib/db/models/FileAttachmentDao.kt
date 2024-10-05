package com.cramsan.edifikana.client.lib.db.models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * Data access object for file attachments.
 */
@Dao
interface FileAttachmentDao {

    /**
     * Get all file attachments.
     */
    @Query("SELECT * FROM FileAttachmentEntity")
    suspend fun getAll(): List<FileAttachmentEntity>

    /**
     * Get a specific file attachment.
     */
    @Insert
    suspend fun insert(fileAttachmentEntity: FileAttachmentEntity)

    /**
     * Delete a file attachment.
     */
    @Delete
    suspend fun delete(fileAttachmentEntity: FileAttachmentEntity)

    /**
     * Get the count of file attachments.
     */
    @Query("SELECT COUNT(id) FROM FileAttachmentEntity")
    suspend fun getCount(): Int
}
