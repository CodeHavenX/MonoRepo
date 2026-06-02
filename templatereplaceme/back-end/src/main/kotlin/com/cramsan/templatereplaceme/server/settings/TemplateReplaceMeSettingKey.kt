package com.cramsan.templatereplaceme.server.settings

import com.cramsan.architecture.server.settings.SettingKey

/**
 * Application setting keys for the TemplateReplaceMe application.
 */
object TemplateReplaceMeSettingKey {
    /** Whether to log successful ping-pong operations to the debug output. */
    val LogPingPong = SettingKey.boolean("logging.ping_pong")
}
