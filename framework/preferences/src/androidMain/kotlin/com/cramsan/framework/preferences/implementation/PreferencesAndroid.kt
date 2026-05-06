package com.cramsan.framework.preferences.implementation

import android.content.Context
import android.content.SharedPreferences
import com.cramsan.framework.preferences.PreferencesDelegate

/**
 * This implementation of [PreferencesDelegate] uses the [context] to manage a [SharedPreferences].
 *
 * @param preferenceName Unique name for the shared preferences file. Use a reverse-domain string
 *   (e.g. "com.example.app.prefs") to avoid key collisions between features.
 */
class PreferencesAndroid(context: Context, preferenceName: String) : PreferencesDelegate {
    private val sharedPref = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)

    override fun saveString(key: String, value: String?) {
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    override fun loadString(key: String): String? {
        return if (!sharedPref.contains(key)) {
            null
        } else {
            sharedPref.getString(key, null)
        }
    }

    override fun saveInt(key: String, value: Int) {
        with(sharedPref.edit()) {
            putInt(key, value)
            apply()
        }
    }

    override fun loadInt(key: String): Int? {
        return if (!sharedPref.contains(key)) {
            null
        } else {
            sharedPref.getInt(key, 0)
        }
    }

    override fun saveLong(key: String, value: Long) {
        with(sharedPref.edit()) {
            putLong(key, value)
            apply()
        }
    }

    override fun loadLong(key: String): Long? {
        return if (!sharedPref.contains(key)) {
            null
        } else {
            sharedPref.getLong(key, 0)
        }
    }

    override fun saveBoolean(key: String, value: Boolean) {
        with(sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    override fun loadBoolean(key: String): Boolean? {
        return if (!sharedPref.contains(key)) {
            null
        } else {
            sharedPref.getBoolean(key, false)
        }
    }

    override fun remove(key: String) {
        with(sharedPref.edit()) {
            remove(key)
            apply()
        }
    }

    override fun clear() {
        with(sharedPref.edit()) {
            clear()
            apply()
        }
    }
}
