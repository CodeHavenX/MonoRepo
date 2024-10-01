package com.cramsan.edifikana.client.lib.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cramsan.edifikana.lib.EventLogRecordPK
import com.cramsan.framework.core.CoreUri
import kotlinx.datetime.Clock

/**
 * Database model for representing a [EventLogRecord]
 */
@Entity
data class FileAttachmentEntity(
    @PrimaryKey
    val id: String,
    val eventLogRecordPK: String?,
    val fileUri: String? = null,
) {
    companion object {

        /**
         * Create a new [FileAttachmentEntity] with the given [eventLogRecordPK], [clock], and [fileUri]
         */
        fun create(
            eventLogRecordPK: EventLogRecordPK,
            clock: Clock,
            fileUri: CoreUri,
        ): FileAttachmentEntity {
            val id = "$eventLogRecordPK-$fileUri-${clock.now().epochSeconds}"
            return FileAttachmentEntity(
                id,
                eventLogRecordPK.documentPath,
                fileUri.toString(),
            )
        }
    }
}
