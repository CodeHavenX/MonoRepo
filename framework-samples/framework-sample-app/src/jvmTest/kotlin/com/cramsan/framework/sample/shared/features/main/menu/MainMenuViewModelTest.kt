package com.cramsan.framework.sample.shared.features.main.menu

import app.cash.turbine.turbineScope
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import com.cramsan.framework.sample.shared.features.main.MainDestination
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MainMenuViewModelTest : CoroutineTest() {

    private lateinit var viewModel: MainMenuViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventBus: EventBus<ApplicationEvent>

    @BeforeTest
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventBus = EventBus()
        windowEventBus = EventBus()
        viewModel = MainMenuViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventBus,
            ),
        )
    }

    @Test
    fun `initial UIState is correct`() = runCoroutineTest {
        assertEquals(MainMenuUIState.Initial, viewModel.uiState.value)
    }

    @Test
    fun `navigateToHaltUtil emits NavigateToScreen with HaltUtilDestination`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateToHaltUtil()

            assertEquals(
                SampleWindowEvent.NavigateToScreen(MainDestination.HaltUtilDestination),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `navigateToLogging emits NavigateToScreen with LoggingDestination`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateToLogging()

            assertEquals(
                SampleWindowEvent.NavigateToScreen(MainDestination.LoggingDestination),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `navigateToPreferences emits NavigateToScreen with PreferencesDestination`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateToPreferences()

            assertEquals(
                SampleWindowEvent.NavigateToScreen(MainDestination.PreferencesDestination),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `navigateToThreadUtil emits NavigateToScreen with ThreadUtilDestination`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateToThreadUtil()

            assertEquals(
                SampleWindowEvent.NavigateToScreen(MainDestination.ThreadUtilDestination),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `navigateToAssertUtil emits NavigateToScreen with AssertUtilDestination`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateToAssertUtil()

            assertEquals(
                SampleWindowEvent.NavigateToScreen(MainDestination.AssertUtilDestination),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `navigateToMetrics emits NavigateToScreen with MetricsDestination`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateToMetrics()

            assertEquals(
                SampleWindowEvent.NavigateToScreen(MainDestination.MetricsDestination),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `navigateToConfiguration emits NavigateToScreen with ConfigurationDestination`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateToConfiguration()

            assertEquals(
                SampleWindowEvent.NavigateToScreen(MainDestination.ConfigurationDestination),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `navigateToCrashHandler emits NavigateToScreen with CrashHandlerDestination`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateToCrashHandler()

            assertEquals(
                SampleWindowEvent.NavigateToScreen(MainDestination.CrashHandlerDestination),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `navigateToUserEvents emits NavigateToScreen with UserEventsDestination`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateToUserEvents()

            assertEquals(
                SampleWindowEvent.NavigateToScreen(MainDestination.UserEventsDestination),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `navigateToRemoteConfig emits NavigateToScreen with RemoteConfigDestination`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateToRemoteConfig()

            assertEquals(
                SampleWindowEvent.NavigateToScreen(MainDestination.RemoteConfigDestination),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `navigateToDispatcher emits NavigateToScreen with DispatcherDestination`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateToDispatcher()

            assertEquals(
                SampleWindowEvent.NavigateToScreen(MainDestination.DispatcherDestination),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }
}
