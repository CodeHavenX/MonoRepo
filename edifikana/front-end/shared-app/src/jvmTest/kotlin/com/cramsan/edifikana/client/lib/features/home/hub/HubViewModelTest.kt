package com.cramsan.edifikana.client.lib.features.home.hub

import app.cash.turbine.turbineScope
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.edifikana.client.lib.features.account.AccountDestination
import com.cramsan.edifikana.client.lib.features.home.organizationhome.OrganizationHomeViewModel
import com.cramsan.edifikana.client.lib.features.home.organizationhome.Tabs
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
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
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class HubViewModelTest : CoroutineTest() {

    private lateinit var viewModel: OrganizationHomeViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var organizationManager: OrganizationManager
    private lateinit var preferencesManager: PreferencesManager

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        organizationManager = mockk()
        preferencesManager = mockk()
        viewModel = OrganizationHomeViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventReceiver,
            ),
            organizationManager = organizationManager,
            preferencesManager = preferencesManager,
        )
    }

    @Test
    fun `test navigateToAccount emits NavigateToNavGraph event`() = runCoroutineTest {
        turbineScope {
            // Arrange
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.navigateToAccount()

            // Assert
            assertEquals(
                EdifikanaWindowsEvent.NavigateToNavGraph(EdifikanaNavGraphDestination.AccountNavGraphDestination),
                turbine.awaitItem()
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `test selectTab updates UI state`() = runCoroutineTest {
        val selectedTab = Tabs.Properties
        viewModel.selectTab(selectedTab)
        assertEquals(selectedTab, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun `test navigateToNotifications emits NavigateToScreen event`() = runCoroutineTest {
        turbineScope {
            // Arrange
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.navigateToNotifications()

            // Assert
            assertEquals(
                EdifikanaWindowsEvent.NavigateToScreen(AccountDestination.NotificationsDestination),
                turbine.awaitItem()
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }
}