package com.cramsan.edifikana.client.lib.features.admin.employee

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.management.employee.EmployeeViewModel
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole

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
import kotlin.test.assertTrue

class EmployeeViewModelTest : CoroutineTest() {

    private lateinit var viewModel: EmployeeViewModel
    private lateinit var employeeManager: EmployeeManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        employeeManager = mockk(relaxed = true)
        viewModel = EmployeeViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
                windowEventReceiver = windowEventBus,
            ),
            employeeManager = employeeManager
        )
    }

    @Test
    fun `test loadEmployee with valid employeeId updates UI state`() = runCoroutineTest {
        val employeeId = EmployeeId("123")
        val employee = EmployeeModel(
            id = employeeId,
            idType = IdType.DNI,
            firstName = "John",
            lastName = "Doe",
            role = EmployeeRole.SECURITY_COVER,
            email = "test@test.com",
        )
        coEvery { employeeManager.getEmployee(employeeId) } returns Result.success(employee)

        viewModel.loadEmployee(employeeId)

        assertEquals("John Doe", viewModel.uiState.value.title)
        assertEquals(IdType.DNI, viewModel.uiState.value.idType)
        assertEquals("John", viewModel.uiState.value.firstName)
        assertEquals("Doe", viewModel.uiState.value.lastName)
        assertEquals(EmployeeRole.SECURITY_COVER, viewModel.uiState.value.role)
        assertTrue(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun `test loadEmployee with invalid employeeId updates UI state with empty title`() = runCoroutineTest {
        val employeeId = EmployeeId("123")
        coEvery { employeeManager.getEmployee(employeeId) } returns Result.failure(Exception("Error"))

        viewModel.loadEmployee(employeeId)

        assertEquals("", viewModel.uiState.value.title)
        assertTrue(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun `test onBackSelected emits NavigateBack event`() = runCoroutineTest {
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(EdifikanaWindowsEvent.NavigateBack, awaitItem())
            }
        }
        viewModel.onBackSelected()
        verificationJob.join()
    }
}