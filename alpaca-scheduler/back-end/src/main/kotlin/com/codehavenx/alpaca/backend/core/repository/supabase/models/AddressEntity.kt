package com.codehavenx.alpaca.backend.core.repository.supabase.models

import com.codehavenx.alpaca.backend.core.repository.supabase.SupabaseModel
import kotlinx.serialization.Serializable

/**
 * Supabase entity representing an address.
 */
@Serializable
@SupabaseModel
data class AddressEntity(
    val streetAddress: String,
    val unit: String?,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String,
) {
    /**
     * Supabase entity representing a request to create new address.
     */
    @Serializable
    @SupabaseModel
    data class CreateAddressEntity(
        val streetAddress: String,
        val unit: String?,
        val city: String,
        val state: String,
        val zipCode: String,
        val country: String,
    )
}
