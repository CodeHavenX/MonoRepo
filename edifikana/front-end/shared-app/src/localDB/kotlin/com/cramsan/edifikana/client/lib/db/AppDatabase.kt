package com.cramsan.edifikana.client.lib.db

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.TypeConverters
import com.cramsan.edifikana.client.lib.db.models.EventLogRecordDao
import com.cramsan.edifikana.client.lib.db.models.EventLogRecordEntity
import com.cramsan.edifikana.client.lib.db.models.FileAttachmentDao
import com.cramsan.edifikana.client.lib.db.models.FileAttachmentEntity
import com.cramsan.edifikana.client.lib.db.models.TimeCardRecordDao
import com.cramsan.edifikana.client.lib.db.models.TimeCardRecordEntity

/** Constructor used by Room to instantiate [AppDatabase] on non-Android platforms. */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>

/**
 * Room database for the application.
 */
@ConstructedBy(AppDatabaseConstructor::class)
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
