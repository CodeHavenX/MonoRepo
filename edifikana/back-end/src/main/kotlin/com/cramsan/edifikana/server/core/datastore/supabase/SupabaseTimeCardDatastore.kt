package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.server.core.datastore.TimeCardDatastore
import com.cramsan.edifikana.server.core.datastore.supabase.models.TimeCardEventEntity
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import kotlin.time.Instant

/**
 * Datastore for managing time card events.
 */
class SupabaseTimeCardDatastore(
    private val postgrest: Postgrest,
) : TimeCardDatastore {

    /**
     * Creates a new time card event for the given [request]. Returns the [Result] of the operation with the created [TimeCardEvent].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun createTimeCardEvent(
        employeeId: EmployeeId,
        fallbackEmployeeName: String?,
        propertyId: PropertyId,
        type: TimeCardEventType,
        imageUrl: String?,
        timestamp: Instant,
    ): Result<TimeCardEvent> = runSuspendCatching(TAG) {
        logD(TAG, "Creating time card event: %s", type)
        val requestEntity: TimeCardEventEntity.CreateTimeCardEventEntity = CreateTimeCardEventEntity(
            employeeId = employeeId,
            fallbackEmpName = fallbackEmployeeName,
            propertyId = propertyId,
            type = type,
            imageUrl = imageUrl,
            timestamp = timestamp
        )

        val createdTimeCardEvent = postgrest.from(TimeCardEventEntity.COLLECTION).insert(requestEntity) {
            select()
        }.decodeSingle<TimeCardEventEntity>()
        logD(TAG, "Time card event created eventId: %s", createdTimeCardEvent.id)
        createdTimeCardEvent.toTimeCardEvent()
    }

    /**
     * Retrieves a time card event for the given [request]. Returns the [Result] of the operation with the fetched [TimeCardEvent] if found.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getTimeCardEvent(
        id: TimeCardEventId,
    ): Result<TimeCardEvent?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting time card event: %s", id)

        val timeCardEventEntity = postgrest.from(TimeCardEventEntity.COLLECTION).select {
            filter {
                TimeCardEventEntity::id eq id.timeCardEventId
            }
        }.decodeSingleOrNull<TimeCardEventEntity>()

        timeCardEventEntity?.toTimeCardEvent()
    }

    @OptIn(SupabaseModel::class)
    override suspend fun getTimeCardEvents(
        employeeId: EmployeeId?,
    ): Result<List<TimeCardEvent>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting all time card events")

        postgrest.from(TimeCardEventEntity.COLLECTION).select {
            filter {
                employeeId?.let {
                    TimeCardEventEntity::employeeId eq it.empId
                }
            }
            select()
        }.decodeList<TimeCardEventEntity>().map { it.toTimeCardEvent() }
    }

    /**
     * Deletes a time card event for the given [request]. Returns the [Result] of the operation.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteTimeCardEvent(
        id: TimeCardEventId,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Deleting time card event: %s", id)

        postgrest.from(TimeCardEventEntity.COLLECTION).delete {
            select()
            filter {
                TimeCardEventEntity::id eq id.timeCardEventId
            }
        }.decodeSingleOrNull<TimeCardEventEntity>() != null
    }

    companion object {
        const val TAG = "SupabaseTimeCardDatastore"
    }
}
