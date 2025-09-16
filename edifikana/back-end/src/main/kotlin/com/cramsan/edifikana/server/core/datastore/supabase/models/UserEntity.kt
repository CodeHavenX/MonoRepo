package com.cramsan.edifikana.server.core.datastore.supabase.models

import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase entity representing a user.
 */
@Serializable
@SupabaseModel
data class UserEntity(
    val id: String,
    val email: String,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("auth_metadata")
    val authMetadata: AuthMetadataEntity,
) {
    companion object {
        const val COLLECTION = "users"
    }

    /**
     * Supabase entity representing a create user request.
     */
    @Serializable
    @SupabaseModel
    data class CreateUserEntity(
        val id: String,
        val email: String,
        @SerialName("phone_number")
        val phoneNumber: String,
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
@SupabaseModel
data class AuthMetadataEntity(
    @SerialName("pending_association")
    val pendingAssociation: Boolean = true,
    @SerialName("can_password_auth")
    val canPasswordAuth: Boolean = false,
    @SerialName("hashed_password")
    val hashedPassword: String? = null,
)
