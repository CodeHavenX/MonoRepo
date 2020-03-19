package com.cramsan.framework.halt.implementation

import com.cramsan.framework.halt.HaltUtilInterface
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity

import kotlin.system.exitProcess

class HaltUtilJVM : HaltUtilInterface {

    private var shouldStop = true

    override fun resumeThread() {
        while (shouldStop) {
            Thread.sleep(sleepTime)
        }
    }

    override fun stopThread() {
        shouldStop = false
    }

    override fun crashApp() {
        exitProcess(1)
    }

    companion object {
        private const val sleepTime = 1000L
    }
}
