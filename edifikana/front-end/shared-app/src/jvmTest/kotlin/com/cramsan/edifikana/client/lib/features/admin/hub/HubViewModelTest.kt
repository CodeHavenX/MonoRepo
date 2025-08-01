package com.cramsan.edifikana.client.lib.features.admin.hub

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.account.AccountRouteDestination
import com.cramsan.edifikana.client.lib.features.management.hub.HubViewModel
import com.cramsan.edifikana.client.lib.features.management.hub.Tabs
import com.cramsan.edifikana.client.lib.features.window.ActivityRouteDestination
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
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class HubViewModelTest : CoroutineTest() {

    private lateinit var viewModel: HubViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        viewModel = HubViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventReceiver,
            )
        )
    }

    @Test
    fun `test navigateToAccount emits NavigateToActivity event`() = runCoroutineTest {
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToActivity(ActivityRouteDestination.AccountRouteDestination),
                    awaitItem()
                )
            }
        }
        viewModel.navigateToAccount()
        verificationJob.join()
    }

    @Test
    fun `test selectTab updates UI state`() = runCoroutineTest {
        val selectedTab = Tabs.Properties
        viewModel.selectTab(selectedTab)
        assertEquals(selectedTab, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun `test navigateToNotifications emits NavigateToScreen event`() = runCoroutineTest {
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(AccountRouteDestination.NotificationsDestination),
                    awaitItem()
                )
            }
        }
        viewModel.navigateToNotifications()
        verificationJob.join()
    }
}