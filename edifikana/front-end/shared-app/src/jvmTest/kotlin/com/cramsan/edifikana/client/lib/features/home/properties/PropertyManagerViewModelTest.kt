package com.cramsan.edifikana.client.lib.features.home.properties

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.home.HomeDestination
import com.cramsan.edifikana.client.lib.features.home.properties.PropertyManagerUIModel
import com.cramsan.edifikana.client.lib.features.home.properties.PropertyManagerUIState
import com.cramsan.edifikana.client.lib.features.home.properties.PropertyManagerViewModel
import com.cramsan.edifikana.client.lib.features.home.properties.PropertyUIModel
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
import io.mockk.mockk
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class PropertyManagerViewModelTest : CoroutineTest() {

    private lateinit var viewModel: PropertyManagerViewModel
    private lateinit var propertyManager: PropertyManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        windowEventBus = EventBus()
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
    fun `test loadPage updates UI state with property list`() = runCoroutineTest {
        val organizationId = OrganizationId("org_id_1")
        val properties = listOf(
            PropertyModel(PropertyId("1"), "Property 1", "Address 1", organizationId),
            PropertyModel(PropertyId("2"), "Property 2", "Address 2", organizationId)
        )
        coEvery { propertyManager.getPropertyList() } returns Result.success(properties)

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
    fun `test loadPage with failure updates UI state with loading false`() = runCoroutineTest {
        coEvery { propertyManager.getPropertyList() } returns Result.failure(Exception("Error"))

        viewModel.loadPage()

        assertEquals(
            PropertyManagerUIState.Empty.copy(isLoading = false),
            viewModel.uiState.value
        )
    }

    @Test
    fun `test navigateToPropertyDetails emits NavigateToScreen event`() = runCoroutineTest {
        val propertyId = PropertyId("123")
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(
                        HomeDestination.PropertyManagementDestination(propertyId)
                    ),
                    awaitItem()
                )
            }
        }

        viewModel.navigateToPropertyDetails(propertyId)
        verificationJob.join()
    }

    @Test
    fun `test navigateToAddProperty emits NavigateToScreen event`() = runCoroutineTest {
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(
                        HomeDestination.AddPropertyManagementDestination
                    ),
                    awaitItem()
                )
            }
        }

        viewModel.navigateToAddProperty()
        verificationJob.join()
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
}