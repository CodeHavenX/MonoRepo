package com.cramsan.edifikana.client.lib.features.admin.addproperty

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
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
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddPropertyViewModelTest : TestBase() {

    private lateinit var viewModel: AddPropertyViewModel
    private lateinit var propertyManager: PropertyManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        propertyManager = mockk(relaxed = true)
        viewModel = AddPropertyViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventReceiver,
            ),
            propertyManager = propertyManager
        )
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

    @Test
    fun `test addProperty with valid data adds property and navigates back`() = runBlockingTest {
        val propertyName = "Test Property"
        val address = "123 Test Street"
        val newProperty = PropertyModel(
            id = PropertyId("test-id"),
            name = propertyName,
            address = address,
        )
        coEvery { propertyManager.addProperty(propertyName, address) } returns Result.success(newProperty)

        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar(
                        "Property $propertyName added successfully"
                    ),
                    awaitItem()
                )
                assertEquals(EdifikanaWindowsEvent.NavigateBack, awaitItem())
            }
        }

        viewModel.addProperty(propertyName, address)
        verificationJob.join()

        coVerify { propertyManager.addProperty(propertyName, address) }
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `test addProperty with failure updates UI state with error`() = runBlockingTest {
        val propertyName = "Test Property"
        val address = "123 Test Street"
        coEvery { propertyManager.addProperty(propertyName, address) } returns Result.failure(Exception("Error"))

        viewModel.addProperty(propertyName, address)

        assertEquals(1, exceptionHandler.exceptions.size)
        assertEquals(false, viewModel.uiState.value.isLoading)
        // TODO: Update this test once error handling is implemented in addProperty
    }
}