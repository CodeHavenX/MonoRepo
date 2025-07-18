package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.server.core.datastore.TimeCardDatastore
import com.cramsan.edifikana.server.core.datastore.supabase.models.TimeCardEventEntity
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.core.service.models.requests.CreateTimeCardEventRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteTimeCardEventRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventListRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventRequest
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest

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
        request: CreateTimeCardEventRequest,
    ): Result<TimeCardEvent> = runSuspendCatching(TAG) {
        logD(TAG, "Creating time card event: %s", request.type)
        val requestEntity: TimeCardEventEntity.CreateTimeCardEventEntity = request.toTimeCardEventEntity()

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
        request: GetTimeCardEventRequest,
    ): Result<TimeCardEvent?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting time card event: %s", request.id)

        val timeCardEventEntity = postgrest.from(TimeCardEventEntity.COLLECTION).select {
            filter {
                TimeCardEventEntity::id eq request.id.timeCardEventId
            }
        }.decodeSingleOrNull<TimeCardEventEntity>()

        timeCardEventEntity?.toTimeCardEvent()
    }

    @OptIn(SupabaseModel::class)
    override suspend fun getTimeCardEvents(
        request: GetTimeCardEventListRequest,
    ): Result<List<TimeCardEvent>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting all time card events")

        postgrest.from(TimeCardEventEntity.COLLECTION).select {
            filter {
                request.staffId?.let {
                    TimeCardEventEntity::staffId eq it.staffId
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
        request: DeleteTimeCardEventRequest,
    ): Result<Boolean> = runSuspendCatching(TAG) {
        logD(TAG, "Deleting time card event: %s", request.id)

        postgrest.from(TimeCardEventEntity.COLLECTION).delete {
            select()
            filter {
                TimeCardEventEntity::id eq request.id.timeCardEventId
            }
        }.decodeSingleOrNull<TimeCardEventEntity>() != null
    }

    companion object {
        const val TAG = "SupabaseTimeCardDatastore"
    }
}
