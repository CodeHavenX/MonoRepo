package com.cramsan.edifikana.client.lib.features.main.timecard.employeelist

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.management.ManagementDestination
import com.cramsan.edifikana.client.lib.features.management.timecardemployeelist.TimeCardEmployeeListViewModel
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
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import edifikana_lib.Res
import edifikana_lib.title_timecard_employee_list
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

class EmployeeListViewModelTest : CoroutineTest() {

    private lateinit var employeeManager: EmployeeManager
    private lateinit var viewModel: TimeCardEmployeeListViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var stringProvider: StringProvider

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        employeeManager = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        stringProvider = mockk()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
            windowEventReceiver = windowEventBus,
        )
        viewModel = TimeCardEmployeeListViewModel(
            employeeManager,
            stringProvider = stringProvider,
            dependencies,
        )
    }

    @Test
    fun `test loadEmployees successfully loads employee members`() = runCoroutineTest {
        // Arrange
        val employeeList = listOf(
            EmployeeModel(
                id = EmployeeId("1"),
                idType = IdType.OTHER,
                firstName = "Employee 1",
                lastName = "Last 1",
                role = EmployeeRole.SECURITY,
                email = "employee1@test.com",
            )
        )
        coEvery { employeeManager.getEmployeeList() } returns Result.success(employeeList)
        coEvery { stringProvider.getString(Res.string.title_timecard_employee_list) } returns "Employee List"

        // Act
        viewModel.loadEmployees()

        // Assert
        coVerify { employeeManager.getEmployeeList() }
        val uiState = viewModel.uiState.value
        assertEquals(1, uiState.employees.size)
        assertEquals("Employee 1 Last 1", uiState.employees[0].fullName)
        assertEquals(false, uiState.isLoading)
    }

    @Test
    fun `test loadEmployees handles failure`() = runCoroutineTest {
        // Arrange
        coEvery { employeeManager.getEmployeeList() } returns Result.failure(Exception("Error"))
        coEvery { stringProvider.getString(Res.string.title_timecard_employee_list) } returns "Employee List"

        // Act
        viewModel.loadEmployees()

        // Assert
        coVerify { employeeManager.getEmployeeList() }
        val uiState = viewModel.uiState.value
        assertEquals(0, uiState.employees.size)
        assertEquals(false, uiState.isLoading)
    }

    @Test
    fun `test navigateToEmployee emits NavigateToScreen event`() = runCoroutineTest {
        // Arrange
        val employeeId = EmployeeId("123")

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(
                        ManagementDestination.TimeCardSingleEmployeeDestination(employeeId)
                    ),
                    awaitItem(),
                )
            }
        }
        viewModel.navigateToEmployee(employeeId)

        // Assert
        verificationJob.join()
    }

    @Test
    fun `test navigateBack emits NavigateBack event`() = runCoroutineTest {
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
}