package com.cramsan.runasimi.service.di

import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.halt.HaltUtil
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.preferences.Preferences
import com.cramsan.framework.thread.ThreadUtil
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.runasimi.service.service.DiscordCommunicationService
import io.mockk.mockk
import org.koin.dsl.module

/**
 * Class to initialize all the framework level components.
 */
fun createFrameworkModule(
    preferences: Preferences = mockk(relaxed = true),
    eventLoggerInterface: EventLoggerInterface = mockk(relaxed = true),
    haltUtil: HaltUtil = mockk(relaxed = true),
    assertUtilInterface: AssertUtilInterface = mockk(relaxed = true),
    threadUtilInterface: ThreadUtilInterface = mockk(relaxed = true),
) = module(createdAtStart = true) {

    single<Preferences> { preferences }

    single<EventLoggerInterface> {
        EventLogger.setInstance(eventLoggerInterface)
        EventLogger.singleton
    }

    single<HaltUtil> { haltUtil }

    single<AssertUtilInterface> {
        AssertUtil.setInstance(assertUtilInterface)
        AssertUtil.singleton
    }

    single<ThreadUtilInterface> {
        ThreadUtil.setInstance(threadUtilInterface)
        ThreadUtil.singleton
    }

    single {
        DiscordCommunicationService(
            get(),
            "",
        )
    }
}