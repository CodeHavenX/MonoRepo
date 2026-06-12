package com.cramsan.edifikana.client.lib.settings

import com.cramsan.architecture.client.settings.SettingGroup
import com.cramsan.architecture.client.settings.SettingKey
import com.cramsan.architecture.client.settings.settingGroup

/**
 * Typed constants for the overrides that can be set in the application.
 *
 * These are SettingKey instances so callers can rely on typed keys (boolean/string) like other projects.
 * The string names are kept the same as the old enum names to preserve existing stored preferences.
 */
object EdifikanaSettingKey {
    /** Supabase API URL override. */
    val SupabaseOverrideUrl = SettingKey.string("KEY_SUPABASE_OVERRIDE_URL")

    /** Supabase publishable key override. */
    val SupabaseOverrideKey = SettingKey.string("KEY_SUPABASE_OVERRIDE_KEY")

    /** Whether to open the debug window on desktop. */
    val OpenDebugWindow = SettingKey.boolean("KEY_OPEN_DEBUG_WINDOW")

    /** The user-selected UI theme. */
    val SelectedTheme = SettingKey.string("KEY_SELECTED_THEME")

    /** The last property selected by the user. */
    val lastSelectedProperty = SettingKey.string("KEY_LAST_SELECTED_PROPERTY")

    /** The last organisation selected by the user. */
    val lastSelectedOrganization = SettingKey.string("KEY_LAST_SELECTED_ORGANIZATION")

    /**
     * Returns a [SettingGroup] describing all Edifikana-specific settings for use with
     * [com.cramsan.architecture.client.settings.SettingRegistry].
     *
     * Call this once during DI startup and pass the result to
     * [com.cramsan.architecture.client.settings.SettingRegistry.register].
     */
    fun defaultGroup(): SettingGroup =
        settingGroup("Edifikana") {
            subGroup("Supabase") {
                setting(SupabaseOverrideUrl, "Supabase API URL", "Override from `supabase status`")
                setting(SupabaseOverrideKey, "Supabase Publishable Key", "Override from `supabase status`")
            }
            subGroup("UI") {
                setting(SelectedTheme, "Selected Theme")
                setting(OpenDebugWindow, "Open Debug Window", "Desktop only.")
            }
            subGroup("Session") {
                setting(lastSelectedProperty, "Last Selected Property")
                setting(lastSelectedOrganization, "Last Selected Organisation")
            }
        }
}
