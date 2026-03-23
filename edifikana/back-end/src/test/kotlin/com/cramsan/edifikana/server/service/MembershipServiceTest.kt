@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.InviteRole
import com.cramsan.edifikana.lib.model.OrgMemberStatus
import com.cramsan.edifikana.lib.model.OrgRole
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.MembershipDatastore
import com.cramsan.edifikana.server.datastore.UserDatastore
import com.cramsan.edifikana.server.service.models.Invite
import com.cramsan.edifikana.server.service.models.OrgMemberView
import com.cramsan.edifikana.server.service.models.User
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.asClock
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.TestTimeSource

/**
 * Test class for [MembershipService].
 */
@OptIn(ExperimentalTime::class)
class MembershipServiceTest {

    private lateinit var membershipDatastore: MembershipDatastore
    private lateinit var userDatastore: UserDatastore
    private lateinit var membershipService: MembershipService
    private lateinit var testTimeSource: TestTimeSource
    private lateinit var clock: Clock

    /**
     * Sets up the test environment by initializing mocks for [MembershipDatastore],
     * [UserDatastore], and [MembershipService].
     */
    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        membershipDatastore = mockk()
        userDatastore = mockk()
        testTimeSource = TestTimeSource()
        clock = testTimeSource.asClock(2024, 1, 1, 0, 0)
        membershipService = MembershipService(membershipDatastore, userDatastore, clock)
    }

    /**
     * Cleans up the test environment by stopping Koin.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    // -------------------------------------------------------------------------
    // inviteMember
    // -------------------------------------------------------------------------

    /**
     * Tests that inviteMember creates an invite with a 14-day expiry.
     */
    @Test
    fun `inviteMember should create invite with 14-day expiry`() = runTest {
        // Arrange
        val orgId = OrganizationId("org123")
        val email = "invite@example.com"
        val role = InviteRole.EMPLOYEE
        val expectedExpiry = clock.now() + 14.days
        val invite = mockk<Invite>()
        coEvery {
            membershipDatastore.createInvite(email, orgId, expectedExpiry, role)
        } returns Result.success(invite)

        // Act
        val result = membershipService.inviteMember(orgId, email, role)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { membershipDatastore.createInvite(email, orgId, expectedExpiry, role) }
    }

    // -------------------------------------------------------------------------
    // listMembers
    // -------------------------------------------------------------------------

    /**
     * Tests that listMembers returns the list of members from the datastore.
     */
    @Test
    fun `listMembers should return members from datastore`() = runTest {
        // Arrange
        val orgId = OrganizationId("org123")
        val members = listOf(
            orgMemberView(UserId("user123"), orgId, OrgRole.EMPLOYEE)
        )
        coEvery { membershipDatastore.getMembers(orgId) } returns Result.success(members)

        // Act
        val result = membershipService.listMembers(orgId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(members, result.getOrThrow())
    }

    // -------------------------------------------------------------------------
    // updateMemberRole
    // -------------------------------------------------------------------------

    /**
     * Tests that updateMemberRole delegates to the datastore.
     */
    @Test
    fun `updateMemberRole should delegate to datastore`() = runTest {
        // Arrange
        val orgId = OrganizationId("org123")
        val targetUserId = UserId("user123")
        val newRole = OrgRole.ADMIN
        coEvery { membershipDatastore.updateMemberRole(orgId, targetUserId, newRole) } returns Result.success(Unit)

        // Act
        val result = membershipService.updateMemberRole(orgId, targetUserId, newRole)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { membershipDatastore.updateMemberRole(orgId, targetUserId, newRole) }
    }

    // -------------------------------------------------------------------------
    // removeMember
    // -------------------------------------------------------------------------

    /**
     * Tests that removeMember succeeds when the target is not the sole owner.
     */
    @Test
    fun `removeMember should succeed when target is not the sole owner`() = runTest {
        // Arrange
        val orgId = OrganizationId("org123")
        val targetUserId = UserId("user123")
        val ownerMember = orgMemberView(UserId("owner456"), orgId, OrgRole.OWNER)
        val targetMember = orgMemberView(targetUserId, orgId, OrgRole.EMPLOYEE)
        coEvery { membershipDatastore.getMembers(orgId) } returns Result.success(listOf(ownerMember, targetMember))
        coEvery { membershipDatastore.unassignTasksForMember(orgId, targetUserId) } returns Result.success(Unit)
        coEvery { membershipDatastore.removeMember(orgId, targetUserId) } returns Result.success(Unit)

        // Act
        val result = membershipService.removeMember(orgId, targetUserId)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { membershipDatastore.unassignTasksForMember(orgId, targetUserId) }
        coVerify { membershipDatastore.removeMember(orgId, targetUserId) }
    }

    /**
     * Tests that removeMember fails when the target is the sole owner.
     */
    @Test
    fun `removeMember should fail when target is the sole owner`() = runTest {
        // Arrange
        val orgId = OrganizationId("org123")
        val targetUserId = UserId("owner123")
        val ownerMember = orgMemberView(targetUserId, orgId, OrgRole.OWNER)
        coEvery { membershipDatastore.getMembers(orgId) } returns Result.success(listOf(ownerMember))

        // Act
        val result = membershipService.removeMember(orgId, targetUserId)

        // Assert
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { membershipDatastore.removeMember(any(), any()) }
    }

    // -------------------------------------------------------------------------
    // leaveOrganization
    // -------------------------------------------------------------------------

    /**
     * Tests that leaveOrganization succeeds when the caller is not the sole owner.
     */
    @Test
    fun `leaveOrganization should succeed when caller is not the sole owner`() = runTest {
        // Arrange
        val callerId = UserId("user123")
        val orgId = OrganizationId("org123")
        val ownerMember = orgMemberView(UserId("owner456"), orgId, OrgRole.OWNER)
        coEvery { membershipDatastore.getMembers(orgId) } returns Result.success(listOf(ownerMember))
        coEvery { membershipDatastore.unassignTasksForMember(orgId, callerId) } returns Result.success(Unit)
        coEvery { membershipDatastore.removeMember(orgId, callerId) } returns Result.success(Unit)

        // Act
        val result = membershipService.leaveOrganization(callerId, orgId)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { membershipDatastore.removeMember(orgId, callerId) }
    }

    /**
     * Tests that leaveOrganization fails when the caller is the sole owner.
     */
    @Test
    fun `leaveOrganization should fail when caller is the sole owner`() = runTest {
        // Arrange
        val callerId = UserId("owner123")
        val orgId = OrganizationId("org123")
        val ownerMember = orgMemberView(callerId, orgId, OrgRole.OWNER)
        coEvery { membershipDatastore.getMembers(orgId) } returns Result.success(listOf(ownerMember))

        // Act
        val result = membershipService.leaveOrganization(callerId, orgId)

        // Assert
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { membershipDatastore.removeMember(any(), any()) }
    }

    // -------------------------------------------------------------------------
    // transferOwnership
    // -------------------------------------------------------------------------

    /**
     * Tests that transferOwnership succeeds when the new owner is a different active member.
     */
    @Test
    fun `transferOwnership should succeed when target is a different active member`() = runTest {
        // Arrange
        val callerId = UserId("owner123")
        val newOwnerId = UserId("user456")
        val orgId = OrganizationId("org123")
        val targetMember = orgMemberView(newOwnerId, orgId, OrgRole.EMPLOYEE)
        coEvery { membershipDatastore.getMember(orgId, newOwnerId) } returns Result.success(targetMember)
        coEvery { membershipDatastore.transferOwnership(orgId, newOwnerId, callerId) } returns Result.success(Unit)

        // Act
        val result = membershipService.transferOwnership(callerId, orgId, newOwnerId)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { membershipDatastore.transferOwnership(orgId, newOwnerId, callerId) }
    }

    /**
     * Tests that transferOwnership fails when the caller and new owner are the same user.
     */
    @Test
    fun `transferOwnership should fail when caller and new owner are the same`() = runTest {
        // Arrange
        val callerId = UserId("owner123")
        val orgId = OrganizationId("org123")

        // Act
        val result = membershipService.transferOwnership(callerId, orgId, callerId)

        // Assert
        assertTrue(result.isFailure)
    }

    /**
     * Tests that transferOwnership fails when the target user is not an active member.
     */
    @Test
    fun `transferOwnership should fail when target is not an active member`() = runTest {
        // Arrange
        val callerId = UserId("owner123")
        val newOwnerId = UserId("nonmember456")
        val orgId = OrganizationId("org123")
        coEvery { membershipDatastore.getMember(orgId, newOwnerId) } returns Result.success(null)

        // Act
        val result = membershipService.transferOwnership(callerId, orgId, newOwnerId)

        // Assert
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { membershipDatastore.transferOwnership(any(), any(), any()) }
    }

    // -------------------------------------------------------------------------
    // listPendingInvites
    // -------------------------------------------------------------------------

    /**
     * Tests that listPendingInvites delegates to the datastore.
     */
    @Test
    fun `listPendingInvites should delegate to datastore`() = runTest {
        // Arrange
        val orgId = OrganizationId("org123")
        val invites = listOf(
            Invite(
                id = InviteId("invite123"),
                email = "invite@example.com",
                organizationId = orgId,
                role = InviteRole.EMPLOYEE,
                expiration = clock.now() + 14.days,
                inviteCode = "code123",
            )
        )
        coEvery { membershipDatastore.listPendingInvites(orgId) } returns Result.success(invites)

        // Act
        val result = membershipService.listPendingInvites(orgId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(invites, result.getOrThrow())
    }

    // -------------------------------------------------------------------------
    // getInviteOrganization
    // -------------------------------------------------------------------------

    /**
     * Tests that getInviteOrganization returns the organization ID from a found invite.
     */
    @Test
    fun `getInviteOrganization should return orgId from invite`() = runTest {
        // Arrange
        val orgId = OrganizationId("org123")
        val inviteId = InviteId("invite123")
        val invite = Invite(
            id = inviteId,
            email = "invite@example.com",
            organizationId = orgId,
            role = InviteRole.EMPLOYEE,
            expiration = clock.now() + 14.days,
            inviteCode = "code123",
        )
        coEvery { membershipDatastore.getInviteById(inviteId) } returns Result.success(invite)

        // Act
        val result = membershipService.getInviteOrganization(inviteId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(orgId, result.getOrThrow())
    }

    /**
     * Tests that getInviteOrganization fails when the invite is not found.
     */
    @Test
    fun `getInviteOrganization should fail when invite not found`() = runTest {
        // Arrange
        val inviteId = InviteId("invite123")
        coEvery { membershipDatastore.getInviteById(inviteId) } returns Result.success(null)

        // Act
        val result = membershipService.getInviteOrganization(inviteId)

        // Assert
        assertTrue(result.isFailure)
    }

    // -------------------------------------------------------------------------
    // cancelInvite
    // -------------------------------------------------------------------------

    /**
     * Tests that cancelInvite delegates to the datastore.
     */
    @Test
    fun `cancelInvite should delegate to datastore`() = runTest {
        // Arrange
        val inviteId = InviteId("invite123")
        coEvery { membershipDatastore.cancelInvite(inviteId) } returns Result.success(Unit)

        // Act
        val result = membershipService.cancelInvite(inviteId)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { membershipDatastore.cancelInvite(inviteId) }
    }

    // -------------------------------------------------------------------------
    // resendInvite
    // -------------------------------------------------------------------------

    /**
     * Tests that resendInvite calls the datastore with a new code and a 7-day expiry.
     */
    @Test
    fun `resendInvite should call datastore with new code and 7-day expiry`() = runTest {
        // Arrange
        val inviteId = InviteId("invite123")
        val expectedExpiry = clock.now() + 7.days
        val updatedInvite = mockk<Invite>()
        coEvery {
            membershipDatastore.resendInvite(inviteId, any(), expectedExpiry)
        } returns Result.success(updatedInvite)

        // Act
        val result = membershipService.resendInvite(inviteId)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { membershipDatastore.resendInvite(inviteId, any(), expectedExpiry) }
    }

    // -------------------------------------------------------------------------
    // joinViaCode
    // -------------------------------------------------------------------------

    /**
     * Tests that joinViaCode succeeds when the invite code is valid and the caller's
     * email matches the invite.
     */
    @Test
    fun `joinViaCode should succeed when invite matches caller email`() = runTest {
        // Arrange
        val callerId = UserId("user123")
        val inviteCode = "ABC123"
        val email = "user@example.com"
        val orgId = OrganizationId("org123")
        val inviteId = InviteId("invite123")
        val invite = Invite(
            id = inviteId,
            email = email,
            organizationId = orgId,
            role = InviteRole.EMPLOYEE,
            expiration = clock.now() + 14.days,
            inviteCode = inviteCode,
        )
        val user = mockk<User>()
        every { user.email } returns email
        coEvery { membershipDatastore.getInviteByCode(inviteCode) } returns Result.success(invite)
        coEvery { userDatastore.getUser(callerId) } returns Result.success(user)
        coEvery { membershipDatastore.acceptInviteByCode(inviteId, callerId) } returns Result.success(Unit)

        // Act
        val result = membershipService.joinViaCode(callerId, inviteCode)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { membershipDatastore.acceptInviteByCode(inviteId, callerId) }
    }

    /**
     * Tests that joinViaCode fails when the invite code does not exist.
     */
    @Test
    fun `joinViaCode should fail when invite not found`() = runTest {
        // Arrange
        val callerId = UserId("user123")
        val inviteCode = "INVALID"
        coEvery { membershipDatastore.getInviteByCode(inviteCode) } returns Result.success(null)

        // Act
        val result = membershipService.joinViaCode(callerId, inviteCode)

        // Assert
        assertTrue(result.isFailure)
    }

    /**
     * Tests that joinViaCode fails when the caller user record is not found.
     */
    @Test
    fun `joinViaCode should fail when user not found`() = runTest {
        // Arrange
        val callerId = UserId("user123")
        val inviteCode = "ABC123"
        val invite = Invite(
            id = InviteId("invite123"),
            email = "user@example.com",
            organizationId = OrganizationId("org123"),
            role = InviteRole.EMPLOYEE,
            expiration = clock.now() + 14.days,
            inviteCode = inviteCode,
        )
        coEvery { membershipDatastore.getInviteByCode(inviteCode) } returns Result.success(invite)
        coEvery { userDatastore.getUser(callerId) } returns Result.success(null)

        // Act
        val result = membershipService.joinViaCode(callerId, inviteCode)

        // Assert
        assertTrue(result.isFailure)
    }

    /**
     * Tests that joinViaCode fails when the caller's email does not match the invite's email.
     */
    @Test
    fun `joinViaCode should fail when caller email does not match invite email`() = runTest {
        // Arrange
        val callerId = UserId("user123")
        val inviteCode = "ABC123"
        val invite = Invite(
            id = InviteId("invite123"),
            email = "other@example.com",
            organizationId = OrganizationId("org123"),
            role = InviteRole.EMPLOYEE,
            expiration = clock.now() + 14.days,
            inviteCode = inviteCode,
        )
        val user = mockk<User>()
        every { user.email } returns "user@example.com"
        coEvery { membershipDatastore.getInviteByCode(inviteCode) } returns Result.success(invite)
        coEvery { userDatastore.getUser(callerId) } returns Result.success(user)

        // Act
        val result = membershipService.joinViaCode(callerId, inviteCode)

        // Assert
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { membershipDatastore.acceptInviteByCode(any(), any()) }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private fun orgMemberView(userId: UserId, orgId: OrganizationId, role: OrgRole) = OrgMemberView(
        userId = userId,
        orgId = orgId,
        role = role,
        status = OrgMemberStatus.ACTIVE,
        joinedAt = null,
        email = "${userId.userId}@example.com",
        firstName = "Test",
        lastName = "User",
    )
}
