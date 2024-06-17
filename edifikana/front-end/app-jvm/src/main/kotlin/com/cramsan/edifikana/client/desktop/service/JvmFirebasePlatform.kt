package com.cramsan.edifikana.client.desktop.service

import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.preferences.Preferences
import com.google.firebase.FirebasePlatform

class JvmFirebasePlatform(
    private val preferences: Preferences,
    private val eventLogger: EventLoggerInterface,
) : FirebasePlatform() {
    override fun clear(key: String) {
        preferences.clear()
    }

    override fun log(msg: String) {
        eventLogger.log(Severity.INFO, TAG, msg)
    }

    override fun retrieve(key: String): String? {
        return preferences.loadString(key)
    }

    override fun store(key: String, value: String) {
        preferences.saveString(key, value)
    }

    companion object {
        private const val TAG = "JvmFirebasePlatform"
    }
}
