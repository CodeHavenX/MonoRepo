@file:Suppress("MagicNumber")

package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.service.TimeCardService
import com.cramsan.edifikana.lib.model.TimeCardEventType
import kotlinx.datetime.Clock

/**
 * Dummy implementation of [TimeCardService] for testing purposes.
 */
class DummyTimeCardService : TimeCardService {
    override suspend fun getRecords(staffPK: StaffPK): Result<List<TimeCardRecordModel>> {
        return Result.success(
            (0..10).map {
                TimeCardRecordModel(
                    id = TimeCardRecordPK("$it"),
                    entityId = null,
                    staffPk = StaffPK("$it"),
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
                    id = TimeCardRecordPK("$it"),
                    entityId = null,
                    staffPk = StaffPK("$it"),
                    eventType = TimeCardEventType.CLOCK_IN,
                    eventTime = Clock.System.now().epochSeconds,
                    imageUrl = "",
                    imageRef = null,
                )
            }
        )
    }

    override suspend fun getRecord(timeCardRecordPK: TimeCardRecordPK): Result<TimeCardRecordModel> {
        return Result.success(
            TimeCardRecordModel(
                id = TimeCardRecordPK("1"),
                entityId = null,
                staffPk = StaffPK("1"),
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
                id = TimeCardRecordPK("1"),
                entityId = null,
                staffPk = StaffPK("1"),
                eventType = TimeCardEventType.CLOCK_IN,
                eventTime = Clock.System.now().epochSeconds,
                imageUrl = "",
                imageRef = null,
            )
        )
    }
}
