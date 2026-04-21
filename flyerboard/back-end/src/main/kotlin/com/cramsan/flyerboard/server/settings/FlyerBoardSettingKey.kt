package com.cramsan.flyerboard.server.settings

import com.cramsan.architecture.server.settings.SettingKey

/**
 * Application setting keys for the FlyerBoard application.
 */
object FlyerBoardSettingKey {
    val LogAccountCreated = SettingKey.boolean("logging.account_created")
    val MaxFileSizeBytes = SettingKey.long("flyer.max_file_size_bytes")
    val SupabaseUrl = SettingKey.string("supabase.url")
    val SupabaseKey = SettingKey.string("supabase.key")
}
