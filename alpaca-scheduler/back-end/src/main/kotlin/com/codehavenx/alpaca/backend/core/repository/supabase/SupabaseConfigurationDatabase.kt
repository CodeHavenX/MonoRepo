package com.codehavenx.alpaca.backend.core.repository.supabase

import com.codehavenx.alpaca.backend.core.repository.ConfigurationDatabase
import com.codehavenx.alpaca.backend.core.repository.supabase.models.ConfigurationEntity
import com.codehavenx.alpaca.backend.core.service.models.AppointmentConfiguration
import com.codehavenx.alpaca.backend.core.service.models.requests.CreateConfigurationRequest
import com.codehavenx.alpaca.backend.core.service.models.requests.GetConfigurationRequest
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Database for managing appointment configurations.
 */
class SupabaseConfigurationDatabase(
    private val postgrest: Postgrest,
) : ConfigurationDatabase {

    /**
     * Creates an appointment configuration.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun createConfiguration(
        request: CreateConfigurationRequest,
    ): Result<AppointmentConfiguration> = runSuspendCatching(TAG) {
        logD(TAG, "Creating configuration: %S", request.name)

        val requestEntity = request.toConfigurationEntity()

        val createdConfiguration = postgrest.from(ConfigurationEntity.COLLECTION).insert(requestEntity) {
            select()
        }
            .decodeSingle<ConfigurationEntity>()
        logD(TAG, "Configuration created id=%S", createdConfiguration.id)

        createdConfiguration.toConfiguration()
    }

    /**
     * Get an appointment configuration by its ID.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getConfiguration(
        request: GetConfigurationRequest,
    ): Result<AppointmentConfiguration?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting configuration: %S", request.id)

        postgrest.from(ConfigurationEntity.COLLECTION).select {
            filter {
                ConfigurationEntity::id eq request.id
            }
            limit(1)
            single()
        }.decodeAsOrNull<ConfigurationEntity>()?.toConfiguration()
    }

    companion object {
        const val TAG = "SupabaseConfigurationDatabase"
    }
}
