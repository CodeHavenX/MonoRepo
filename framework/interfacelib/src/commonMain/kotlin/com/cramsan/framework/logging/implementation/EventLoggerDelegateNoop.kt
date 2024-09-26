package com.cramsan.framework.logging.implementation

import com.cramsan.framework.logging.EventLoggerDelegate
import com.cramsan.framework.logging.Severity

object EventLoggerDelegateNoop : EventLoggerDelegate {
    override fun log(severity: Severity, tag: String, message: String, throwable: Throwable?, vararg args: Any?) {
        println(message)
    }

    override fun setTargetSeverity(targetSeverity: Severity) {
        println(targetSeverity)
    }
}