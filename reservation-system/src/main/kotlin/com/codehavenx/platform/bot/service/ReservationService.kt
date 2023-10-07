package com.codehavenx.platform.bot.service

import com.codehavenx.platform.bot.domain.models.AppointmentType
import com.codehavenx.platform.bot.domain.models.TimeSlot
import com.codehavenx.platform.bot.domain.models.UserId
import kotlinx.datetime.DateTimePeriod

class ReservationService(
    private val configurationService: ConfigurationService,
    private val calendarService: CalendarService,
    private val notificationService: NotificationService,
) {

    suspend fun getAvailableTimeSlots(appointmentType: AppointmentType, period: DateTimePeriod, staff: UserId?): List<TimeSlot> {
        val configuration = configurationService.getAppointmentConfiguration(appointmentType)

        return calendarService.getAvailableTimeSlots(
            period,
            configuration.duration,
            staff,
        )
    }

    companion object {
        private const val TAG = "ReservationService"
    }
}
