package com.cramsan.edifikana.client.lib.features.admin.addsecondaryemployee

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.management.addsecondaryemployee.AddSecondaryEmployeeViewModel
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
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
import edifikana_lib.text_please_complete_fields
import edifikana_lib.text_there_was_an_error_processing_request
import edifikana_lib.title_timecard_add_employee
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AddSecondaryEmployeeViewModelTest : CoroutineTest() {

    private lateinit var viewModel: AddSecondaryEmployeeViewModel
    private lateinit var employeeManager: EmployeeManager
    private lateinit var propertyManager: PropertyManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var stringProvider: StringProvider

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        employeeManager = mockk(relaxed = true)
        propertyManager = mockk(relaxed = true)
        stringProvider = mockk()
        windowEventBus = EventBus()
        viewModel = AddSecondaryEmployeeViewModel(
            employeeManager = employeeManager,
            propertyManager = propertyManager,
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
                windowEventReceiver = windowEventBus,
            ),
            stringProvider = stringProvider,
        )
    }

    @Test
    fun `test onBackSelected emits NavigateBack event`() = runCoroutineTest {
        // Setup

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateBack,
                    awaitItem()
                )
            }
        }
        viewModel.onBackSelected()

        // Assert
        verificationJob.join()
    }

    @Test
    fun `test saveEmployee with invalid data shows error snackbar`() = runCoroutineTest {
        // Setup
        coEvery { stringProvider.getString(Res.string.text_please_complete_fields) } returns "Please complete all fields."

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar("Please complete all fields."),
                    awaitItem()
                )
            }
        }
        viewModel.saveEmployee(null, null, null, null, null)

        // Assert
        verificationJob.join()
    }

    @Test
    fun `test saveEmployee with valid data saves employee and navigates back`() = runCoroutineTest {
        // Setup
        val id = "123"
        val idType = IdType.DNI
        val name = "John"
        val lastName = "Doe"
        val role = EmployeeRole.SECURITY
        val propertyId = PropertyId("propertyId")
        coEvery { propertyManager.activeProperty() } returns MutableStateFlow(propertyId)
        coEvery { employeeManager.addEmployee(any()) } returns Result.success(mockk())
        coEvery { stringProvider.getString(Res.string.title_timecard_add_employee) } returns "Add Employee"

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(EdifikanaWindowsEvent.NavigateBack, awaitItem())
            }
        }

        viewModel.saveEmployee(id, idType, name, lastName, role)

        // Assert
        verificationJob.join()
        coVerify {
            employeeManager.addEmployee(
                EmployeeModel.CreateEmployeeRequest(
                    idType = idType,
                    firstName = name,
                    lastName = lastName,
                    role = role,
                    propertyId = propertyId
                )
            )
        }
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `test saveEmployee with valid data but failure response shows error snackbar`() = runCoroutineTest {
        // Setup
        val id = "123"
        val idType = IdType.PASSPORT
        val name = "John"
        val lastName = "Doe"
        val role = EmployeeRole.SECURITY
        val propertyId = PropertyId("propertyId")
        coEvery { propertyManager.activeProperty() } returns MutableStateFlow(propertyId)
        coEvery { employeeManager.addEmployee(any()) } returns Result.failure(Exception("Error"))
        coEvery { stringProvider.getString(Res.string.title_timecard_add_employee) } returns "Add Employee"
        coEvery { stringProvider.getString(Res.string.text_there_was_an_error_processing_request) } returns "There was an error processing the request."

        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar("There was an error processing the request."),
                    awaitItem()
                )
            }
        }

        viewModel.saveEmployee(id, idType, name, lastName, role)

        // Assert
        verificationJob.join()
        coVerify {
            employeeManager.addEmployee(
                EmployeeModel.CreateEmployeeRequest(
                    idType = idType,
                    firstName = name,
                    lastName = lastName,
                    role = role,
                    propertyId = propertyId
                )
            )
        }
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }
}