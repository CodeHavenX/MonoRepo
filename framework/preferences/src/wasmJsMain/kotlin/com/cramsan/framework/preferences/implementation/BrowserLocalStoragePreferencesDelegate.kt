package com.cramsan.framework.preferences.implementation

import com.cramsan.framework.logging.logW
import com.cramsan.framework.preferences.PreferencesDelegate
import org.w3c.dom.get
import org.w3c.dom.set

/**
 * A [PreferencesDelegate] implementation that uses the browser's local storage.
 */
class BrowserLocalStoragePreferencesDelegate : PreferencesDelegate {

    private val storage = kotlinx.browser.localStorage

    override fun saveString(key: String, value: String?) {
        if (value == null) {
            storage.removeItem(key)
        } else {
            storage[key] = value
        }
    }

    override fun loadString(key: String): String? {
        return storage[key]
    }

    override fun saveInt(key: String, value: Int) {
        storage[key] = value.toString()
    }

    override fun loadInt(key: String): Int? {
        return storage[key]?.toIntOrNull()
    }

    override fun saveLong(key: String, value: Long) {
        storage[key] = value.toString()
    }

    override fun loadLong(key: String): Long? {
        return storage[key]?.toLongOrNull()
    }

    override fun saveBoolean(key: String, value: Boolean) {
        storage[key] = value.toString()
    }

    override fun loadBoolean(key: String): Boolean? {
        return storage[key]?.toBooleanStrictOrNull()
    }

    override fun remove(key: String) {
        storage.removeItem(key)
    }

    override fun clear() {
        logW(
            TAG,
            "clear() is not supported in BrowserLocalStoragePreferencesDelegate. " +
                "Use remove() for individual keys."
        )
    }

    companion object {
        private const val TAG = "BrowserLocalStoragePreferencesDelegate"
    }
}
