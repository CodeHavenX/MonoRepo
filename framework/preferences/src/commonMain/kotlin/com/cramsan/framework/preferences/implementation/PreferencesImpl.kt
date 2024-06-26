package com.cramsan.framework.preferences.implementation

import com.cramsan.framework.preferences.Preferences
import com.cramsan.framework.preferences.PreferencesDelegate

/**
 * Simple implementation of [Preferences]. It delegates all it's operations to the [platformDelegate].
 */
class PreferencesImpl(private val platformDelegate: PreferencesDelegate) : Preferences {

    override fun saveString(key: String, value: String?) {
        platformDelegate.saveString(key, value)
    }

    override fun loadString(key: String): String? {
        return platformDelegate.loadString(key)
    }

    override fun saveInt(key: String, value: Int) {
        platformDelegate.saveInt(key, value)
    }

    override fun loadInt(key: String): Int? {
        return platformDelegate.loadInt(key)
    }

    override fun saveLong(key: String, value: Long) {
        platformDelegate.saveLong(key, value)
    }

    override fun loadLong(key: String): Long? {
        return platformDelegate.loadLong(key)
    }

    override fun saveBoolean(key: String, value: Boolean) {
        platformDelegate.saveBoolean(key, value)
    }

    override fun loadBoolean(key: String): Boolean? {
        return platformDelegate.loadBoolean(key)
    }

    override fun remove(key: String) {
        platformDelegate.remove(key)
    }

    override fun clear() {
        platformDelegate.clear()
    }
}
