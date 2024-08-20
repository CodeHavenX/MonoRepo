package com.codehavenx.alpaca.backend.core.service

import com.codehavenx.alpaca.backend.core.service.models.StaffId
import com.codehavenx.alpaca.backend.core.service.models.TimeSlot
import com.cramsan.framework.logging.logI
import kotlinx.datetime.LocalDateTime

/**
 * Service for managing reservations. This service is responsible for finding available times for
 * appointments. It uses the [ConfigurationService] to get the configuration for the appointment
 */
class ReservationService(
    private val configurationService: ConfigurationService,
    private val calendarService: CalendarService,
) {

    /**
     * Get the available times for a given staff member between the given start and end times.
     */
    suspend fun getAvailableTimes(
        configurationId: String,
        startDateTime: LocalDateTime,
        endDatetime: LocalDateTime,
        staffId: StaffId,
    ): Map<LocalDateTime, List<TimeSlot>> {
        logI(TAG, "getAvailableTimes called")

        val configuration = configurationService.getAppointmentConfiguration(configurationId) ?: TODO()

        return calendarService.getAvailableTimeSlotsForStaff(
            startDateTime,
            endDatetime,
            configuration.timeZone,
            configuration.duration,
            listOf(staffId),
        )
    }

    companion object {
        private const val TAG = "ReservationService"
    }
}
