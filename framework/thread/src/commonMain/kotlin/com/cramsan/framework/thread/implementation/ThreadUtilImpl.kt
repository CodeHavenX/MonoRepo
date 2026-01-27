package com.cramsan.framework.thread.implementation

import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.ThreadUtilInterface

/**
 * Common implementation of [ThreadUtilInterface].
 *
 * @see [ThreadUtilInterface]
 */
class ThreadUtilImpl(private val platformDelegate: ThreadUtilDelegate) : ThreadUtilInterface {

    override fun isUIThread(): Boolean = platformDelegate.isUIThread()

    override fun isBackgroundThread(): Boolean = platformDelegate.isBackgroundThread()

    override fun assertIsUIThread() {
        platformDelegate.assertIsUIThread()
    }

    override fun assertIsBackgroundThread() {
        platformDelegate.assertIsBackgroundThread()
    }
}
