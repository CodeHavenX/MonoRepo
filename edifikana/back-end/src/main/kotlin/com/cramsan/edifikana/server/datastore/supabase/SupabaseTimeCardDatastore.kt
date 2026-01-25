package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.server.datastore.TimeCardDatastore
import com.cramsan.edifikana.server.datastore.supabase.models.TimeCardEventEntity
import com.cramsan.edifikana.server.service.models.TimeCardEvent
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Datastore for managing time card events.
 */
class SupabaseTimeCardDatastore(
    private val postgrest: Postgrest,
    private val clock: Clock,
) : TimeCardDatastore {

    /**
     * Creates a new time card event for an employee clock-in/out.
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
        requireNotNull(createdTimeCardEvent.toTimeCardEvent())
    }

    /**
     * Retrieves a time card event by [id]. Returns the [TimeCardEvent] if found, null otherwise.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getTimeCardEvent(
        id: TimeCardEventId,
    ): Result<TimeCardEvent?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting time card event: %s", id)

        val timeCardEventEntity = postgrest.from(TimeCardEventEntity.COLLECTION).select {
            filter {
                TimeCardEventEntity::id eq id.timeCardEventId
                TimeCardEventEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<TimeCardEventEntity>()

        timeCardEventEntity?.toTimeCardEvent()
    }

    /**
     * Gets time card events, optionally filtered by [employeeId].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getTimeCardEvents(
        employeeId: EmployeeId?,
    ): Result<List<TimeCardEvent>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting all time card events")

        postgrest.from(TimeCardEventEntity.COLLECTION).select {
            filter {
                TimeCardEventEntity::deletedAt isExact null
                employeeId?.let {
                    TimeCardEventEntity::employeeId eq it.empId
                }
            }
            select()
        }.decodeList<TimeCardEventEntity>().mapNotNull { it.toTimeCardEvent() }
    }

    /**
     * Soft deletes a time card event by [id]. Returns true if successful.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun deleteTimeCardEvent(
        id: TimeCardEventId,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Soft deleting time card event: %s", id)

        postgrest.from(TimeCardEventEntity.COLLECTION).update({
            TimeCardEventEntity::deletedAt setTo clock.now()
        }) {
            select()
            filter {
                TimeCardEventEntity::id eq id.timeCardEventId
                TimeCardEventEntity::deletedAt isExact null
            }
        }.decodeSingleOrNull<TimeCardEventEntity>() != null
    }

    companion object {
        const val TAG = "SupabaseTimeCardDatastore"
    }
}
