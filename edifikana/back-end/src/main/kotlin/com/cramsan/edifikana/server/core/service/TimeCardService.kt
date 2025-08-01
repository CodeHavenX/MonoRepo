package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.server.core.datastore.TimeCardDatastore
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.core.service.models.requests.CreateTimeCardEventRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventListRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventRequest
import com.cramsan.framework.logging.logD
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Service for time card operations.
 */
@OptIn(ExperimentalTime::class)
class TimeCardService(
    private val timeCardDatastore: TimeCardDatastore,
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
        return timeCardDatastore.createTimeCardEvent(
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
        val timeCard = timeCardDatastore.getTimeCardEvent(
            request = GetTimeCardEventRequest(
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
        val timeCards = timeCardDatastore.getTimeCardEvents(
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
