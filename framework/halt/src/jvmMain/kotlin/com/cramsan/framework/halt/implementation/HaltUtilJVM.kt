package com.cramsan.framework.halt.implementation

import com.cramsan.framework.halt.HaltUtilDelegate
import com.cramsan.framework.logging.EventLoggerInterface
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess

/**
 * JVM implementation of [HaltUtilDelegate]. This implementation uses a spin-lock for halting a thread.
 */
class HaltUtilJVM(private val eventLogger: EventLoggerInterface) : HaltUtilDelegate {
    private val shouldStop = AtomicBoolean(false)

    override fun resumeThread() {
        eventLogger.w(TAG, "Resuming current thread.")
        shouldStop.set(false)
    }

    override fun stopThread() {
        eventLogger.w(TAG, "Stopping current thread.")
        shouldStop.set(true)
        while (shouldStop.get()) {
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
