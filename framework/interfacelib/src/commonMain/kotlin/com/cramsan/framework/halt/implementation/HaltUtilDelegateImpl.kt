package com.cramsan.framework.halt.implementation

import com.cramsan.framework.halt.HaltUtilDelegate

/**
 * Noop implementation of [HaltUtilDelegate] that does nothing.
 */
object HaltUtilDelegateImpl : HaltUtilDelegate {
    override fun resumeThread() = Unit

    override fun stopThread() = Unit

    override fun crashApp() = Unit
}
