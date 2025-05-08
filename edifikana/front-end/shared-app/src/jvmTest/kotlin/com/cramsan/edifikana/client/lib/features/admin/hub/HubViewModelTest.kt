package com.cramsan.edifikana.client.lib.features.admin.hub

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.ActivityRouteDestination
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.account.AccountRouteDestination
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.SharedFlowApplicationReceiver
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.TestBase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach

@OptIn(ExperimentalCoroutinesApi::class)
class HubViewModelTest : TestBase() {

    private lateinit var viewModel: HubViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: SharedFlowApplicationReceiver

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = SharedFlowApplicationReceiver()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        viewModel = HubViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
            )
        )
    }

    @Test
    fun `test navigateToAccount emits NavigateToActivity event`() = runBlockingTest {
        val verificationJob = launch {
            applicationEventReceiver.events.test {
                assertEquals(
                    EdifikanaApplicationEvent.NavigateToActivity(ActivityRouteDestination.AccountRouteDestination),
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
            applicationEventReceiver.events.test {
                assertEquals(
                    EdifikanaApplicationEvent.NavigateToScreen(AccountRouteDestination.NotificationsDestination),
                    awaitItem()
                )
            }
        }
        viewModel.navigateToNotifications()
        verificationJob.join()
    }
}