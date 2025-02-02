package com.cramsan.edifikana.server.core.repository.supabase.models

import com.cramsan.edifikana.server.core.repository.supabase.SupabaseModel
import kotlinx.serialization.Serializable

/**
 * Supabase entity representing a user.
 */
@Serializable
@SupabaseModel
data class UserEntity(
    val id: String,
    val email: String,
    val phone: String,
    val firstName: String,
    val lastName: String,
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
        val phone: String,
        val firstName: String,
        val lastName: String,
    )
}
