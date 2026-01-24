package com.cramsan.edifikana.client.lib.features.home.invitestaffmember

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserRole
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

class InviteStaffMemberViewModelTest : CoroutineTest() {

    private lateinit var viewModel: InviteStaffMemberViewModel
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
        viewModel = InviteStaffMemberViewModel(
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
    fun `test initialize sets orgId and roles`() = runCoroutineTest {
        val organizationId = OrganizationId("org_id_1")

        viewModel.initialize(organizationId)

        assertEquals(organizationId, viewModel.uiState.value.orgId)
        assertEquals(3, viewModel.uiState.value.roles.size)
        assertEquals(UserRole.ADMIN, viewModel.uiState.value.roles[0].role)
        assertEquals("Admin", viewModel.uiState.value.roles[0].displayName)
        assertEquals(UserRole.MANAGER, viewModel.uiState.value.roles[1].role)
        assertEquals("Manager", viewModel.uiState.value.roles[1].displayName)
        assertEquals(UserRole.EMPLOYEE, viewModel.uiState.value.roles[2].role)
        assertEquals("Employee", viewModel.uiState.value.roles[2].displayName)
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
    fun `test sendInvitation with empty email shows error snackbar`() = runCoroutineTest {
        val organizationId = OrganizationId("org_id_1")
        val role = StaffRoleUIModel(UserRole.ADMIN, "Admin")

        viewModel.initialize(organizationId)

        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar("Email cannot be empty."),
                    awaitItem()
                )
            }
        }

        viewModel.sendInvitation("", role)
        verificationJob.join()

        coVerify(exactly = 0) { authManager.inviteEmployee(any(), any(), any()) }
    }

    @Test
    fun `test sendInvitation with blank email shows error snackbar`() = runCoroutineTest {
        val organizationId = OrganizationId("org_id_1")
        val role = StaffRoleUIModel(UserRole.ADMIN, "Admin")

        viewModel.initialize(organizationId)

        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar("Email cannot be empty."),
                    awaitItem()
                )
            }
        }

        viewModel.sendInvitation("   ", role)
        verificationJob.join()

        coVerify(exactly = 0) { authManager.inviteEmployee(any(), any(), any()) }
    }

    @Test
    fun `test sendInvitation with invalid email format shows error snackbar`() = runCoroutineTest {
        val organizationId = OrganizationId("org_id_1")
        val role = StaffRoleUIModel(UserRole.ADMIN, "Admin")

        viewModel.initialize(organizationId)

        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar("Invalid email format."),
                    awaitItem()
                )
            }
        }

        viewModel.sendInvitation("invalid-email", role)
        verificationJob.join()

        coVerify(exactly = 0) { authManager.inviteEmployee(any(), any(), any()) }
    }

    @Test
    fun `test sendInvitation with null role shows error snackbar`() = runCoroutineTest {
        val organizationId = OrganizationId("org_id_1")
        val email = "test@example.com"

        viewModel.initialize(organizationId)

        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar("Please select a role"),
                    awaitItem()
                )
            }
        }

        viewModel.sendInvitation(email, null)
        verificationJob.join()

        coVerify(exactly = 0) { authManager.inviteEmployee(any(), any(), any()) }
    }

    @Test
    fun `test sendInvitation with valid data sends invitation and navigates back`() = runCoroutineTest {
        val organizationId = OrganizationId("org_id_1")
        val email = "test@example.com"
        val role = StaffRoleUIModel(UserRole.ADMIN, "Admin")

        coEvery { authManager.inviteEmployee(email, organizationId, UserRole.ADMIN) } returns Result.success(Unit)

        viewModel.initialize(organizationId)

        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar("Invitation sent to $email"),
                    awaitItem()
                )
                assertEquals(EdifikanaWindowsEvent.NavigateBack, awaitItem())
            }
        }

        viewModel.sendInvitation(email, role)
        verificationJob.join()

        coVerify { authManager.inviteEmployee(email, organizationId, UserRole.ADMIN) }
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `test sendInvitation with failure shows error snackbar`() = runCoroutineTest {
        val organizationId = OrganizationId("org_id_1")
        val email = "test@example.com"
        val role = StaffRoleUIModel(UserRole.ADMIN, "Admin")

        coEvery { authManager.inviteEmployee(email, organizationId, UserRole.ADMIN) } returns Result.failure(
            Exception("Error"),
        )

        viewModel.initialize(organizationId)

        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.ShowSnackbar("Failed to send invitation"),
                    awaitItem()
                )
            }
        }

        viewModel.sendInvitation(email, role)
        verificationJob.join()

        coVerify { authManager.inviteEmployee(email, organizationId, UserRole.ADMIN) }
        assertEquals(false, viewModel.uiState.value.isLoading)
    }
}
