package com.codehavenx.alpaca.backend.core.service

import com.codehavenx.alpaca.backend.core.service.models.AppointmentConfiguration
import com.codehavenx.alpaca.backend.core.service.models.AppointmentType
import com.codehavenx.alpaca.backend.core.storage.ConfigurationDatabase
import com.codehavenx.alpaca.backend.core.storage.requests.CreateConfigurationRequest
import com.codehavenx.alpaca.backend.core.storage.requests.GetConfigurationRequest
import kotlinx.datetime.TimeZone
import kotlin.time.Duration

/**
 * Service for managing appointment configurations.
 */
class ConfigurationService(
    private val configurationDatabase: ConfigurationDatabase,
) {

    /**
     * Creates an appointment configuration.
     */
    suspend fun createAppointmentConfiguration(
        name: String,
        appointmentType: AppointmentType,
        duration: Duration,
        timeZone: TimeZone,
    ): AppointmentConfiguration {
        val configurationEntity = CreateConfigurationRequest(
            name = name,
            appointmentType = AppointmentType(appointmentType.appointmentType),
            duration = duration,
            timeZone = timeZone,
        )

        return configurationDatabase.createConfiguration(
            configurationEntity,
        ).getOrThrow()
    }

    /**
     * Get an appointment configuration by its ID.
     */
    suspend fun getAppointmentConfiguration(configurationId: String): AppointmentConfiguration? {
        return configurationDatabase.getConfiguration(
            GetConfigurationRequest(configurationId),
        ).getOrNull()
    }
}
