package com.codehavenx.alpaca.backend.core.repository

import com.codehavenx.alpaca.backend.core.service.models.AppointmentConfiguration
import com.codehavenx.alpaca.backend.core.service.models.requests.CreateConfigurationRequest
import com.codehavenx.alpaca.backend.core.service.models.requests.GetConfigurationRequest

/**
 * Interface for interacting with the configuration database.
 */
interface ConfigurationDatabase {
    /**
     * Creates an appointment configuration.
     */
    suspend fun createConfiguration(
        request: CreateConfigurationRequest,
    ): Result<AppointmentConfiguration>

    /**
     * Get an appointment configuration by its ID.
     */
    suspend fun getConfiguration(
        request: GetConfigurationRequest,
    ): Result<AppointmentConfiguration?>
}
