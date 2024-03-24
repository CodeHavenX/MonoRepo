package com.codehavenx.platform.bot.service

import com.codehavenx.platform.bot.domain.models.StaffId
import com.codehavenx.platform.bot.domain.models.TimeSlot
import com.cramsan.framework.logging.logI
import kotlinx.datetime.LocalDateTime

class ReservationService(
    private val configurationService: ConfigurationService,
    private val calendarService: CalendarService,
) {

    suspend fun getAvailableTimes(
        configurationId: String,
        startDateTime: LocalDateTime,
        endDatetime: LocalDateTime,
        staffId: StaffId
    ): Map<LocalDateTime, List<TimeSlot>> {
        logI(TAG, "getAvailableTimes called")

        val configuration = configurationService.getAppointmentConfiguration(configurationId) ?: TODO()

        return calendarService.getAvailableTimeSlots(
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
