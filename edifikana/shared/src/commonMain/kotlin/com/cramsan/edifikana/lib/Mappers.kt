package com.cramsan.edifikana.lib

import com.cramsan.framework.logging.logE
import kotlinx.datetime.TimeZone

private val default_timezone = TimeZone.of("America/Lima")
fun safeTimeZone(timeZone: String?): TimeZone {
    return timeZone?.let {
        try {
            TimeZone.of(it)
        } catch (e: Exception) {
            logE(TAG, "Failed to parse time zone: $it", e)
            default_timezone
        }
    } ?: default_timezone
}

private const val TAG = "SharedMapper"
