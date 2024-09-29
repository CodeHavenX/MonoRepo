package com.cramsan.edifikana.server.core.repository.dummy

import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.server.core.repository.TimeCardDatabase
import com.cramsan.edifikana.server.core.service.models.StaffId
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.core.service.models.TimeCardEventId
import com.cramsan.edifikana.server.core.service.models.requests.CreateTimeCardEventRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventListRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventRequest
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

class DummyTimeCardDatabase(
    private val clock: Clock,
) : TimeCardDatabase {
    override suspend fun createTimeCardEvent(request: CreateTimeCardEventRequest): Result<TimeCardEvent> {
        delay(1000)
        return Result.success(
            TimeCardEvent(
                id = TimeCardEventId("1"),
                staffId = request.staffId,
                type = request.eventType,
                time = clock.now(),
            )
        )
    }

    override suspend fun getTimeCardEvent(request: GetTimeCardEventRequest): Result<TimeCardEvent?> {
        delay(1000)
        return Result.success(
            TimeCardEvent(
                id = TimeCardEventId("1"),
                staffId = StaffId("1"),
                type = TimeCardEventType.CLOCK_IN,
                time = clock.now(),
            )
        )
    }

    override suspend fun getTimeCardEvents(request: GetTimeCardEventListRequest): Result<List<TimeCardEvent>> {
        delay(1000)
        return Result.success(
            (0..10).map {
                TimeCardEvent(
                    id = TimeCardEventId(it.toString()),
                    staffId = StaffId(it.toString()),
                    type = TimeCardEventType.CLOCK_IN,
                    time = clock.now(),
                )
            }
        )
    }
}