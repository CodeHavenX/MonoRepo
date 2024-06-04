package com.cramsan.edifikana.client.android.framework.crashhandler

import com.cramsan.framework.logging.EventLoggerErrorCallbackDelegate
import com.cramsan.framework.logging.Severity
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

class CrashlyticsErrorCallback : EventLoggerErrorCallbackDelegate {

    private val crashlytics = Firebase.crashlytics

    override fun handleErrorEvent(tag: String, message: String, throwable: Throwable, severity: Severity) {
        crashlytics.log("$tag[$severity]: $message")
        crashlytics.recordException(throwable)
    }
}
