@file:Suppress("MagicNumber")

package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.service.TimeCardService
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import kotlinx.datetime.Clock

/**
 * Dummy implementation of [TimeCardService] for testing purposes.
 */
class DummyTimeCardService : TimeCardService {
    override suspend fun getRecords(staffPK: StaffId): Result<List<TimeCardRecordModel>> {
        return Result.success(
            (0..10).map {
                TimeCardRecordModel(
                    id = TimeCardEventId("$it"),
                    entityId = null,
                    staffPk = StaffId("$it"),
                    eventType = TimeCardEventType.CLOCK_IN,
                    eventTime = Clock.System.now().epochSeconds,
                    imageUrl = "",
                    imageRef = null,
                )
            }
        )
    }

    override suspend fun getAllRecords(): Result<List<TimeCardRecordModel>> {
        return Result.success(
            (0..10).map {
                TimeCardRecordModel(
                    id = TimeCardEventId("$it"),
                    entityId = null,
                    staffPk = StaffId("$it"),
                    eventType = TimeCardEventType.CLOCK_IN,
                    eventTime = Clock.System.now().epochSeconds,
                    imageUrl = "",
                    imageRef = null,
                )
            }
        )
    }

    override suspend fun getRecord(timeCardRecordPK: TimeCardEventId): Result<TimeCardRecordModel> {
        return Result.success(
            TimeCardRecordModel(
                id = TimeCardEventId("1"),
                entityId = null,
                staffPk = StaffId("1"),
                eventType = TimeCardEventType.CLOCK_IN,
                eventTime = Clock.System.now().epochSeconds,
                imageUrl = "",
                imageRef = null,
            )
        )
    }

    override suspend fun addRecord(timeCardRecord: TimeCardRecordModel): Result<TimeCardRecordModel> {
        return Result.success(
            TimeCardRecordModel(
                id = TimeCardEventId("1"),
                entityId = null,
                staffPk = StaffId("1"),
                eventType = TimeCardEventType.CLOCK_IN,
                eventTime = Clock.System.now().epochSeconds,
                imageUrl = "",
                imageRef = null,
            )
        )
    }
}
