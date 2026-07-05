package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.OrgMemberModel
import com.cramsan.edifikana.client.lib.service.MembershipService
import com.cramsan.edifikana.lib.model.organization.OrgMemberStatus
import com.cramsan.edifikana.lib.model.organization.OrgRole
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CoroutineTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MembershipManagerTest : CoroutineTest() {

    private lateinit var membershipService: MembershipService
    private lateinit var dependencies: ManagerDependencies
    private lateinit var manager: MembershipManager

    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        membershipService = mockk()
        dependencies = mockk(relaxed = true)
        every { dependencies.appScope } returns testCoroutineScope
        every { dependencies.dispatcherProvider } returns UnifiedDispatcherProvider(testCoroutineDispatcher)
        manager = MembershipManager(membershipService, dependencies)
    }

    @Test
    fun `listMembers returns member list on success`() = runCoroutineTest {
        val orgId = OrganizationId("org-1")
        val members = listOf(
            OrgMemberModel(
                userId = UserId("user-1"),
                orgId = orgId,
                role = OrgRole.ADMIN,
                status = OrgMemberStatus.ACTIVE,
                joinedAt = null,
                displayName = "Alice",
                email = "alice@example.com",
            ),
        )
        coEvery { membershipService.listMembers(orgId) } returns Result.success(members)

        val result = manager.listMembers(orgId)

        assertTrue(result.isSuccess)
        assertEquals(members, result.getOrNull())
        coVerify { membershipService.listMembers(orgId) }
    }

    @Test
    fun `listMembers returns failure when service fails`() = runCoroutineTest {
        val orgId = OrganizationId("org-1")
        coEvery { membershipService.listMembers(orgId) } returns Result.failure(RuntimeException("error"))

        val result = manager.listMembers(orgId)

        assertTrue(result.isFailure)
        coVerify { membershipService.listMembers(orgId) }
    }

    @Test
    fun `leaveOrganization returns success`() = runCoroutineTest {
        val orgId = OrganizationId("org-1")
        coEvery { membershipService.leaveOrganization(orgId) } returns Result.success(Unit)

        val result = manager.leaveOrganization(orgId)

        assertTrue(result.isSuccess)
        coVerify { membershipService.leaveOrganization(orgId) }
    }

    @Test
    fun `leaveOrganization returns failure when service fails`() = runCoroutineTest {
        val orgId = OrganizationId("org-1")
        coEvery { membershipService.leaveOrganization(orgId) } returns Result.failure(RuntimeException("error"))

        val result = manager.leaveOrganization(orgId)

        assertTrue(result.isFailure)
        coVerify { membershipService.leaveOrganization(orgId) }
    }

    @Test
    fun `transferOwnership returns success`() = runCoroutineTest {
        val orgId = OrganizationId("org-1")
        val newOwnerId = UserId("user-2")
        coEvery { membershipService.transferOwnership(orgId, newOwnerId) } returns Result.success(Unit)

        val result = manager.transferOwnership(orgId, newOwnerId)

        assertTrue(result.isSuccess)
        coVerify { membershipService.transferOwnership(orgId, newOwnerId) }
    }

    @Test
    fun `transferOwnership returns failure when service fails`() = runCoroutineTest {
        val orgId = OrganizationId("org-1")
        val newOwnerId = UserId("user-2")
        coEvery { membershipService.transferOwnership(orgId, newOwnerId) } returns Result.failure(RuntimeException("error"))

        val result = manager.transferOwnership(orgId, newOwnerId)

        assertTrue(result.isFailure)
        coVerify { membershipService.transferOwnership(orgId, newOwnerId) }
    }
}
