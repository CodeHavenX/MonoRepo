package com.cramsan.edifikana.server.settings

import com.cramsan.architecture.server.settings.SettingKey

/**
 * Application setting keys for the Edifikana application.
 */
object EdifikanaSettingKey {
    val SupabaseUrl = SettingKey.string("supabase.url")
    val SupabaseKey = SettingKey.string("supabase.key")
}
