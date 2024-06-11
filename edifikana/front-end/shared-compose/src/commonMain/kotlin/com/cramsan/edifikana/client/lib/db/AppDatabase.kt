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

@Database(
    entities = [
        TimeCardRecordEntity::class,
        EventLogRecordEntity::class,
        FileAttachmentEntity::class,
    ],
    version = 4,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timeCardRecordDao(): TimeCardRecordDao
    abstract fun eventLogRecordDao(): EventLogRecordDao
    abstract fun fileAttachmentDao(): FileAttachmentDao
}
