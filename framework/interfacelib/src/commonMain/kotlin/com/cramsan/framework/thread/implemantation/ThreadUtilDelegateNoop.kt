package com.cramsan.framework.thread.implemantation

import com.cramsan.framework.thread.ThreadUtilDelegate

/**
 * Noop implementation of [ThreadUtilDelegate] that does nothing.
 */
object ThreadUtilDelegateNoop : ThreadUtilDelegate {

    override fun isUIThread(): Boolean = false

    override fun isBackgroundThread(): Boolean = false

    override fun assertIsUIThread() = Unit

    override fun assertIsBackgroundThread() = Unit
}
