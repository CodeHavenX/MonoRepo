@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.client.lib.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.utils.time.Chronos
import kotlin.time.ExperimentalTime

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
            eventLogRecordPK: String,
            fileUri: CoreUri,
        ): FileAttachmentEntity {
            val id = "$eventLogRecordPK-$fileUri-${Chronos.currentInstant().epochSeconds}"
            return FileAttachmentEntity(
                id,
                eventLogRecordPK,
                fileUri.toString(),
            )
        }
    }
}
