package com.cramsan.edifikana.client.lib.features.settings.organizations.orgdetail

import app.cash.turbine.turbineScope
import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.edifikana.client.lib.features.settings.SettingsDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.MembershipManager
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
import com.cramsan.edifikana.client.lib.models.OrgMemberModel
import com.cramsan.edifikana.client.lib.models.Organization
import com.cramsan.edifikana.client.lib.settings.EdifikanaSettingKey
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
import kotlin.test.assertTrue

class OrgDetailViewModelTest : CoroutineTest() {

    private lateinit var viewModel: OrgDetailViewModel
    private lateinit var organizationManager: OrganizationManager
    private lateinit var membershipManager: MembershipManager
    private lateinit var authManager: AuthManager
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventBus: EventBus<ApplicationEvent>

    private val currentUserId = UserId("user-1")
    private val orgId = OrganizationId("org-1")

    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventBus = EventBus()
        windowEventBus = EventBus()
        organizationManager = mockk()
        membershipManager = mockk()
        authManager = mockk(relaxed = true)
        preferencesManager = mockk(relaxed = true)

        every { authManager.activeUser() } returns MutableStateFlow(currentUserId)

        viewModel = OrgDetailViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventBus,
            ),
            organizationManager = organizationManager,
            membershipManager = membershipManager,
            authManager = authManager,
            preferencesManager = preferencesManager,
        )
    }

    private fun memberModel(userId: UserId = currentUserId, role: OrgRole = OrgRole.OWNER) =
        OrgMemberModel(
            userId = userId,
            orgId = orgId,
            role = role,
            status = OrgMemberStatus.ACTIVE,
            joinedAt = null,
            displayName = "Test User",
            email = "test@example.com",
        )

    @Test
    fun `initialize populates state with org details`() = runCoroutineTest {
        val org = Organization(id = orgId, name = "My Org", description = "")
        coEvery { organizationManager.getOrganization(orgId) } returns Result.success(org)
        coEvery { membershipManager.listMembers(orgId) } returns Result.success(listOf(memberModel()))
        coEvery { preferencesManager.getStringPreference(EdifikanaSettingKey.lastSelectedOrganization) } returns Result.success(orgId.id)

        viewModel.initialize(orgId)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("My Org", state.orgName)
        assertEquals(true, state.isActiveOrg)
        assertEquals(OrgRole.OWNER, state.userRole)
        assertEquals(1, state.memberCount)
        assertTrue(state.isSoleOwner)
    }

    @Test
    fun `initialize sets isSoleOwner false when multiple owners exist`() = runCoroutineTest {
        val org = Organization(id = orgId, name = "My Org", description = "")
        val members = listOf(
            memberModel(userId = currentUserId, role = OrgRole.OWNER),
            memberModel(userId = UserId("user-2"), role = OrgRole.OWNER),
        )
        coEvery { organizationManager.getOrganization(orgId) } returns Result.success(org)
        coEvery { membershipManager.listMembers(orgId) } returns Result.success(members)
        coEvery { preferencesManager.getStringPreference(EdifikanaSettingKey.lastSelectedOrganization) } returns Result.success(null)

        viewModel.initialize(orgId)

        assertFalse(viewModel.uiState.value.isSoleOwner)
    }

    @Test
    fun `initialize shows snackbar on org load failure`() = runCoroutineTest {
        coEvery { organizationManager.getOrganization(orgId) } returns Result.failure(RuntimeException("error"))
        coEvery { membershipManager.listMembers(orgId) } returns Result.success(emptyList())
        coEvery { preferencesManager.getStringPreference(EdifikanaSettingKey.lastSelectedOrganization) } returns Result.success(null)

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.initialize(orgId)

            assertEquals(EdifikanaWindowsEvent.ShowSnackbar("Failed to load organization details"), turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `onLeaveOrganizationTapped sets showLeaveDialog true`() = runCoroutineTest {
        viewModel.onLeaveOrganizationTapped()

        assertTrue(viewModel.uiState.value.showLeaveDialog)
    }

    @Test
    fun `dismissLeaveDialog sets showLeaveDialog false`() = runCoroutineTest {
        viewModel.onLeaveOrganizationTapped()
        viewModel.dismissLeaveDialog()

        assertFalse(viewModel.uiState.value.showLeaveDialog)
    }

    @Test
    fun `confirmLeaveOrganization on success navigates to MyOrganizations`() = runCoroutineTest {
        coEvery { membershipManager.leaveOrganization(orgId) } returns Result.success(Unit)

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.confirmLeaveOrganization(orgId)

            val event = turbine.awaitItem() as EdifikanaWindowsEvent.NavigateToScreen
            assertEquals(SettingsDestination.MyOrganizationsDestination, event.destination)
            assertTrue(event.clearStack)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `confirmLeaveOrganization on failure shows snackbar`() = runCoroutineTest {
        coEvery { membershipManager.leaveOrganization(orgId) } returns Result.failure(RuntimeException("error"))

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.confirmLeaveOrganization(orgId)

            assertEquals(EdifikanaWindowsEvent.ShowSnackbar("Failed to leave organization"), turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `onTransferOwnershipTapped navigates to TransferOwnership screen`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.onTransferOwnershipTapped(orgId)

            val event = turbine.awaitItem() as EdifikanaWindowsEvent.NavigateToScreen
            assertEquals(SettingsDestination.TransferOwnershipDestination(orgId), event.destination)
            advanceUntilIdleAndAwaitComplete(turbine)
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
