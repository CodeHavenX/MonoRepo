package com.codehavenx.alpaca.backend.core.repository.supabase.models

import com.codehavenx.alpaca.backend.core.repository.supabase.SupabaseModel
import kotlinx.serialization.Serializable

/**
 * Supabase entity representing a configuration.
 */
@Serializable
@SupabaseModel
data class ConfigurationEntity(
    val id: String,
    val name: String,
    val appointmentType: String,
    val duration: Long,
    val timeZone: String,
) {
    companion object {
        const val COLLECTION = "configurations"
    }

    /**
     * Supabase entity representing a create configuration request.
     */
    @Serializable
    @SupabaseModel
    data class CreateConfigurationEntity(
        val name: String,
        val appointmentType: String,
        val duration: Long,
        val timeZone: String,
    )
}
