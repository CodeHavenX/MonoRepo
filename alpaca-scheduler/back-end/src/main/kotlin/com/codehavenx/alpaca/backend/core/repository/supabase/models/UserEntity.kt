package com.codehavenx.alpaca.backend.core.repository.supabase.models

import com.codehavenx.alpaca.backend.core.repository.supabase.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase entity representing a user.
 */
@Serializable
@SupabaseModel
data class UserEntity(
    val id: String,
    @SerialName("is_verified")
    val isVerified: Boolean = false,
    val username: String,
    @SerialName("phone_number")
    val phoneNumber: List<String>,
    val email: List<String>,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    val address: AddressEntity? = null,
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
        val username: String,
        val phoneNumber: List<String>,
        val email: List<String>
    )
}
