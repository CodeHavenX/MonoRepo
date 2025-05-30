package com.cramsan.edifikana.client.lib.features.admin.hub

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.ActivityRouteDestination
import com.cramsan.edifikana.client.lib.features.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.features.account.AccountRouteDestination
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEventBus
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.TestBase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class HubViewModelTest : TestBase() {

    private lateinit var viewModel: HubViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: WindowEventBus
    private lateinit var applicationEventReceiver: ApplicationEventBus

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = ApplicationEventBus()
        windowEventBus = WindowEventBus()
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
    fun `test navigateToAccount emits NavigateToActivity event`() = runBlockingTest {
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
    fun `test selectTab updates UI state`() = runBlockingTest {
        val selectedTab = Tabs.Properties
        viewModel.selectTab(selectedTab)
        assertEquals(selectedTab, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun `test navigateToNotifications emits NavigateToScreen event`() = runBlockingTest {
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