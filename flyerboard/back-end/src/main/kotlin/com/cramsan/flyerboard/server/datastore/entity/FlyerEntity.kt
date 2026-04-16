package com.cramsan.flyerboard.server.datastore.entity

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.service.models.Flyer
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Supabase entity representing a row in the `flyers` table.
 */
@Serializable
@SupabaseModel
data class FlyerEntity(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("file_path")
    val filePath: String,
    @SerialName("status")
    val status: String,
    @SerialName("expires_at")
    val expiresAt: Instant? = null,
    @SerialName("uploader_id")
    val uploaderId: String,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("updated_at")
    val updatedAt: Instant,
) {
    companion object {
        const val COLLECTION = "flyers"
    }

    /**
     * Entity for inserting a new flyer row. Excludes server-generated fields (id, status, created_at, updated_at).
     */
    @Serializable
    @SupabaseModel
    data class CreateFlyerEntity(
        @SerialName("title")
        val title: String,
        @SerialName("description")
        val description: String,
        @SerialName("file_path")
        val filePath: String,
        @SerialName("uploader_id")
        val uploaderId: String,
        @SerialName("expires_at")
        val expiresAt: Instant? = null,
    )
}

/**
 * Maps a [FlyerEntity] to the [Flyer] domain model.
 */
@OptIn(SupabaseModel::class)
fun FlyerEntity.toFlyer(): Flyer = Flyer(
    id = FlyerId(id),
    title = title,
    description = description,
    filePath = filePath,
    status = FlyerStatus.valueOf(status.uppercase()),
    expiresAt = expiresAt,
    uploaderId = UserId(uploaderId),
    createdAt = createdAt,
    updatedAt = updatedAt,
)
