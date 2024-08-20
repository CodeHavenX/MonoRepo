package com.codehavenx.alpaca.backend.core.storage.supabase.models

import com.codehavenx.alpaca.backend.core.storage.supabase.SupabaseModel
import kotlinx.serialization.Serializable

/**
 * Supabase entity representing a user.
 */
@Serializable
@SupabaseModel
data class UserEntity(
    val id: String,
    val username: String,
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
    )
}
