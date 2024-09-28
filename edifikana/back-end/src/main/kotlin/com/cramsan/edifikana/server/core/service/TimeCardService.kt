package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.server.core.repository.TimeCardDatabase
import com.cramsan.edifikana.server.core.service.models.StaffId
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.core.service.models.TimeCardEventId
import com.cramsan.edifikana.server.core.service.models.requests.CreateTimeCardEventRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventListRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventRequest

/**
 * Service for time card operations.
 */
class TimeCardService(
    private val timeCardDatabase: TimeCardDatabase,
) {

    /**
     * Creates a time card with the provided [hours].
     */
    suspend fun createTimeCard(
        staffId: StaffId,
        eventType: TimeCardEventType,
    ): TimeCardEvent {
        return timeCardDatabase.createTimeCardEvent(
            request = CreateTimeCardEventRequest(
                staffId = staffId,
                eventType = eventType,
            ),
        ).getOrThrow()
    }

    /**
     * Retrieves a time card with the provided [id].
     */
    suspend fun getTimeCard(
        id: TimeCardEventId,
    ): TimeCardEvent? {
        val timeCard = timeCardDatabase.getTimeCardEvent(
            request = GetTimeCardEventRequest(
                id = id,
            ),
        ).getOrNull()

        return timeCard
    }

    /**
     * Retrieves all time cards.
     */
    suspend fun getTimeCards(): List<TimeCardEvent> {
        val timeCards = timeCardDatabase.getTimeCardEvents(
            request = GetTimeCardEventListRequest(
                staffId = null,
            ),
        ).getOrThrow()
        return timeCards
    }
}
