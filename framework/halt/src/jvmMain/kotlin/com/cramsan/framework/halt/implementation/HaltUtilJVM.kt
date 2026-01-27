package com.cramsan.framework.halt.implementation

import com.cramsan.framework.halt.HaltUtilDelegate
import com.cramsan.framework.logging.EventLoggerInterface
import kotlin.system.exitProcess

/**
 * JVM implementation of [HaltUtilDelegate]. This implementation uses a spin-lock for halting a thread.
 */
class HaltUtilJVM(private val eventLogger: EventLoggerInterface) : HaltUtilDelegate {

    // TODO: Refactor this into an AtomicBoolean
    private var shouldStop = false

    override fun resumeThread() {
        eventLogger.w(TAG, "Resuming current thread.")
        shouldStop = false
    }

    override fun stopThread() {
        eventLogger.w(TAG, "Stopping current thread.")
        shouldStop = true
        while (shouldStop) {
            Thread.sleep(SLEEP_TIME)
        }
    }

    override fun crashApp() {
        exitProcess(1)
    }

    companion object {
        private const val SLEEP_TIME = 1000L
        private const val TAG = "HaltUtilJVM"
    }
}
