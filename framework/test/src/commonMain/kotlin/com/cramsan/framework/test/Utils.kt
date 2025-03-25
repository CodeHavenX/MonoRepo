package com.cramsan.framework.test

import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.assertlib.implementation.NoopAssertUtil
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.implementation.NoopEventLogger
import com.cramsan.framework.thread.ThreadUtil
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.thread.implemantation.NoopThreadUtil
import com.cramsan.framework.userevents.UserEvents
import com.cramsan.framework.userevents.UserEventsInterface
import com.cramsan.framework.userevents.implementation.NoopUserEvents

/**
 * Helper function to apply framework singletons.
 */
fun applyFrameworkSingletons(
    eventLogger: EventLoggerInterface?,
    assertUtil: AssertUtilInterface?,
    userEvents: UserEventsInterface?,
    threadUtil: ThreadUtilInterface?,
) {
    eventLogger?.let { EventLogger.setInstance(it) }
    assertUtil?.let { AssertUtil.setInstance(it) }
    userEvents?.let { UserEvents.setInstance(it) }
    threadUtil?.let { ThreadUtil.setInstance(it) }
}

/**
 * Helper function to apply no-op framework singletons.
 */
fun applyNoopFrameworkSingletons() {
    applyFrameworkSingletons(
        eventLogger = NoopEventLogger(),
        assertUtil = NoopAssertUtil(),
        userEvents = NoopUserEvents(),
        threadUtil = NoopThreadUtil(),
    )
}
