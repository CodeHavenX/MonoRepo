package com.codehavenx.alpaca.backend.core.repository.supabase.models

import com.codehavenx.alpaca.backend.core.repository.supabase.SupabaseModel
import kotlinx.serialization.Serializable

@Serializable
@SupabaseModel
data class AddressEntity (
    val streetAddress: String,
    val unit: String?,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String,
) {
    /**
     * Supabase entity representing a create new address request.
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