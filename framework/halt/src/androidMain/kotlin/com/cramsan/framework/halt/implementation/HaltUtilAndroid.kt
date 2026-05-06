package com.cramsan.framework.halt.implementation

import android.content.Context
import com.cramsan.framework.halt.HaltUtilDelegate

/**
 * [HaltUtilDelegate] implementation for the Android target.
 */
@Suppress("UnusedPrivateProperty")
class HaltUtilAndroid(private val appContext: Context) : HaltUtilDelegate {
    override fun stopThread() = Unit

    override fun resumeThread() = Unit

    override fun crashApp() {
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}
