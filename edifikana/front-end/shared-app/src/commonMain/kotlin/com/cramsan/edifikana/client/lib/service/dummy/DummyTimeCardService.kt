package com.cramsan.edifikana.client.lib.service.dummy

import com.cramsan.edifikana.client.lib.models.TimeCardRecordModel
import com.cramsan.edifikana.client.lib.service.TimeCardService
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

/**
 * Dummy time card service implementation with hardcoded responses.
 */
class DummyTimeCardService : TimeCardService {

    override suspend fun getRecords(
        staffPK: StaffId,
    ): Result<List<TimeCardRecordModel>> {
        delay(1.seconds)
        return Result.success(
            listOf(
                TIME_CARD_EVENT_1,
                TIME_CARD_EVENT_2,
                TIME_CARD_EVENT_3,
                TIME_CARD_EVENT_4,
                TIME_CARD_EVENT_5,
            )
        )
    }

    override suspend fun getAllRecords(): Result<List<TimeCardRecordModel>> {
        delay(1.seconds)
        return Result.success(
            listOf(
                TIME_CARD_EVENT_1,
                TIME_CARD_EVENT_2,
                TIME_CARD_EVENT_3,
                TIME_CARD_EVENT_4,
                TIME_CARD_EVENT_5,
            )
        )
    }

    override suspend fun getRecord(
        timeCardRecordPK: TimeCardEventId,
    ): Result<TimeCardRecordModel> {
        delay(1.seconds)
        return Result.success(TIME_CARD_EVENT_1)
    }

    override suspend fun addRecord(
        timeCardRecord: TimeCardRecordModel,
    ): Result<TimeCardRecordModel> {
        delay(1.seconds)
        return Result.success(TIME_CARD_EVENT_1)
    }
}
