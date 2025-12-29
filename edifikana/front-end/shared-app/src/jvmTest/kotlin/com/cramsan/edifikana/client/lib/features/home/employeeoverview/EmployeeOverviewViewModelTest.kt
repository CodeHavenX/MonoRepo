package com.cramsan.edifikana.client.lib.features.home.employeeoverview

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.home.HomeDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.models.Invite
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
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
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EmployeeOverviewViewModelTest : CoroutineTest() {

    private lateinit var viewModel: EmployeeOverviewViewModel
    private lateinit var authManager: AuthManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>

    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        authManager = mockk(relaxed = true)
        viewModel = EmployeeOverviewViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventReceiver,
            ),
            authManager = authManager
        )
    }

    @Test
    fun `test initial state`() = runCoroutineTest {
        assertEquals(true, viewModel.uiState.value.isLoading)
        assertEquals(null, viewModel.uiState.value.orgId)
        assertEquals(emptyList(), viewModel.uiState.value.employeeList)
        assertEquals(emptyList(), viewModel.uiState.value.inviteList)
    }

    @Test
    fun `test initialize without orgId does not load data`() = runCoroutineTest {
        viewModel.initialize()

        coVerify(exactly = 0) { authManager.getUsers(any()) }
        coVerify(exactly = 0) { authManager.getInvites(any()) }
    }

    @Test
    fun `test setOrgId sets orgId and loads employees and invites`() = runCoroutineTest {
        val organizationId = OrganizationId("org_id_1")
        val users = listOf(
            UserModel(
                id = UserId("user-1"),
                email = "john@example.com",
                phoneNumber = "1234567890",
                firstName = "John",
                lastName = "Doe",
            ),
            UserModel(
                id = UserId("user-2"),
                email = "jane@example.com",
                phoneNumber = "0987654321",
                firstName = "Jane",
                lastName = "Smith",
            ),
        )
        val invites = listOf(
            Invite(
                id = InviteId("invite-1"),
                email = "pending@example.com",
            ),
        )

        coEvery { authManager.getUsers(organizationId) } returns Result.success(users)
        coEvery { authManager.getInvites(organizationId) } returns Result.success(invites)

        viewModel.setOrgId(organizationId)

        assertEquals(organizationId, viewModel.uiState.value.orgId)
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(2, viewModel.uiState.value.employeeList.size)
        assertEquals(1, viewModel.uiState.value.inviteList.size)

        // Verify employee mapping
        assertEquals(UserId("user-1"), viewModel.uiState.value.employeeList[0].id)
        assertEquals("John Doe", viewModel.uiState.value.employeeList[0].name)
        assertEquals("john@example.com", viewModel.uiState.value.employeeList[0].email)

        assertEquals(UserId("user-2"), viewModel.uiState.value.employeeList[1].id)
        assertEquals("Jane Smith", viewModel.uiState.value.employeeList[1].name)
        assertEquals("jane@example.com", viewModel.uiState.value.employeeList[1].email)

        // Verify invite mapping
        assertEquals("pending@example.com", viewModel.uiState.value.inviteList[0].email)

        coVerify { authManager.getUsers(organizationId) }
        coVerify { authManager.getInvites(organizationId) }
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `test setOrgId with user without name uses email as name`() = runCoroutineTest {
        val organizationId = OrganizationId("org_id_1")
        val users = listOf(
            UserModel(
                id = UserId("user-1"),
                email = "noname@example.com",
                phoneNumber = "1234567890",
                firstName = "",
                lastName = "",
            ),
        )

        coEvery { authManager.getUsers(organizationId) } returns Result.success(users)
        coEvery { authManager.getInvites(organizationId) } returns Result.success(emptyList())

        viewModel.setOrgId(organizationId)

        assertEquals(1, viewModel.uiState.value.employeeList.size)
        assertEquals("noname@example.com", viewModel.uiState.value.employeeList[0].name)
    }

    @Test
    fun `test setOrgId with getUsers failure shows error snackbar`() = runCoroutineTest {
        val organizationId = OrganizationId("org_id_1")

        coEvery { authManager.getUsers(organizationId) } returns Result.failure(Exception("Network error"))
        coEvery { authManager.getInvites(organizationId) } returns Result.success(emptyList())

        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar("Failed to load employees: Network error"),
                    awaitItem()
                )
            }
        }

        viewModel.setOrgId(organizationId)
        verificationJob.join()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(emptyList(), viewModel.uiState.value.employeeList)
    }

    @Test
    fun `test setOrgId with getInvites failure shows error snackbar`() = runCoroutineTest {
        val organizationId = OrganizationId("org_id_1")

        coEvery { authManager.getUsers(organizationId) } returns Result.success(emptyList())
        coEvery { authManager.getInvites(organizationId) } returns Result.failure(Exception("Server error"))

        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar("Failed to load invites: Server error"),
                    awaitItem()
                )
            }
        }

        viewModel.setOrgId(organizationId)
        verificationJob.join()

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(emptyList(), viewModel.uiState.value.inviteList)
    }

    @Test
    fun `test setOrgId with both failures shows both error snackbars`() = runCoroutineTest {
        val organizationId = OrganizationId("org_id_1")

        coEvery { authManager.getUsers(organizationId) } returns Result.failure(Exception("Users error"))
        coEvery { authManager.getInvites(organizationId) } returns Result.failure(Exception("Invites error"))

        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar("Failed to load employees: Users error"),
                    awaitItem()
                )
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar("Failed to load invites: Invites error"),
                    awaitItem()
                )
            }
        }

        viewModel.setOrgId(organizationId)
        verificationJob.join()

        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `test navigateToAddEmployeeScreen emits NavigateToScreen event`() = runCoroutineTest {
        val organizationId = OrganizationId("org_id_1")

        coEvery { authManager.getUsers(organizationId) } returns Result.success(emptyList())
        coEvery { authManager.getInvites(organizationId) } returns Result.success(emptyList())

        viewModel.setOrgId(organizationId)

        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(
                        HomeDestination.InviteStaffMemberDestination(orgId = organizationId)
                    ),
                    awaitItem()
                )
            }
        }

        viewModel.navigateToAddEmployeeScreen()
        verificationJob.join()
    }

    @Test
    fun `test navigateToAddEmployeeScreen without orgId does not emit event`() = runCoroutineTest {
        val verificationJob = launch {
            windowEventBus.events.test {
                expectNoEvents()
            }
        }

        viewModel.navigateToAddEmployeeScreen()
        verificationJob.cancel()

        // No events should be emitted when orgId is null
    }

    @Test
    fun `test initialize after setOrgId loads data`() = runCoroutineTest {
        val organizationId = OrganizationId("org_id_1")
        val users = listOf(
            UserModel(
                id = UserId("user-1"),
                email = "test@example.com",
                phoneNumber = "1234567890",
                firstName = "Test",
                lastName = "User",
            ),
        )

        coEvery { authManager.getUsers(organizationId) } returns Result.success(users)
        coEvery { authManager.getInvites(organizationId) } returns Result.success(emptyList())

        // First set the orgId
        viewModel.setOrgId(organizationId)

        // Clear the employee list to simulate a refresh scenario
        coEvery { authManager.getUsers(organizationId) } returns Result.success(users)

        // Call initialize
        viewModel.initialize()

        assertEquals(1, viewModel.uiState.value.employeeList.size)
        coVerify(exactly = 2) { authManager.getUsers(organizationId) }
    }
}
