package com.cramsan.flyerboard.client.lib.settings

import com.cramsan.architecture.client.settings.SettingKey

/**
 * Setting keys used in FlyerBoard application.
 */
object FlyerBoardSettingKey {
    val OpenDebugWindow = SettingKey.boolean("debug.window.open")
    val SupabaseUrl = SettingKey.string("KEY_SUPABASE_URL")
    val SupabaseKey = SettingKey.string("KEY_SUPABASE_KEY")
}
