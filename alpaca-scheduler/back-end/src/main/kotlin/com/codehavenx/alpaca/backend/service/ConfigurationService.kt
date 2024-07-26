package com.codehavenx.alpaca.backend.service

import com.codehavenx.alpaca.backend.models.AppointmentConfiguration
import com.codehavenx.alpaca.backend.models.AppointmentType
import com.codehavenx.alpaca.backend.storage.ConfigurationDatabase
import kotlinx.datetime.TimeZone
import kotlin.time.Duration

class ConfigurationService(
    private val configurationDatabase: ConfigurationDatabase,
) {

    suspend fun createAppointmentConfiguration(
        appointmentType: AppointmentType,
        duration: Duration,
        timeZone: TimeZone,
    ) {
        configurationDatabase.createConfiguration(
            appointmentType = appointmentType,
            duration = duration,
            timeZone = timeZone,
        )
    }
    suspend fun getAppointmentConfiguration(configurationId: String): AppointmentConfiguration? {
        return configurationDatabase.getConfiguration(configurationId)
    }

    suspend fun getAllAppointmentConfigurations(): List<AppointmentConfiguration> {
        return configurationDatabase.getAllConfiguration()
    }

    suspend fun deleteAppointmentConfiguration(appointmentId: String): Boolean {
        return configurationDatabase.deleteConfiguration(appointmentId)
    }

    companion object {
        @Suppress("UnusedPrivateProperty")
        private const val TAG = "ConfigurationService"
    }
}
