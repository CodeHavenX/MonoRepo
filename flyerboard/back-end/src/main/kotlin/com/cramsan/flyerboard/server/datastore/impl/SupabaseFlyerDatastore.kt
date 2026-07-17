package com.cramsan.flyerboard.server.datastore.impl

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.datastore.FlyerDatastore
import com.cramsan.flyerboard.server.datastore.PagedResult
import com.cramsan.flyerboard.server.datastore.entity.FlyerEntity
import com.cramsan.flyerboard.server.datastore.entity.toFlyer
import com.cramsan.flyerboard.server.service.models.Flyer
import com.cramsan.framework.annotations.BackendDatastore
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order
import kotlin.time.Instant

/**
 * Supabase implementation of [FlyerDatastore].
 */
@BackendDatastore
class SupabaseFlyerDatastore(private val postgrest: Postgrest) : FlyerDatastore {
    override suspend fun createFlyer(
        id: FlyerId,
        title: String,
        description: String,
        filePath: String,
        uploaderId: UserId,
        expiresAt: Instant?,
    ): Result<Flyer> =
        runSuspendCatching(TAG) {
            logD(TAG, "Creating flyer: %s", title)
            val entity =
                FlyerEntity.CreateFlyerEntity(
                    id = id.flyerId,
                    title = title,
                    description = description,
                    filePath = filePath,
                    uploaderId = uploaderId.userId,
                    expiresAt = expiresAt,
                )
            try {
                postgrest
                    .from(FlyerEntity.COLLECTION)
                    .insert(entity) {
                        select()
                    }.decodeSingle<FlyerEntity>()
                    .toFlyer()
            } catch (e: PostgrestRestException) {
                if (e.code == POSTGRES_FOREIGN_KEY_VIOLATION) {
                    // flyers.uploader_id references user_profiles(id) -- this fires when an
                    // authenticated caller who never completed POST /api/v1/user (or whose
                    // profile was since removed) tries to create a flyer.
                    throw ClientRequestExceptions.InvalidRequestException(
                        "Cannot create a flyer before completing signup (POST /api/v1/user)",
                        e,
                    )
                }
                throw e
            }
        }

    override suspend fun getFlyer(id: FlyerId): Result<Flyer?> =
        runSuspendCatching(TAG) {
            logD(TAG, "Getting flyer: %s", id)
            try {
                postgrest
                    .from(FlyerEntity.COLLECTION)
                    .select {
                        filter {
                            FlyerEntity::id eq id.flyerId
                        }
                    }.decodeSingleOrNull<FlyerEntity>()
                    ?.toFlyer()
            } catch (e: PostgrestRestException) {
                // The `id` column is a Postgres UUID; a value that isn't UUID-shaped can never
                // match a row, so this is equivalent to "not found", not a server error.
                if (e.code == POSTGRES_INVALID_TEXT_REPRESENTATION) null else throw e
            }
        }

    override suspend fun listFlyers(
        status: FlyerStatus?,
        query: String?,
        offset: Int,
        limit: Int,
    ): Result<PagedResult<Flyer>> =
        runSuspendCatching(TAG) {
            logD(TAG, "Listing flyers, status=%s query=%s offset=%d limit=%d", status, query, offset, limit)
            try {
                val result =
                    postgrest.from(FlyerEntity.COLLECTION).select {
                        count(Count.EXACT)
                        filter {
                            status?.let { FlyerEntity::status eq it.name.lowercase() }
                            query?.takeIf { it.isNotBlank() }?.let { q ->
                                or {
                                    FlyerEntity::title ilike "%$q%"
                                    FlyerEntity::description ilike "%$q%"
                                }
                            }
                        }
                        order("created_at", order = Order.DESCENDING)
                        range(from = offset.toLong(), to = (offset + limit - 1).toLong())
                    }
                val items = result.decodeList<FlyerEntity>().map { it.toFlyer() }
                PagedResult(items = items, total = result.countOrNull() ?: items.size.toLong())
            } catch (e: PostgrestRestException) {
                if (e.code != POSTGREST_RANGE_NOT_SATISFIABLE) throw e
                val total =
                    postgrest
                        .from(FlyerEntity.COLLECTION)
                        .select {
                            count(Count.EXACT)
                            filter {
                                status?.let { FlyerEntity::status eq it.name.lowercase() }
                                query?.takeIf { it.isNotBlank() }?.let { q ->
                                    or {
                                        FlyerEntity::title ilike "%$q%"
                                        FlyerEntity::description ilike "%$q%"
                                    }
                                }
                            }
                        }.countOrNull() ?: 0L
                PagedResult(items = emptyList(), total = total)
            }
        }

