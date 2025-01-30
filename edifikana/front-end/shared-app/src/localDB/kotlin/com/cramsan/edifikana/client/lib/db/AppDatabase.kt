package com.cramsan.edifikana.client.lib.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cramsan.edifikana.client.lib.db.models.EventLogRecordDao
import com.cramsan.edifikana.client.lib.db.models.EventLogRecordEntity
import com.cramsan.edifikana.client.lib.db.models.FileAttachmentDao
import com.cramsan.edifikana.client.lib.db.models.FileAttachmentEntity
import com.cramsan.edifikana.client.lib.db.models.TimeCardRecordDao
import com.cramsan.edifikana.client.lib.db.models.TimeCardRecordEntity

/**
 * Room database for the application.
 */
@Database(
    entities = [
        TimeCardRecordEntity::class,
        EventLogRecordEntity::class,
        FileAttachmentEntity::class,
    ],
    version = 1,
    exportSchema = true,
    autoMigrations = []
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Get the time card record DAO.
     */
    abstract fun timeCardRecordDao(): TimeCardRecordDao

    /**
     * Get the event log record DAO.
     */
    abstract fun eventLogRecordDao(): EventLogRecordDao

    /**
     * Get the file attachment DAO.
     */
    abstract fun fileAttachmentDao(): FileAttachmentDao
}
