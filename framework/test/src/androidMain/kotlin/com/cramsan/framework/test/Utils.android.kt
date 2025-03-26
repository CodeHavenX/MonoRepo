package com.cramsan.framework.test

import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.userevents.UserEventsInterface
import io.mockk.mockk

/**
 * Helper function to apply mocked framework singletons.
 */
fun applyMockedFrameworkSingletons(
    eventLogger: EventLoggerInterface? = mockk<EventLoggerInterface>(relaxUnitFun = true),
    assertUtil: AssertUtilInterface? = mockk<AssertUtilInterface>(relaxUnitFun = true),
    userEvents: UserEventsInterface? = mockk<UserEventsInterface>(relaxUnitFun = true),
    threadUtil: ThreadUtilInterface? = mockk<ThreadUtilInterface>(relaxUnitFun = true),
) {
    applyFrameworkSingletons(
        eventLogger = eventLogger,
        assertUtil = assertUtil,
        userEvents = userEvents,
        threadUtil = threadUtil,
    )
}
