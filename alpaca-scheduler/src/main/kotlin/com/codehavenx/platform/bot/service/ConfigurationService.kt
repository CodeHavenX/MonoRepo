package com.codehavenx.platform.bot.service

import com.codehavenx.platform.bot.domain.models.AppointmentConfiguration
import com.codehavenx.platform.bot.domain.models.AppointmentType

class ConfigurationService {

    suspend fun getAppointmentConfiguration(appointmentType: AppointmentType): AppointmentConfiguration {
        TODO()
    }

    companion object {
        private const val TAG = "ConfigurationService"
    }
}