    override suspend fun updateFlyer(
        id: FlyerId,
        title: String?,
        description: String?,
        status: FlyerStatus?,
        expiresAt: Instant?,
        rejectionReason: String?,
    ): Result<Flyer> =
        runSuspendCatching(TAG) {
            logD(TAG, "Updating flyer: %s", id)
            postgrest
                .from(FlyerEntity.COLLECTION)
                .update({
                    title?.let { FlyerEntity::title setTo it }
                    description?.let { FlyerEntity::description setTo it }
                    status?.let { FlyerEntity::status setTo it.name.lowercase() }
                    expiresAt?.let { FlyerEntity::expiresAt setTo it }
                    rejectionReason?.let { FlyerEntity::rejectionReason setTo it }
                }) {
                    select()
                    filter {
                        FlyerEntity::id eq id.flyerId
                    }
                }.decodeSingle<FlyerEntity>()
                .toFlyer()
        }

    override suspend fun listExpiredFlyers(now: Instant): Result<List<Flyer>> =
        runSuspendCatching(TAG) {
            logD(TAG, "Listing expired flyers at: %s", now)
            postgrest
                .from(FlyerEntity.COLLECTION)
                .select {
                    filter {
                        FlyerEntity::status eq FlyerStatus.APPROVED.name.lowercase()
                        FlyerEntity::expiresAt lt now.toString()
                    }
                    order("created_at", order = Order.DESCENDING)
                }.decodeList<FlyerEntity>()
                .map { it.toFlyer() }
        }

    override suspend fun listFlyersByUploader(
        uploaderId: UserId,
        offset: Int,
        limit: Int,
    ): Result<PagedResult<Flyer>> =
        runSuspendCatching(TAG) {
            logD(TAG, "Listing flyers by uploader: %s", uploaderId)
            try {
                val result =
                    postgrest.from(FlyerEntity.COLLECTION).select {
                        count(Count.EXACT)
                        filter {
                            FlyerEntity::uploaderId eq uploaderId.userId
                        }
                        order("created_at", order = Order.DESCENDING)
                        range(from = offset.toLong(), to = (offset + limit - 1).toLong())
                    }
                val items = result.decodeList<FlyerEntity>().map { it.toFlyer() }
                PagedResult(items = items, total = result.countOrNull() ?: items.size.toLong())
            } catch (e: PostgrestRestException) {
                if (e.code != POSTGREST_RANGE_NOT_SATISFIABLE) throw e
                val total =
                    postgrest
                        .from(FlyerEntity.COLLECTION)
                        .select {
                            count(Count.EXACT)
                            filter {
                                FlyerEntity::uploaderId eq uploaderId.userId
                            }
                        }.countOrNull() ?: 0L
                PagedResult(items = emptyList(), total = total)
            }
        }

    companion object {
        private const val TAG = "SupabaseFlyerDatastore"

        // PostgREST's code for "Requested range not satisfiable" -- e.g. an offset past the end
        // of the result set, or a limit/offset combination that produces an invalid range. This
        // is a normal "there's nothing at this page" outcome, not a server error.
        private const val POSTGREST_RANGE_NOT_SATISFIABLE = "PGRST103"

        // Postgres error code 22P02: invalid_text_representation -- thrown when a value can't be
        // cast to the column's type (here, a non-UUID-shaped flyer id against a `uuid` column).
        private const val POSTGRES_INVALID_TEXT_REPRESENTATION = "22P02"

        // Postgres error code 23503: foreign_key_violation.
        private const val POSTGRES_FOREIGN_KEY_VIOLATION = "23503"
    }
}
