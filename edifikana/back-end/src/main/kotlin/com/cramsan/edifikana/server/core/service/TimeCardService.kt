package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.server.core.repository.TimeCardDatabase
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.core.service.models.requests.CreateTimeCardEventRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteTimeCardEventRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventListRequest
import com.cramsan.framework.logging.logD
import kotlinx.datetime.Instant

/**
 * Service for time card operations.
 */
class TimeCardService(
    private val timeCardDatabase: TimeCardDatabase,
) {

    /**
     * Creates a time card event with the provided parameters.
     */
    suspend fun createTimeCardEvent(
        staffId: StaffId,
        fallbackStaffName: String?,
        propertyId: PropertyId,
        type: TimeCardEventType,
        imageUrl: String?,
        timestamp: Instant,
    ): TimeCardEvent {
        logD(TAG, "createTimeCardEvent")
        return timeCardDatabase.createTimeCardEvent(
            request = CreateTimeCardEventRequest(
                staffId = staffId,
                fallbackStaffName = fallbackStaffName,
                propertyId = propertyId,
                type = type,
                imageUrl = imageUrl,
                timestamp = timestamp,
            ),
        ).getOrThrow()
    }

    /**
     * Retrieves a time card event with the provided [id].
     */
    suspend fun getTimeCardEvent(
        id: TimeCardEventId,
    ): TimeCardEvent? {
        logD(TAG, "getTimeCardEvent")
        val timeCard = timeCardDatabase.getTimeCardEvent(
            request = DeleteTimeCardEventRequest(
                id = id,
            ),
        ).getOrNull()

        return timeCard
    }

    /**
     * Retrieves all time cards.
     */
    suspend fun getTimeCardEvents(
        staffId: StaffId?,
    ): List<TimeCardEvent> {
        logD(TAG, "getTimeCardEvents")
        val timeCards = timeCardDatabase.getTimeCardEvents(
            request = GetTimeCardEventListRequest(
                staffId = staffId,
            ),
        ).getOrThrow()
        return timeCards
    }

    companion object {
        private const val TAG = "TimeCardService"
    }
}
