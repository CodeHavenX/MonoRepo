package com.codehavenx.alpaca.backend.core.repository.supabase.models

import com.codehavenx.alpaca.backend.core.repository.supabase.SupabaseModel
import kotlinx.serialization.Serializable

/**
 * Supabase entity representing an event.
 */
@Serializable
@SupabaseModel
data class EventEntity(
    val id: String,
    val owner: String,
    val attendants: Set<String>,
    val title: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
) {
    companion object {
        const val COLLECTION = "events"
    }

    /**
     * Supabase entity representing a create event request.
     */
    @Serializable
    @SupabaseModel
    data class CreateEventEntity(
        val owner: String,
        val attendants: Set<String>,
        val title: String,
        val description: String,
        val startTime: Long,
        val endTime: Long,
    )
}
