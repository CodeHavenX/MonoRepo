package com.cramsan.edifikana.server.core.repository.supabase.models

import com.cramsan.edifikana.server.core.repository.supabase.SupabaseModel
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
    val authMetadata: AuthMetadata,
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
        val authMetadata: AuthMetadata,
    )
}

/**
 * Metadata for user authentication.
 * This is used to store additional information about the user's authentication capabilities.
 */
@Serializable
@SupabaseModel
data class AuthMetadata(
    @SerialName("pending_association")
    val pendingAssociation: Boolean = false,
    @SerialName("can_password_auth")
    val canPasswordAuth: Boolean = false,
)
