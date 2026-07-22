package com.cramsan.edifikana.client.lib.features.settings.organizations.myorganizations

import app.cash.turbine.turbineScope
import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.MembershipManager
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.models.OrgMemberModel
import com.cramsan.edifikana.client.lib.models.Organization
import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.client.lib.settings.EdifikanaSettingKey
import com.cramsan.edifikana.lib.model.organization.OrgMemberStatus
import com.cramsan.edifikana.lib.model.organization.OrgRole
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
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
import kotlin.test.assertIs

class MyOrganizationsViewModelTest : CoroutineTest() {

    private lateinit var viewModel: MyOrganizationsViewModel
    private lateinit var organizationManager: OrganizationManager
    private lateinit var membershipManager: MembershipManager
    private lateinit var authManager: AuthManager
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var propertyManager: PropertyManager
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
        propertyManager = mockk()

        every { authManager.activeUser() } returns MutableStateFlow(currentUserId)

        viewModel = MyOrganizationsViewModel(
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
            propertyManager = propertyManager,
        )
    }

    @Test
    fun `initialize loads orgs and maps membership roles`() = runCoroutineTest {
        val org = Organization(id = orgId, name = "Acme Corp", description = "")
        val member = OrgMemberModel(
            userId = currentUserId,
            orgId = orgId,
            role = OrgRole.OWNER,
            status = OrgMemberStatus.ACTIVE,
            joinedAt = null,
            displayName = "Alice",
            email = "alice@example.com",
        )
        val properties = listOf(
            PropertyModel(id = PropertyId("prop-1"), name = "Building A", address = "", organizationId = orgId),
            PropertyModel(id = PropertyId("prop-2"), name = "Building B", address = "", organizationId = orgId),
        )
        coEvery { organizationManager.getOrganizations() } returns Result.success(listOf(org))
        coEvery { membershipManager.listMembers(orgId) } returns Result.success(listOf(member))
        coEvery { preferencesManager.getStringPreference(EdifikanaSettingKey.lastSelectedOrganization) } returns Result.success(orgId.id)
        coEvery { propertyManager.getPropertyList() } returns Result.success(properties)

        viewModel.initialize()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(1, state.organizations.size)
        assertEquals(orgId, state.organizations[0].orgId)
        assertEquals("Acme Corp", state.organizations[0].name)
        assertEquals("Owner", state.organizations[0].roleLabel)
        assertEquals(2, state.organizations[0].propertyCount)
        assertEquals(true, state.organizations[0].isActive)
    }

    @Test
    fun `initialize defaults propertyCount to 0 when property fetch fails`() = runCoroutineTest {
        val org = Organization(id = orgId, name = "Acme Corp", description = "")
        val member = OrgMemberModel(
            userId = currentUserId,
            orgId = orgId,
            role = OrgRole.OWNER,
            status = OrgMemberStatus.ACTIVE,
            joinedAt = null,
            displayName = "Alice",
            email = "alice@example.com",
        )
        coEvery { organizationManager.getOrganizations() } returns Result.success(listOf(org))
        coEvery { membershipManager.listMembers(orgId) } returns Result.success(listOf(member))
        coEvery { preferencesManager.getStringPreference(EdifikanaSettingKey.lastSelectedOrganization) } returns Result.success(orgId.id)
        coEvery { propertyManager.getPropertyList() } returns Result.failure(RuntimeException("error"))

        viewModel.initialize()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(1, state.organizations.size)
        assertEquals(0, state.organizations[0].propertyCount)
    }

    @Test
    fun `initialize sorts active org first then remaining orgs alphabetically`() = runCoroutineTest {
        val activeOrgId = OrganizationId("org-active")
        val orgB = Organization(id = OrganizationId("org-b"), name = "Beta Corp", description = "")
        val orgZ = Organization(id = OrganizationId("org-z"), name = "Zeta Corp", description = "")
        val orgActive = Organization(id = activeOrgId, name = "Mid Corp", description = "")
        val orgA = Organization(id = OrganizationId("org-a"), name = "Alpha Corp", description = "")

        fun member(orgId: OrganizationId) = OrgMemberModel(
            userId = currentUserId,
            orgId = orgId,
            role = OrgRole.ADMIN,
            status = OrgMemberStatus.ACTIVE,
            joinedAt = null,
            displayName = "Alice",
            email = "alice@example.com",
        )

        coEvery { organizationManager.getOrganizations() } returns Result.success(listOf(orgB, orgZ, orgActive, orgA))
        coEvery { membershipManager.listMembers(orgB.id) } returns Result.success(listOf(member(orgB.id)))
        coEvery { membershipManager.listMembers(orgZ.id) } returns Result.success(listOf(member(orgZ.id)))
        coEvery { membershipManager.listMembers(activeOrgId) } returns Result.success(listOf(member(activeOrgId)))
        coEvery { membershipManager.listMembers(orgA.id) } returns Result.success(listOf(member(orgA.id)))
        coEvery {
            preferencesManager.getStringPreference(EdifikanaSettingKey.lastSelectedOrganization)
        } returns Result.success(activeOrgId.id)
        coEvery { propertyManager.getPropertyList() } returns Result.success(emptyList())

        viewModel.initialize()

        val state = viewModel.uiState.value
        assertEquals(
            listOf("Mid Corp", "Alpha Corp", "Beta Corp", "Zeta Corp"),
            state.organizations.map { it.name },
        )
    }

    @Test
    fun `initialize shows snackbar on org load failure`() = runCoroutineTest {
        coEvery { organizationManager.getOrganizations() } returns Result.failure(RuntimeException("error"))
        coEvery { preferencesManager.getStringPreference(EdifikanaSettingKey.lastSelectedOrganization) } returns Result.success(null)
        coEvery { propertyManager.getPropertyList() } returns Result.success(emptyList())

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.initialize()

            assertEquals(EdifikanaWindowsEvent.ShowSnackbar("Failed to load organizations"), turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `onOrgSelected navigates to detail screen`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.onOrgSelected(orgId)

            val event = turbine.awaitItem() as EdifikanaWindowsEvent.NavigateToScreen
            assertEquals(
                orgId,
                (event.destination as com.cramsan.edifikana.client.lib.features.settings.SettingsDestination.OrganizationDetailDestination).orgId,
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `requestSwitchOrg sets dialog to ConfirmSwitchOrg`() = runCoroutineTest {
        viewModel.requestSwitchOrg(orgId)

        val dialog = assertIs<MyOrganizationsDialogState.ConfirmSwitchOrg>(viewModel.uiState.value.dialog)
        assertEquals(orgId, dialog.orgId)
    }

    @Test
    fun `dismissDialog sets dialog to None`() = runCoroutineTest {
        viewModel.requestSwitchOrg(orgId)

        viewModel.dismissDialog()

        assertIs<MyOrganizationsDialogState.None>(viewModel.uiState.value.dialog)
    }

    @Test
    fun `navigateBack emits NavigateBack event`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateBack()

            assertEquals(EdifikanaWindowsEvent.NavigateBack, turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }
}
