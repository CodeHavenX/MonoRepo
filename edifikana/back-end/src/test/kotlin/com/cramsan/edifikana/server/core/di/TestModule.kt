package com.cramsan.edifikana.server.core.di

import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.core.controller.EventLogController
import com.cramsan.edifikana.server.core.controller.HealthCheckController
import com.cramsan.edifikana.server.core.controller.PropertyController
import com.cramsan.edifikana.server.core.controller.StaffController
import com.cramsan.edifikana.server.core.controller.TimeCardController
import com.cramsan.edifikana.server.core.controller.UserController
import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
import com.cramsan.edifikana.server.core.service.EventLogService
import com.cramsan.edifikana.server.core.service.PropertyService
import com.cramsan.edifikana.server.core.service.StaffService
import com.cramsan.edifikana.server.core.service.TimeCardService
import com.cramsan.edifikana.server.core.service.UserService
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.assertlib.implementation.AssertUtilImpl
import com.cramsan.framework.halt.HaltUtil
import com.cramsan.framework.halt.HaltUtilDelegate
import com.cramsan.framework.halt.implementation.HaltUtilImpl
import com.cramsan.framework.halt.implementation.HaltUtilJVM
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.Severity
import com.cramsan.framework.logging.implementation.EventLoggerImpl
import com.cramsan.framework.logging.implementation.Log4J2Helpers
import com.cramsan.framework.logging.implementation.LoggerJVM
import com.cramsan.framework.thread.ThreadUtil
import com.cramsan.framework.thread.ThreadUtilDelegate
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.thread.implementation.ThreadUtilImpl
import com.cramsan.framework.thread.implementation.ThreadUtilJVM
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.Logger
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Produce a framework module for testing.
 */
fun testFrameworkModule() = module(createdAtStart = true) {
    single<Logger> {
        Log4J2Helpers.getRootLogger(false, Severity.DEBUG)
    }
    single<EventLoggerInterface> {
        val instance = EventLoggerImpl(Severity.DEBUG, null, LoggerJVM(get()))
        EventLogger.setInstance(instance)
        EventLogger.singleton
    }
    single<HaltUtilDelegate> { HaltUtilJVM() }
    single<HaltUtil> { HaltUtilImpl(get()) }
    single<AssertUtilInterface>(createdAtStart = true) {
        val impl = AssertUtilImpl(
            true,
            get(),
            get(),
        )
        AssertUtil.setInstance(impl)
        AssertUtil.singleton
    }
    single<ThreadUtilDelegate> { ThreadUtilJVM(get(), get()) }
    single<ThreadUtilInterface> {
        val instance = ThreadUtilImpl(get())
        ThreadUtil.setInstance(instance)
        ThreadUtil.singleton
    }
}

/**
 * Produce a Ktor module for testing.
 */
fun testKtorModule() = module {
    singleOf(::UserController)
    singleOf(::EventLogController)
    singleOf(::HealthCheckController)
    singleOf(::PropertyController)
    singleOf(::StaffController)
    singleOf(::TimeCardController)
}

/**
 * Produce a test module for the application.
 */
fun testApplicationModule() = module {
    single<Json> { createJson() }

    single<Clock> { mockk() }

    single<ContextRetriever> { mockk() }

    // Services
    single<UserService> { mockk() }
    single<EventLogService> { mockk() }
    single<PropertyService> { mockk() }
    single<StaffService> { mockk() }
    single<TimeCardService> { mockk() }
}
