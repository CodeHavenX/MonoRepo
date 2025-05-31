package com.cramsan.edifikana.client.lib.features.main.home

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.ActivityRouteDestination
import com.cramsan.edifikana.client.lib.features.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.features.account.AccountRouteDestination
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.models.PropertyModel
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
import com.cramsan.framework.test.TestBase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest : TestBase() {

    private lateinit var propertyManager: PropertyManager
    private lateinit var viewModel: HomeViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        propertyManager = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
            windowEventReceiver = windowEventBus,
        )
        viewModel = HomeViewModel(dependencies, propertyManager)
    }

    @Test
    fun `test loadContent successfully loads properties`() = runBlockingTest {
        // Arrange
        val properties = listOf(
            PropertyModel(id = PropertyId("1"), name = "Property 1", address = "Address 1"),
            PropertyModel(id = PropertyId("2"), name = "Property 2", address = "Address 2"),
        )
        val activeProperty = MutableStateFlow(PropertyId("1"))
        coEvery { propertyManager.getPropertyList() } returns Result.success(properties)
        coEvery { propertyManager.activeProperty() } returns activeProperty

        // Act
        viewModel.loadContent()

        // Assert
        coVerify { propertyManager.getPropertyList() }
        val uiState = viewModel.uiState.value
        assertEquals("Property 1", uiState.label)
        assertEquals(2, uiState.availableProperties.size)
    }

    @Test
    fun `test selectProperty updates active property`() = runBlockingTest {
        // Arrange
        val propertyId = PropertyId("1")
        coEvery { propertyManager.setActiveProperty(propertyId) } returns Result.success(Unit)
        coEvery { propertyManager.getPropertyList() } returns Result.success(emptyList())
        coEvery { propertyManager.activeProperty() } returns MutableStateFlow(propertyId)

        // Act
        viewModel.selectProperty(propertyId)

        // Assert
        coVerify { propertyManager.setActiveProperty(propertyId) }
    }

    @Test
    fun `test navigateBack emits NavigateBack event`() = runBlockingTest {
        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateBack,
                    awaitItem(),
                )
            }
        }
        viewModel.navigateBack()

        // Assert
        verificationJob.join()
    }

    @Test
    fun `test navigateToAccount emits NavigateToActivity event`() = runBlockingTest {
        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToActivity(
                        ActivityRouteDestination.AccountRouteDestination
                    ),
                    awaitItem(),
                )
            }
        }
        viewModel.navigateToAccount()

        // Assert
        verificationJob.join()
    }

    @Test
    fun `test navigateToNotifications emits NavigateToScreen event`() = runBlockingTest {
        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(
                        AccountRouteDestination.NotificationsDestination
                    ),
                    awaitItem(),
                )
            }
        }
        viewModel.navigateToNotifications()

        // Assert
        verificationJob.join()
    }
}