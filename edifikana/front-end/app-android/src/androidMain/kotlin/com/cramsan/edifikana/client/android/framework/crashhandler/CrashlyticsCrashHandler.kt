package com.cramsan.edifikana.client.android.framework.crashhandler

import com.cramsan.framework.crashehandler.CrashHandlerDelegate

class CrashlyticsCrashHandler : CrashHandlerDelegate {

    override fun initialize() {
        // Crashlytics is automatically initialized by Firebase
        // No further action is required
    }
}
