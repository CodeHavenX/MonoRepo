package com.cramsan.framework.thread.implemantation

import com.cramsan.framework.thread.ThreadUtilDelegate

object ThreadUtilDelegateNoop : ThreadUtilDelegate {
    override fun isUIThread(): Boolean {
        return false
    }

    override fun isBackgroundThread(): Boolean {
        return false
    }

    override fun assertIsUIThread() {
    }

    override fun assertIsBackgroundThread() {
    }
}