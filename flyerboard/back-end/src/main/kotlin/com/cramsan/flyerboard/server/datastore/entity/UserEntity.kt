package com.cramsan.flyerboard.server.datastore.entity

import com.cramsan.framework.annotations.DatabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Supabase entity representing a row in the `users` table.
 */
@Serializable
@DatabaseModel
data class UserEntity(
    @SerialName("id")
    val id: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("updated_at")
    val updatedAt: Instant,
) {
    companion object {
        const val COLLECTION = "users"
    }

    /**
     * Entity for inserting a new user row at sign-up.
     */
    @Serializable
    @DatabaseModel
    data class CreateUserEntity(
        @SerialName("first_name")
        val firstName: String,
        @SerialName("last_name")
        val lastName: String,
    )
}
