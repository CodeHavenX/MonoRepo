package com.cramsan.flyerboard.client.lib.settings

import com.cramsan.architecture.client.settings.SettingGroup
import com.cramsan.architecture.client.settings.SettingKey
import com.cramsan.architecture.client.settings.settingGroup

/**
 * Setting keys used in FlyerBoard application.
 */
object FlyerBoardSettingKey {
    /** Supabase API URL override. */
    val SupabaseUrl = SettingKey.string("KEY_SUPABASE_URL")

    /** Supabase publishable key override. */
    val SupabaseKey = SettingKey.string("KEY_SUPABASE_KEY")

    /**
     * Returns a [SettingGroup] describing all FlyerBoard-specific settings for use with
     * [com.cramsan.architecture.client.settings.SettingRegistry].
     *
     * Call this once during DI startup and pass the result to
     * [com.cramsan.architecture.client.settings.SettingRegistry.register].
     */
    fun defaultGroup(): SettingGroup =
        settingGroup("FlyerBoard") {
            subGroup("Supabase") {
                setting(SupabaseUrl, "Supabase API URL", "Override from `supabase status`")
                setting(SupabaseKey, "Supabase Publishable Key", "Override from `supabase status`")
            }
        }
}
