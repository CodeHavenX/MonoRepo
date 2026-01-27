package com.cramsan.framework.preferences.implementation

import com.cramsan.framework.preferences.PreferencesDelegate
import java.util.concurrent.locks.ReentrantLock
import java.util.prefs.Preferences

/**
 * This implementation of [PreferencesDelegate] uses the JVM.
 *
 * Provide a [fqdn] (Fully Qualified Domain Name) to create a unique preferences node. Ensure that the FQDN is unique
 * across your application to avoid conflicts with other preferences.
 */
class JVMPreferencesDelegate(
    private val fqdn: String,
) : PreferencesDelegate {

    private val prefs: Preferences = Preferences.userRoot().node(fqdn)
    private val mutex = ReentrantLock()

    override fun saveString(key: String, value: String?) {
        writeAndFlush {
            if (value == null) {
                prefs.remove(key)
            } else {
                prefs.put(key, value)
            }
        }
    }

    override fun loadString(key: String): String? = verifyAndGetOrNull(key) {
        prefs.get(key, null)
    }

    override fun saveInt(key: String, value: Int) {
        writeAndFlush {
            prefs.putInt(key, value)
        }
    }

    override fun loadInt(key: String): Int? = verifyAndGetOrNull(key) {
        prefs.getInt(key, Int.MIN_VALUE)
    }

    override fun saveLong(key: String, value: Long) {
        writeAndFlush {
            prefs.putLong(key, value)
        }
    }

    override fun loadLong(key: String): Long? = verifyAndGetOrNull(key) {
        prefs.getLong(key, Long.MIN_VALUE)
    }

    override fun saveBoolean(key: String, value: Boolean) {
        writeAndFlush {
            prefs.putBoolean(key, value)
        }
    }

    override fun loadBoolean(key: String): Boolean? = verifyAndGetOrNull(key) {
        prefs.getBoolean(key, false)
    }

    @Suppress("SwallowedException")
    private fun <T> verifyAndGetOrNull(
        nodeName: String,
        block: (nodeName: String) -> T?,
    ): T? = try {
        // TODO: Verify that the key exists
        block(nodeName)
    } catch (throwable: Throwable) {
        null
    }

    private fun writeAndFlush(
        writeAction: () -> Unit,
    ) {
        try {
            mutex.lock()
            writeAction()
            prefs.putLong(INTERNAL_LAST_UPDATE, System.currentTimeMillis())
        } finally {
            prefs.flush()
            mutex.unlock()
        }
    }

    override fun remove(key: String) {
        prefs.remove(key)
    }

    override fun clear() {
        prefs.clear()
    }
}

private const val INTERNAL_LAST_UPDATE = "last_update"
