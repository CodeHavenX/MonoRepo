package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.common.Email
import com.cramsan.edifikana.lib.model.common.PhoneNumber
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.DatabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Supabase entity representing a user.
 */
@Serializable
@DatabaseModel
data class UserEntity(
    val id: UserId,
    val email: Email,
    @SerialName("phone_number")
    val phoneNumber: PhoneNumber,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("auth_metadata")
    val authMetadata: AuthMetadataEntity,
    @SerialName("deleted_at")
    val deletedAt: Instant? = null,
) {
    companion object {
        const val COLLECTION = "users"
    }

    /**
     * Supabase entity representing a create user request.
     */
    @Serializable
    @DatabaseModel
    data class CreateUserEntity(
        val id: UserId,
        val email: Email,
        @SerialName("phone_number")
        val phoneNumber: PhoneNumber,
        @SerialName("first_name")
        val firstName: String,
        @SerialName("last_name")
        val lastName: String,
        @SerialName("auth_metadata")
        val authMetadata: AuthMetadataEntity,
    )
}

/**
 * Metadata for user authentication.
 * This is used to store additional information about the user's authentication capabilities.
 */
@Serializable
@DatabaseModel
data class AuthMetadataEntity(
    @SerialName("pending_association")
    val pendingAssociation: Boolean = true,
    @SerialName("can_password_auth")
    val canPasswordAuth: Boolean = false,
    @SerialName("hashed_password")
    val hashedPassword: String? = null,
)
