package com.cramsan.edifikana.client.lib.features.settings.organizations.transferownership

import app.cash.turbine.turbineScope
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.MembershipManager
import com.cramsan.edifikana.client.lib.models.OrgMemberModel
import com.cramsan.edifikana.lib.model.organization.OrgMemberStatus
import com.cramsan.edifikana.lib.model.organization.OrgRole
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.user.UserId
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
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs

class TransferOwnershipViewModelTest : CoroutineTest() {

    private lateinit var viewModel: TransferOwnershipViewModel
    private lateinit var membershipManager: MembershipManager
    private lateinit var authManager: AuthManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventBus: EventBus<ApplicationEvent>

    private val currentUserId = UserId("user-owner")
    private val orgId = OrganizationId("org-1")
    private val adminUserId = UserId("user-admin")

    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventBus = EventBus()
        windowEventBus = EventBus()
        membershipManager = mockk()
        authManager = mockk(relaxed = true)

        every { authManager.activeUser() } returns MutableStateFlow(currentUserId)

        viewModel = TransferOwnershipViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventBus,
            ),
            membershipManager = membershipManager,
            authManager = authManager,
        )
    }

    private fun memberModel(userId: UserId, role: OrgRole) =
        OrgMemberModel(
            userId = userId,
            orgId = orgId,
            role = role,
            status = OrgMemberStatus.ACTIVE,
            joinedAt = null,
            displayName = if (userId == adminUserId) "Bob Admin" else "Owner",
            email = if (userId == adminUserId) "bob@example.com" else "owner@example.com",
        )

    @Test
    fun `initialize loads eligible admins excluding current user`() = runCoroutineTest {
        val members = listOf(
            memberModel(currentUserId, OrgRole.OWNER),
            memberModel(adminUserId, OrgRole.ADMIN),
            memberModel(UserId("user-emp"), OrgRole.EMPLOYEE),
        )
        coEvery { membershipManager.listMembers(orgId) } returns Result.success(members)

        viewModel.initialize(orgId)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.eligibleAdmins.size)
        assertEquals(adminUserId, state.eligibleAdmins[0].userId)
        assertEquals("Bob Admin", state.eligibleAdmins[0].displayName)
    }

    @Test
    fun `initialize shows snackbar on failure`() = runCoroutineTest {
        coEvery { membershipManager.listMembers(orgId) } returns Result.failure(RuntimeException("error"))

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.initialize(orgId)

            assertEquals(EdifikanaWindowsEvent.ShowSnackbar("Failed to load members"), turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `onAdminSelected sets dialog to ConfirmTransfer`() = runCoroutineTest {
        val admin = AdminUIModel(userId = adminUserId, displayName = "Bob Admin", email = "bob@example.com")

        viewModel.onAdminSelected(admin)

        val dialog = assertIs<TransferOwnershipDialogState.ConfirmTransfer>(viewModel.uiState.value.dialog)
        assertEquals(admin, dialog.target)
    }

    @Test
    fun `dismissDialog sets dialog to None`() = runCoroutineTest {
        val admin = AdminUIModel(userId = adminUserId, displayName = "Bob Admin", email = "bob@example.com")
        viewModel.onAdminSelected(admin)

        viewModel.dismissDialog()

        assertIs<TransferOwnershipDialogState.None>(viewModel.uiState.value.dialog)
    }

    @Test
    fun `confirmTransferOwnership on success navigates back`() = runCoroutineTest {
        val admin = AdminUIModel(userId = adminUserId, displayName = "Bob Admin", email = "bob@example.com")
        viewModel.onAdminSelected(admin)
        coEvery { membershipManager.transferOwnership(orgId, adminUserId) } returns Result.success(Unit)

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.confirmTransferOwnership(orgId)

            assertEquals(EdifikanaWindowsEvent.NavigateBack, turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `confirmTransferOwnership on failure shows snackbar`() = runCoroutineTest {
        val admin = AdminUIModel(userId = adminUserId, displayName = "Bob Admin", email = "bob@example.com")
        viewModel.onAdminSelected(admin)
        coEvery { membershipManager.transferOwnership(orgId, adminUserId) } returns Result.failure(RuntimeException("error"))

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.confirmTransferOwnership(orgId)

            assertEquals(EdifikanaWindowsEvent.ShowSnackbar("Failed to transfer ownership"), turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `confirmTransferOwnership does nothing when no target selected`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.confirmTransferOwnership(orgId)

            turbine.expectNoEvents()
            turbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `navigateBack emits NavigateBack`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateBack()

            assertEquals(EdifikanaWindowsEvent.NavigateBack, turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }
}
