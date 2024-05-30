package com.codehavenx.platform.bot.storage

import com.codehavenx.platform.bot.domain.models.AppointmentConfiguration
import com.codehavenx.platform.bot.domain.models.AppointmentType
import com.codehavenx.platform.bot.storage.entity.ConfigurationEntity
import com.cramsan.framework.logging.logD
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.TimeZone
import org.bson.types.ObjectId
import kotlin.time.Duration

class ConfigurationDatabase(
    private val database: MongoDatabase,
    private val objectIdProvider: () -> ObjectId,
) {

    private val collection = database.getCollection<ConfigurationEntity>(COLLECTION_NAME)

    suspend fun createConfiguration(
        appointmentType: AppointmentType,
        duration: Duration,
        timeZone: TimeZone,
    ): AppointmentConfiguration {
        logD(TAG, "Creating configuration")
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
    }

    suspend fun getConfiguration(
        configurationId: String,
    ): AppointmentConfiguration? {
        logD(TAG, "Getting configuration: %S", configurationId)
        return collection
            .find(Filters.eq("_id", ObjectId(configurationId)))
            .firstOrNull()?.toConfiguration()
    }

    suspend fun getAllConfiguration(): List<AppointmentConfiguration> {
        logD(TAG, "Getting all configuration")
        return collection.find().map { it.toConfiguration() }.toList()
    }

    suspend fun updateConfiguration(configuration: AppointmentConfiguration): Boolean {
        logD(TAG, "Updating configuration: %S", configuration.id)
        val updatedConfiguration = configuration.toConfigurationEntity()
        return collection.replaceOne(
            Filters.eq("_id", updatedConfiguration.id),
            updatedConfiguration,
        ).wasAcknowledged()
    }

    suspend fun deleteConfiguration(configurationId: String): Boolean {
        logD(TAG, "Deleting configuration: %S", configurationId)
        return collection.deleteOne(
            Filters.eq("_id", ObjectId(configurationId)),
        ).wasAcknowledged()
    }

    companion object {
        private const val TAG = "ConfigurationDatabase"
        private const val COLLECTION_NAME = "Configurations"
    }
}
