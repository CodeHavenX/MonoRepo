package com.cramsan.framework.sample.shared.stubs

import com.cramsan.framework.crashhandler.CrashHandlerDelegate

/** No-op implementation of [CrashHandlerDelegate] for sample and testing use. */
class NoopCrashHandlerDelegate : CrashHandlerDelegate {
    override fun initialize() = Unit
}
