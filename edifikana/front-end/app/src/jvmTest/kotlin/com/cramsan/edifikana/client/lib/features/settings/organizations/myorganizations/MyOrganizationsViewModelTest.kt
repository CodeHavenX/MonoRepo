package com.cramsan.edifikana.client.lib.features.settings.organizations.myorganizations

import app.cash.turbine.turbineScope
import com.cramsan.architecture.client.manager.PreferencesManager
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

class MyOrganizationsViewModelTest : CoroutineTest() {

    private lateinit var viewModel: MyOrganizationsViewModel
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
        coEvery { organizationManager.getOrganizations() } returns Result.success(listOf(org))
        coEvery { membershipManager.listMembers(orgId) } returns Result.success(listOf(member))
        coEvery { preferencesManager.getStringPreference(EdifikanaSettingKey.lastSelectedOrganization) } returns Result.success(orgId.id)

        viewModel.initialize()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(1, state.organizations.size)
        assertEquals(orgId, state.organizations[0].orgId)
        assertEquals("Acme Corp", state.organizations[0].name)
        assertEquals("Owner", state.organizations[0].roleLabel)
        assertEquals(true, state.organizations[0].isActive)
    }

    @Test
    fun `initialize shows snackbar on org load failure`() = runCoroutineTest {
        coEvery { organizationManager.getOrganizations() } returns Result.failure(RuntimeException("error"))
        coEvery { preferencesManager.getStringPreference(EdifikanaSettingKey.lastSelectedOrganization) } returns Result.success(null)

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
    fun `navigateBack emits NavigateBack event`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateBack()

            assertEquals(EdifikanaWindowsEvent.NavigateBack, turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }
}
