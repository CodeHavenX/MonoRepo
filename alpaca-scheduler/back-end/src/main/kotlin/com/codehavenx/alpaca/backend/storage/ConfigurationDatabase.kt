package com.codehavenx.alpaca.backend.storage

import com.codehavenx.alpaca.backend.models.AppointmentConfiguration
import com.codehavenx.alpaca.backend.models.AppointmentType
import com.cramsan.framework.logging.logD
import kotlinx.datetime.TimeZone
import kotlin.time.Duration

@Suppress("UnusedParameter", "UnusedPrivateProperty", "RedundantSuspendModifier")
class ConfigurationDatabase {

    suspend fun createConfiguration(
        appointmentType: AppointmentType,
        duration: Duration,
        timeZone: TimeZone,
    ): AppointmentConfiguration {
        logD(TAG, "Creating configuration")
        /*
        val configurationEntity = createConfigurationEntity(
            appointmentType = appointmentType,
            duration = duration,
            timeZone = timeZone,
            objectIdProvider = objectIdProvider,
        )
        val result = collection.insertOne(configurationEntity)
        logD(TAG, "Configuration %S, created = %S", configurationEntity.id, result.wasAcknowledged())
        if (result.wasAcknowledged()) {
            return configurationEntity.toConfiguration()
        } else {
            TODO()
        }
         */
        TODO()
    }

    suspend fun getConfiguration(
        configurationId: String,
    ): AppointmentConfiguration? {
        logD(TAG, "Getting configuration: %S", configurationId)
        /*
        return collection
            .find(Filters.eq("_id", ObjectId(configurationId)))
            .firstOrNull()?.toConfiguration()
         */
        TODO()
    }

    suspend fun getAllConfiguration(): List<AppointmentConfiguration> {
        logD(TAG, "Getting all configuration")
        // return collection.find().map { it.toConfiguration() }.toList()
        TODO()
    }

    suspend fun updateConfiguration(configuration: AppointmentConfiguration): Boolean {
        logD(TAG, "Updating configuration: %S", configuration.id)
        val updatedConfiguration = configuration.toConfigurationEntity()
        /*
        return collection.replaceOne(
            Filters.eq("_id", updatedConfiguration.id),
            updatedConfiguration,
        ).wasAcknowledged()
        */
        TODO()
    }

    suspend fun deleteConfiguration(configurationId: String): Boolean {
        logD(TAG, "Deleting configuration: %S", configurationId)
        /*
        return collection.deleteOne(
            Filters.eq("_id", ObjectId(configurationId)),
        ).wasAcknowledged()
        */
        TODO()
    }

    companion object {
        private const val TAG = "ConfigurationDatabase"
        private const val COLLECTION_NAME = "Configurations"
    }
}
