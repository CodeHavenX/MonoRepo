package com.cramsan.edifikana.client.lib.init

import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.thread.ThreadUtil
import com.cramsan.framework.thread.ThreadUtilInterface
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Initializer class for the application.
 */
class Initializer : KoinComponent {

    private val eventLogger by inject<EventLoggerInterface>()

    private val assertUtil by inject<AssertUtilInterface>()

    private val threadUtil by inject<ThreadUtilInterface>()

    /**
     * Perform code at the start of the application.
     */
    fun start() {
        eventLogger.i(TAG, "Starting application")

        EventLogger.setInstance(eventLogger)
        AssertUtil.setInstance(assertUtil)
        ThreadUtil.setInstance(threadUtil)
    }

    companion object {
        private const val TAG = "Initializer"
    }
}
