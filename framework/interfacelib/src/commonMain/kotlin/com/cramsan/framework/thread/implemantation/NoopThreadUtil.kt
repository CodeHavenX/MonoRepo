package com.cramsan.framework.thread.implemantation

import com.cramsan.framework.thread.ThreadUtilInterface

/**
 * Noop implementation of [ThreadUtilInterface] that does nothing.
 */
class NoopThreadUtil : ThreadUtilInterface {

    override fun isUIThread(): Boolean = false

    override fun isBackgroundThread(): Boolean = false

    override fun assertIsUIThread() = Unit

    override fun assertIsBackgroundThread() = Unit
}
