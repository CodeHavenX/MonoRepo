package com.cramsan.edifikana.client.lib.features.home.appshell

import app.cash.turbine.turbineScope
import com.cramsan.edifikana.client.lib.features.account.AccountDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class AppShellViewModelTest : CoroutineTest() {

    private lateinit var viewModel: AppShellViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>

    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        viewModel = AppShellViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
                windowEventReceiver = windowEventBus,
            ),
        )
    }

    @Test
    fun `initial state has Dashboard as selected tab`() = runCoroutineTest {
        assertEquals(AppShellTab.Dashboard, viewModel.uiState.value.selectedTab)
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `selectTab Properties updates selectedTab to Properties`() = runCoroutineTest {
        viewModel.selectTab(AppShellTab.Properties)

        assertEquals(AppShellTab.Properties, viewModel.uiState.value.selectedTab)
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `selectTab Tasks updates selectedTab to Tasks`() = runCoroutineTest {
        viewModel.selectTab(AppShellTab.Tasks)

        assertEquals(AppShellTab.Tasks, viewModel.uiState.value.selectedTab)
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `selectTab Dashboard from another tab updates selectedTab back to Dashboard`() = runCoroutineTest {
        viewModel.selectTab(AppShellTab.Properties)
        viewModel.selectTab(AppShellTab.Dashboard)

        assertEquals(AppShellTab.Dashboard, viewModel.uiState.value.selectedTab)
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `selectTab More does not change state and emits Settings navigation event`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.selectTab(AppShellTab.More)

            assertEquals(
                EdifikanaWindowsEvent.NavigateToNavGraph(EdifikanaNavGraphDestination.SettingsNavGraphDestination),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }

        assertEquals(AppShellTab.Dashboard, viewModel.uiState.value.selectedTab)
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `navigateToAccount emits Account navigation event`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateToAccount()

            assertEquals(
                EdifikanaWindowsEvent.NavigateToNavGraph(EdifikanaNavGraphDestination.AccountNavGraphDestination),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }

        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `navigateToNotifications emits Notifications screen navigation event`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateToNotifications()

            assertEquals(
                EdifikanaWindowsEvent.NavigateToScreen(AccountDestination.NotificationsDestination),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }

        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `navigateToSettings emits Settings navigation event`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateToSettings()

            assertEquals(
                EdifikanaWindowsEvent.NavigateToNavGraph(EdifikanaNavGraphDestination.SettingsNavGraphDestination),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }

        assertTrue(exceptionHandler.exceptions.isEmpty())
    }
}
