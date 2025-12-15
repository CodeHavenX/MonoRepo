package com.cramsan.edifikana.client.lib.features.home.propertyhome

import app.cash.turbine.test
import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.edifikana.client.lib.features.account.AccountDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
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
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.launch
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for [PropertyHomeViewModel].
 */
class PropertyHomeViewModelTest : CoroutineTest() {

    private lateinit var viewModel: PropertyHomeViewModel
    private lateinit var propertyManager: PropertyManager
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>

    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        propertyManager = mockk(relaxed = true)
        preferencesManager = mockk(relaxed = true)
        coEvery { preferencesManager.getStringPreference(any()) } returns Result.success(null)
        viewModel = PropertyHomeViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventReceiver,
            ),
            propertyManager = propertyManager,
            preferencesManager = preferencesManager,
        )
    }

    @Test
    fun `test initial UI state`() = runCoroutineTest {
        val initialState = viewModel.uiState.value
        assertEquals(PropertyHomeUIModel.Empty, initialState)
        assertEquals(Tabs.None, initialState.selectedTab)
    }

    @Test
    fun `test loadContent with properties selects EventLog tab`() = runCoroutineTest {
        // Set up
        val properties = listOf(
            PropertyModel(
                id = PropertyId("property-1"),
                name = "Test Property",
                address = "123 Main St",
                organizationId = OrganizationId("org-1"),
            )
        )
        coEvery { propertyManager.getPropertyList() } returns Result.success(properties)

        // Act
        viewModel.loadContent()

        // Assert
        val state = viewModel.uiState.value
        assertEquals(Tabs.EventLog, state.selectedTab)
        assertEquals(1, state.availableProperties.size)
        assertEquals("Test Property", state.label)
    }

    @Test
    fun `test loadContent with no properties selects GoToOrganization tab`() = runCoroutineTest {
        // Set up
        coEvery { propertyManager.getPropertyList() } returns Result.success(emptyList())

        // Act
        viewModel.loadContent()

        // Assert
        val state = viewModel.uiState.value
        assertEquals(Tabs.GoToOrganization, state.selectedTab)
        assertTrue(state.availableProperties.isEmpty())
    }

    @Test
    fun `test selectProperty updates UI state and saves preference`() = runCoroutineTest {
        // Set up
        val propertyId = PropertyId("property-1")
        val properties = listOf(
            PropertyModel(
                id = propertyId,
                name = "Test Property",
                address = "123 Main St",
                organizationId = OrganizationId("org-1"),
            )
        )
        coEvery { propertyManager.getPropertyList() } returns Result.success(properties)

        // Act
        viewModel.selectProperty(propertyId)

        // Assert
        assertEquals(propertyId, viewModel.uiState.value.propertyId)
        coVerify { preferencesManager.updatePreference(any(), propertyId.propertyId) }
    }

    @Test
    fun `test selectTab updates selected tab`() = runCoroutineTest {
        // Act
        viewModel.selectTab(Tabs.EventLog)

        // Assert
        assertEquals(Tabs.EventLog, viewModel.uiState.value.selectedTab)

        // Act - select different tab
        viewModel.selectTab(Tabs.GoToOrganization)

        // Assert
        assertEquals(Tabs.GoToOrganization, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun `test navigateBack emits NavigateBack event`() = runCoroutineTest {
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateBack,
                    awaitItem()
                )
            }
        }
        viewModel.navigateBack()
        verificationJob.join()
    }

    @Test
    fun `test navigateToAccount emits NavigateToNavGraph event`() = runCoroutineTest {
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToNavGraph(EdifikanaNavGraphDestination.AccountNavGraphDestination),
                    awaitItem()
                )
            }
        }
        viewModel.navigateToAccount()
        verificationJob.join()
    }

    @Test
    fun `test navigateToNotifications emits NavigateToScreen event`() = runCoroutineTest {
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(AccountDestination.NotificationsDestination),
                    awaitItem()
                )
            }
        }
        viewModel.navigateToNotifications()
        verificationJob.join()
    }

    @Test
    fun `test navigateToSettings emits NavigateToNavGraph event`() = runCoroutineTest {
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToNavGraph(EdifikanaNavGraphDestination.SettingsNavGraphDestination),
                    awaitItem()
                )
            }
        }
        viewModel.navigateToSettings()
        verificationJob.join()
    }

    @Test
    fun `test loadContent with existing tab selection preserves tab`() = runCoroutineTest {
        // Set up - First load with no properties
        coEvery { propertyManager.getPropertyList() } returns Result.success(emptyList())
        viewModel.loadContent()
        assertEquals(Tabs.GoToOrganization, viewModel.uiState.value.selectedTab)

        // Now simulate having properties
        val properties = listOf(
            PropertyModel(
                id = PropertyId("property-1"),
                name = "Test Property",
                address = "123 Main St",
                organizationId = OrganizationId("org-1"),
            )
        )
        coEvery { propertyManager.getPropertyList() } returns Result.success(properties)

        // Act - load content again
        viewModel.loadContent()

        // Assert - should switch to EventLog since we were on GoToOrganization
        assertEquals(Tabs.EventLog, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun `test loadContent maintains EventLog tab when already selected`() = runCoroutineTest {
        // Set up
        val properties = listOf(
            PropertyModel(
                id = PropertyId("property-1"),
                name = "Test Property",
                address = "123 Main St",
                organizationId = OrganizationId("org-1"),
            )
        )
        coEvery { propertyManager.getPropertyList() } returns Result.success(properties)

        // Act - first load
        viewModel.loadContent()
        assertEquals(Tabs.EventLog, viewModel.uiState.value.selectedTab)

        // Manually select a different tab (simulating user interaction)
        viewModel.selectTab(Tabs.EventLog)

        // Act - reload content
        viewModel.loadContent()

        // Assert - EventLog should remain selected
        assertEquals(Tabs.EventLog, viewModel.uiState.value.selectedTab)
    }
}

