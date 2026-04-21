package com.cramsan.flyerboard.server.datastore.impl

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.datastore.FlyerDatastore
import com.cramsan.flyerboard.server.datastore.PagedResult
import com.cramsan.flyerboard.server.datastore.entity.FlyerEntity
import com.cramsan.flyerboard.server.datastore.entity.toFlyer
import com.cramsan.flyerboard.server.service.models.Flyer
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order
import kotlin.time.Instant

/**
 * Supabase implementation of [FlyerDatastore].
 */
@OptIn(SupabaseModel::class)
class SupabaseFlyerDatastore(
    private val postgrest: Postgrest,
) : FlyerDatastore {

    override suspend fun createFlyer(
        title: String,
        description: String,
        filePath: String,
        uploaderId: UserId,
        expiresAt: Instant?,
    ): Result<Flyer> = runSuspendCatching(TAG) {
        logD(TAG, "Creating flyer: %s", title)
        val entity = FlyerEntity.CreateFlyerEntity(
            title = title,
            description = description,
            filePath = filePath,
            uploaderId = uploaderId.userId,
            expiresAt = expiresAt,
        )
        postgrest.from(FlyerEntity.COLLECTION).insert(entity) {
            select()
        }.decodeSingle<FlyerEntity>().toFlyer()
    }

    override suspend fun getFlyer(id: FlyerId): Result<Flyer?> = runSuspendCatching(TAG) {
        logD(TAG, "Getting flyer: %s", id)
        postgrest.from(FlyerEntity.COLLECTION).select {
            filter {
                FlyerEntity::id eq id.flyerId
            }
        }.decodeSingleOrNull<FlyerEntity>()?.toFlyer()
    }

    override suspend fun listFlyers(
        status: FlyerStatus?,
        query: String?,
        offset: Int,
        limit: Int,
    ): Result<PagedResult<Flyer>> = runSuspendCatching(TAG) {
        logD(TAG, "Listing flyers, status=%s query=%s offset=%d limit=%d", status, query, offset, limit)
        val result = postgrest.from(FlyerEntity.COLLECTION).select {
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
    }

    override suspend fun updateFlyer(
        id: FlyerId,
        title: String?,
        description: String?,
        filePath: String?,
        status: FlyerStatus?,
        expiresAt: Instant?,
    ): Result<Flyer> = runSuspendCatching(TAG) {
        logD(TAG, "Updating flyer: %s", id)
        postgrest.from(FlyerEntity.COLLECTION).update({
            title?.let { FlyerEntity::title setTo it }
            description?.let { FlyerEntity::description setTo it }
            filePath?.let { FlyerEntity::filePath setTo it }
            status?.let { FlyerEntity::status setTo it.name.lowercase() }
            expiresAt?.let { FlyerEntity::expiresAt setTo it }
        }) {
            select()
            filter {
                FlyerEntity::id eq id.flyerId
            }
        }.decodeSingle<FlyerEntity>().toFlyer()
    }

    override suspend fun listExpiredFlyers(now: Instant): Result<List<Flyer>> = runSuspendCatching(TAG) {
        logD(TAG, "Listing expired flyers at: %s", now)
        postgrest.from(FlyerEntity.COLLECTION).select {
            filter {
                FlyerEntity::status eq FlyerStatus.APPROVED.name.lowercase()
                FlyerEntity::expiresAt lt now.toString()
            }
            order("created_at", order = Order.DESCENDING)
        }.decodeList<FlyerEntity>().map { it.toFlyer() }
    }

    override suspend fun listFlyersByUploader(
        uploaderId: UserId,
        offset: Int,
        limit: Int,
    ): Result<PagedResult<Flyer>> = runSuspendCatching(TAG) {
        logD(TAG, "Listing flyers by uploader: %s", uploaderId)
        val result = postgrest.from(FlyerEntity.COLLECTION).select {
            count(Count.EXACT)
            filter {
                FlyerEntity::uploaderId eq uploaderId.userId
            }
            order("created_at", order = Order.DESCENDING)
            range(from = offset.toLong(), to = (offset + limit - 1).toLong())
        }
        val items = result.decodeList<FlyerEntity>().map { it.toFlyer() }
        PagedResult(items = items, total = result.countOrNull() ?: items.size.toLong())
    }

    companion object {
        private const val TAG = "SupabaseFlyerDatastore"
    }
}
