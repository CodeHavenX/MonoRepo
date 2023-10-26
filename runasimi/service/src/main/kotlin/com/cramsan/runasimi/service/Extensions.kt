package com.cramsan.runasimi.service

import com.cramsan.framework.logging.Severity
import org.slf4j.event.Level

fun Severity.toLevel(): Level {
    return when (this) {
        Severity.DISABLED -> Level.ERROR
        Severity.ERROR -> Level.ERROR
        Severity.WARNING -> Level.WARN
        Severity.INFO -> Level.INFO
        Severity.DEBUG -> Level.DEBUG
        Severity.VERBOSE -> Level.TRACE
    }
}
