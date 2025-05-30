package com.cramsan.edifikana.client.lib.features.admin.properties

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.features.management.ManagementDestination
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEventBus
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.TestBase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PropertyManagerViewModelTest : TestBase() {

    private lateinit var viewModel: PropertyManagerViewModel
    private lateinit var propertyManager: PropertyManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: WindowEventBus
    private lateinit var applicationEventReceiver: ApplicationEventBus

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = ApplicationEventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        windowEventBus = WindowEventBus()
        propertyManager = mockk(relaxed = true)
        viewModel = PropertyManagerViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
                windowEventReceiver = windowEventBus
            ),
            propertyManager = propertyManager
        )
    }

    @Test
    fun `test loadPage updates UI state with property list`() = runBlockingTest {
        val properties = listOf(
            PropertyModel(PropertyId("1"), "Property 1", "Address 1"),
            PropertyModel(PropertyId("2"), "Property 2", "Address 2")
        )
        coEvery { propertyManager.getPropertyList(showAll = true) } returns Result.success(properties)

        viewModel.loadPage()

        assertEquals(
            PropertyManagerUIState(
                content = PropertyManagerUIModel(
                    listOf(
                        PropertyUIModel(PropertyId("1"), "Property 1", "Address 1"),
                        PropertyUIModel(PropertyId("2"), "Property 2", "Address 2")
                    )
                ),
                isLoading = false
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `test loadPage with failure updates UI state with loading false`() = runBlockingTest {
        coEvery { propertyManager.getPropertyList(showAll = true) } returns Result.failure(Exception("Error"))

        viewModel.loadPage()

        assertEquals(
            PropertyManagerUIState.Empty.copy(isLoading = false),
            viewModel.uiState.value
        )
    }

    @Test
    fun `test navigateToPropertyDetails emits NavigateToScreen event`() = runBlockingTest {
        val propertyId = PropertyId("123")
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(
                        ManagementDestination.PropertyManagementDestination(propertyId)
                    ),
                    awaitItem()
                )
            }
        }

        viewModel.navigateToPropertyDetails(propertyId)
        verificationJob.join()
    }

    @Test
    fun `test navigateToAddProperty emits NavigateToScreen event`() = runBlockingTest {
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(
                        ManagementDestination.AddPropertyManagementDestination
                    ),
                    awaitItem()
                )
            }
        }

        viewModel.navigateToAddProperty()
        verificationJob.join()
    }

    @Test
    fun `test navigateBack emits NavigateBack event`() = runBlockingTest {
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
}