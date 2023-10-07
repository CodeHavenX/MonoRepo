package com.codehavenx.platform.bot.service

import com.codehavenx.platform.bot.domain.models.AppointmentConfiguration
import com.codehavenx.platform.bot.domain.models.TimeSlot
import com.codehavenx.platform.bot.domain.models.UserId
import kotlinx.datetime.DateTimePeriod
import kotlin.time.Duration

class CalendarService(
) {

    suspend fun getAvailableTimeSlots(period: DateTimePeriod, duration: Duration, staff: UserId?): List<TimeSlot> {
        TODO()
    }

    companion object {
        private const val TAG = "ConfigurationService"
    }
}
