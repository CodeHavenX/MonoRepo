package com.cramsan.edifikana.server.core.repository.supabase.models

import com.cramsan.edifikana.server.core.repository.supabase.SupabaseModel
import kotlinx.serialization.Serializable

@Serializable
@SupabaseModel
data class GlobalPermOverrideEntity(
    val id: String,
) {
    companion object {
        const val COLLECTION = "global_perm_override"
    }
}
