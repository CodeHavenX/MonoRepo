package com.cramsan.edifikana.server.core.repository.dummy

import com.cramsan.edifikana.server.core.repository.TimeCardDatabase
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.core.service.models.requests.CreateTimeCardEventRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteTimeCardEventRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventListRequest
import com.cramsan.framework.logging.logD

/**
 * Class with dummy data to be used only for development and testing.
 */
class DummyTimeCardDatabase : TimeCardDatabase {
    override suspend fun createTimeCardEvent(request: CreateTimeCardEventRequest): Result<TimeCardEvent> {
        logD(TAG, "createTimeCardEvent")
        return Result.success(TIME_CARD_EVENT_1)
    }

    override suspend fun getTimeCardEvent(request: DeleteTimeCardEventRequest): Result<TimeCardEvent?> {
        logD(TAG, "getTimeCardEvent")
        return Result.success(TIME_CARD_EVENT_1)
    }

    override suspend fun getTimeCardEvents(request: GetTimeCardEventListRequest): Result<List<TimeCardEvent>> {
        val result = if (request.staffId == null) {
            listOf(TIME_CARD_EVENT_1, TIME_CARD_EVENT_2, TIME_CARD_EVENT_3, TIME_CARD_EVENT_4)
        } else {
            listOf(TIME_CARD_EVENT_1, TIME_CARD_EVENT_5)
        }

        return Result.success(result)
    }

    override suspend fun deleteTimeCardEvent(request: DeleteTimeCardEventRequest): Result<Boolean> {
        return Result.success(true)
    }

    companion object {
        private const val TAG = "DummyTimeCardDatabase"
    }
}
