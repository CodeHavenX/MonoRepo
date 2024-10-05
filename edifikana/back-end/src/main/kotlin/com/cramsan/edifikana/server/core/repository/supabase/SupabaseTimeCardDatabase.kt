package com.cramsan.edifikana.server.core.repository.supabase

import com.cramsan.edifikana.server.core.repository.TimeCardDatabase
import com.cramsan.edifikana.server.core.repository.supabase.models.TimeCardEventEntity
import com.cramsan.edifikana.server.core.service.models.TimeCardEvent
import com.cramsan.edifikana.server.core.service.models.requests.CreateTimeCardEventRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventListRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventRequest
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Database for managing time card events.
 */
class SupabaseTimeCardDatabase(
    private val postgrest: Postgrest,
) : TimeCardDatabase {

    /**
     * Creates a new time card event for the given [request]. Returns the [Result] of the operation with the created [TimeCardEvent].
     */
    @OptIn(SupabaseModel::class)
    override suspend fun createTimeCardEvent(
        request: CreateTimeCardEventRequest,
    ): Result<TimeCardEvent> = runSuspendCatching(TAG) {
        logD(TAG, "Creating time card event: %S", request.type)
        val requestEntity: TimeCardEventEntity.CreateTimeCardEventEntity = request.toTimeCardEventEntity()

        val createdTimeCardEvent = postgrest.from(TimeCardEventEntity.COLLECTION).insert(requestEntity) {
            select()
        }.decodeSingle<TimeCardEventEntity>()
        logD(TAG, "Time card event created eventId: %S", createdTimeCardEvent.id)
        createdTimeCardEvent.toTimeCardEvent()
    }

    /**
     * Retrieves a time card event for the given [request]. Returns the [Result] of the operation with the fetched [TimeCardEvent] if found.
     */
    @OptIn(SupabaseModel::class)
    override suspend fun getTimeCardEvent(
        request: GetTimeCardEventRequest,
    ): Result<TimeCardEvent?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting time card event: %S", request.id)

        val timeCardEventEntity = postgrest.from(TimeCardEventEntity.COLLECTION).select {
            filter {
                TimeCardEventEntity::id eq request.id
            }
            limit(1)
            single()
        }.decodeAsOrNull<TimeCardEventEntity>()

        timeCardEventEntity?.toTimeCardEvent()
    }

    @OptIn(SupabaseModel::class)
    override suspend fun getTimeCardEvents(
        request: GetTimeCardEventListRequest,
    ): Result<List<TimeCardEvent>> = runSuspendCatching(TAG) {
        logD(TAG, "Getting all time card events")

        postgrest.from(TimeCardEventEntity.COLLECTION).select {
            select()
        }.decodeList<TimeCardEventEntity>().map { it.toTimeCardEvent() }
    }

    companion object {
        const val TAG = "SupabaseTimeCardDatabase"
    }
}
