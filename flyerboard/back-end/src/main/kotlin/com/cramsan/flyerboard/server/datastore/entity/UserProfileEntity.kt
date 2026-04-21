package com.cramsan.flyerboard.server.datastore.entity

import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.service.models.UserProfile
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Supabase entity representing a row in the `user_profiles` table.
 */
@Serializable
@SupabaseModel
data class UserProfileEntity(
    @SerialName("id")
    val id: String,
    @SerialName("role")
    val role: String,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("updated_at")
    val updatedAt: Instant,
) {
    companion object {
        const val COLLECTION = "user_profiles"
    }

    /**
     * Entity for inserting a new user profile row on signup.
     */
    @Serializable
    @SupabaseModel
    data class CreateUserProfileEntity(
        @SerialName("id")
        val id: String,
        @SerialName("role")
        val role: String = "user",
    )
}

/**
 * Maps a [UserProfileEntity] to the [UserProfile] domain model.
 */
@OptIn(SupabaseModel::class)
fun UserProfileEntity.toUserProfile(): UserProfile = UserProfile(
    id = UserId(id),
    role = UserRole.valueOf(role.uppercase()),
    createdAt = createdAt,
    updatedAt = updatedAt,
)
