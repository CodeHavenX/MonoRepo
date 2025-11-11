package com.cramsan.edifikana.server.settings

import com.cramsan.architecture.server.settings.SettingKey

/**
 * Application setting keys for the Edifikana application.
 */
object EdifikanaSettingKey {
    /**
     * Key for the Supabase project URL.
     */
    val SupabaseUrl = SettingKey.string("supabase.url")

    /**
     * Key for the Supabase API key used for authentication.
     */
    val SupabaseKey = SettingKey.string("supabase.key")
}
