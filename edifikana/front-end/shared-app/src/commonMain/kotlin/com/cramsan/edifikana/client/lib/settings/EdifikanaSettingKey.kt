package com.cramsan.edifikana.client.lib.settings

import com.cramsan.architecture.client.settings.SettingKey

/**
 * Typed constants for the overrides that can be set in the application.
 *
 * These are SettingKey instances so callers can rely on typed keys (boolean/string) like other projects.
 * The string names are kept the same as the old enum names to preserve existing stored preferences.
 */
object EdifikanaSettingKey {
    val SupabaseOverrideUrl = SettingKey.string("KEY_SUPABASE_OVERRIDE_URL")
    val SupabaseOverrideKey = SettingKey.string("KEY_SUPABASE_OVERRIDE_KEY")
    val OpenDebugWindow = SettingKey.boolean("KEY_OPEN_DEBUG_WINDOW")
    val EdifikanaBeUrl = SettingKey.string("KEY_EDIFIKANA_BE_URL")
    val SelectedTheme = SettingKey.string("KEY_SELECTED_THEME")
    val lastSelectedProperty = SettingKey.string("KEY_LAST_SELECTED_PROPERTY")
    val lastSelectedOrganization = SettingKey.string("KEY_LAST_SELECTED_ORGANIZATION")
}
