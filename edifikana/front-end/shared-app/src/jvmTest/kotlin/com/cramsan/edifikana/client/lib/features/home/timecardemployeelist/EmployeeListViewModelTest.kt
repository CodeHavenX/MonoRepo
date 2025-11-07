package com.cramsan.edifikana.client.lib.features.home.timecardemployeelist

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.home.HomeDestination
import com.cramsan.edifikana.client.lib.features.home.employeelist.EmployeeListViewModel
import com.cramsan.edifikana.client.lib.features.home.employeelist.EmployeeMemberUIModel
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.client.lib.models.Organization
import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.OrganizationId
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EmployeeListViewModelTest : CoroutineTest() {

    private lateinit var viewModel: EmployeeListViewModel
    private lateinit var employeeManager: EmployeeManager
    private lateinit var authManager: AuthManager
    private lateinit var organizationManager: OrganizationManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var activeOrganization: MutableStateFlow<Organization?>

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        employeeManager = mockk(relaxed = true)
        windowEventBus = EventBus()
        authManager = mockk(relaxed = true)
        organizationManager = mockk(relaxed = true)
        activeOrganization = MutableStateFlow(null)
        coEvery { organizationManager.observeActiveOrganization() } returns activeOrganization
        viewModel = EmployeeListViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
                windowEventReceiver = windowEventBus,
            ),
            employeeManager = employeeManager,
            authManager = authManager,
            organizationManager = organizationManager,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test loadEmployeeList updates UI state with employee list`() = runCoroutineTest {
        val employeeList = listOf(
            EmployeeModel(
                id = EmployeeId("1"),
                firstName = "John",
                email = "john@example.com",
                idType = IdType.PASSPORT,
                lastName = "Doe",
                role = EmployeeRole.SECURITY_COVER,
            ),
            EmployeeModel(
                id = EmployeeId("2"),
                firstName = "Jane",
                email = "jane@example.com",
                idType = IdType.DNI,
                lastName = "Smith",
                role = EmployeeRole.MANAGER,
            )
        )
        val orgId = OrganizationId("org1")
        val organization = Organization(
            id = orgId,
        )
        coEvery { employeeManager.getEmployeeList() } returns Result.success(employeeList)
        coEvery { authManager.getUsers(orgId) } returns Result.success(emptyList())
        coEvery { authManager.getInvites(orgId) } returns Result.success(emptyList())
        activeOrganization.value = organization
        advanceUntilIdle()

        viewModel.loadEmployeeList()

        assertEquals(
            listOf(
                EmployeeMemberUIModel(
                    employeeId = EmployeeId("2"),
                    name = "Jane",
                    email = "jane@example.com",
                ),
                EmployeeMemberUIModel(
                    employeeId = EmployeeId("1"),
                    name = "John",
                    email = "john@example.com",
                ),
            ),
            viewModel.uiState.value.employeeList
        )
        assertTrue(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun `test loadEmployeeList with failure updates UI state with empty list`() = runCoroutineTest {
        coEvery { employeeManager.getEmployeeList() } returns Result.failure(Exception("Error"))

        viewModel.loadEmployeeList()

        assertTrue(viewModel.uiState.value.employeeList.isEmpty())
        assertTrue(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun `test navigateToAddPrimaryEmployee emits NavigateToScreen event`() = runCoroutineTest {
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(HomeDestination.AddPrimaryEmployeeManagementDestination),
                    awaitItem()
                )
            }
        }

        viewModel.navigateToAddPrimaryEmployee()
        verificationJob.join()
    }

    @Test
    fun `test navigateToAddSecondaryEmployee emits NavigateToScreen event`() = runCoroutineTest {
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(HomeDestination.AddSecondaryEmployeeManagementDestination),
                    awaitItem()
                )
            }
        }

        viewModel.navigateToAddSecondaryEmployee()
        verificationJob.join()
    }

    @Test
    fun `test navigateToEmployee emits NavigateToScreen event`() = runCoroutineTest {
        val employeeId = EmployeeId("123")
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(HomeDestination.EmployeeDestination(employeeId)),
                    awaitItem()
                )
            }
        }

        viewModel.navigateToEmployee(employeeId)
        verificationJob.join()
    }
}