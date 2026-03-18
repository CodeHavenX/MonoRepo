package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.InviteRole
import com.cramsan.edifikana.lib.model.OrgRole
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.utils.uuid.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.runBlocking
import org.koin.test.inject

@OptIn(ExperimentalTime::class)
class SupabaseMembershipDatastoreIntegrationTest : SupabaseIntegrationTest() {

    private val clock: Clock by inject()

    private lateinit var testPrefix: String
    private lateinit var ownerUserId: UserId
    private lateinit var orgId: OrganizationId

    @BeforeTest
    fun setUp() {
        testPrefix = UUID.random()
        ownerUserId = createTestUser("owner-${testPrefix}@test.com")
        orgId = createTestOrganization("test-org-$testPrefix", "")
        runBlocking {
            organizationDatastore.addUserToOrganization(ownerUserId, orgId, OrgRole.OWNER)
        }
    }

    // -------------------------------------------------------------------------
    // getMembers
    // -------------------------------------------------------------------------

    @Test
    fun `getMembers returns all active members for org`() = runCoroutineTest {
        // Arrange
        val memberId = createTestUser("member-${testPrefix}@test.com")
        organizationDatastore.addUserToOrganization(memberId, orgId, OrgRole.EMPLOYEE)

        // Act
        val result = membershipDatastore.getMembers(orgId)

        // Assert
        assertTrue(result.isSuccess)
        val members = result.getOrThrow()
        assertTrue(members.any { it.userId == ownerUserId })
        assertTrue(members.any { it.userId == memberId })
    }

    @Test
    fun `getMembers excludes inactive members`() = runCoroutineTest {
        // Arrange
        val memberId = createTestUser("inactive-${testPrefix}@test.com")
        organizationDatastore.addUserToOrganization(memberId, orgId, OrgRole.EMPLOYEE)
        membershipDatastore.removeMember(orgId, memberId)

        // Act
        val result = membershipDatastore.getMembers(orgId)

        // Assert
        assertTrue(result.isSuccess)
        val members = result.getOrThrow()
        assertTrue(members.none { it.userId == memberId })
    }

    // -------------------------------------------------------------------------
    // getMember
    // -------------------------------------------------------------------------

    @Test
    fun `getMember returns member when found`() = runCoroutineTest {
        // Act
        val result = membershipDatastore.getMember(orgId, ownerUserId)

        // Assert
        assertTrue(result.isSuccess)
        val member = result.getOrThrow()
        assertNotNull(member)
        assertEquals(ownerUserId, member.userId)
        assertEquals(OrgRole.OWNER, member.role)
    }

    @Test
    fun `getMember returns null for non-member`() = runCoroutineTest {
        // Arrange
        val stranger = createTestUser("stranger-${testPrefix}@test.com")

        // Act
        val result = membershipDatastore.getMember(orgId, stranger)

        // Assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow())
    }

    // -------------------------------------------------------------------------
    // updateMemberRole
    // -------------------------------------------------------------------------

    @Test
    fun `updateMemberRole updates role correctly`() = runCoroutineTest {
        // Arrange
        val memberId = createTestUser("roleupdate-${testPrefix}@test.com")
        organizationDatastore.addUserToOrganization(memberId, orgId, OrgRole.EMPLOYEE)

        // Act
        val result = membershipDatastore.updateMemberRole(orgId, memberId, OrgRole.MANAGER)

        // Assert
        assertTrue(result.isSuccess)
        val roleResult = organizationDatastore.getUserRole(memberId, orgId)
        assertEquals(OrgRole.MANAGER, roleResult.getOrThrow())
    }

    // -------------------------------------------------------------------------
    // removeMember
    // -------------------------------------------------------------------------

    @Test
    fun `removeMember soft-deletes member by setting status to INACTIVE`() = runCoroutineTest {
        // Arrange
        val memberId = createTestUser("remove-${testPrefix}@test.com")
        organizationDatastore.addUserToOrganization(memberId, orgId, OrgRole.EMPLOYEE)

        // Act
        val result = membershipDatastore.removeMember(orgId, memberId)

        // Assert
        assertTrue(result.isSuccess)
        val memberResult = membershipDatastore.getMember(orgId, memberId)
        assertTrue(memberResult.isSuccess)
        // v_org_members only shows ACTIVE members, so INACTIVE member should not appear
        assertNull(memberResult.getOrThrow())
    }

    // -------------------------------------------------------------------------
    // listPendingInvites
    // -------------------------------------------------------------------------

    @Test
    fun `listPendingInvites returns non-expired non-cancelled non-accepted invites`() = runCoroutineTest {
        // Arrange
        val futureExpiry = clock.now().plus(kotlin.time.Duration.parse("7d"))
        val inviteId = createTestInvite(
            email = "pending-${testPrefix}@test.com",
            organizationId = orgId,
            expiration = futureExpiry,
            role = InviteRole.EMPLOYEE,
        )

        // Act
        val result = membershipDatastore.listPendingInvites(orgId)

        // Assert
        assertTrue(result.isSuccess)
        val invites = result.getOrThrow()
        assertTrue(invites.any { it.id == inviteId })
    }

    @Test
    fun `listPendingInvites excludes cancelled invites`() = runCoroutineTest {
        // Arrange
        val futureExpiry = clock.now().plus(kotlin.time.Duration.parse("7d"))
        val inviteId = createTestInvite(
            email = "cancelled-${testPrefix}@test.com",
            organizationId = orgId,
            expiration = futureExpiry,
        )
        membershipDatastore.cancelInvite(inviteId)

        // Act
        val result = membershipDatastore.listPendingInvites(orgId)

        // Assert
        assertTrue(result.isSuccess)
        val invites = result.getOrThrow()
        assertTrue(invites.none { it.id == inviteId })
    }

    @Test
    fun `listPendingInvites excludes expired invites`() = runCoroutineTest {
        // Arrange — expiry in the past
        val pastExpiry = clock.now().minus(kotlin.time.Duration.parse("1d"))
        val inviteId = createTestInvite(
            email = "expired-${testPrefix}@test.com",
            organizationId = orgId,
            expiration = pastExpiry,
        )

        // Act
        val result = membershipDatastore.listPendingInvites(orgId)

        // Assert
        assertTrue(result.isSuccess)
        val invites = result.getOrThrow()
        assertTrue(invites.none { it.id == inviteId })
    }

    // -------------------------------------------------------------------------
    // cancelInvite
    // -------------------------------------------------------------------------

    @Test
    fun `cancelInvite marks invite as deleted`() = runCoroutineTest {
        // Arrange
        val futureExpiry = clock.now().plus(kotlin.time.Duration.parse("7d"))
        val inviteId = createTestInvite(
            email = "cancel-${testPrefix}@test.com",
            organizationId = orgId,
            expiration = futureExpiry,
        )

        // Act
        val result = membershipDatastore.cancelInvite(inviteId)

        // Assert
        assertTrue(result.isSuccess)
        val pendingResult = membershipDatastore.listPendingInvites(orgId)
        assertTrue(pendingResult.isSuccess)
        assertTrue(pendingResult.getOrThrow().none { it.id == inviteId })
    }

    // -------------------------------------------------------------------------
    // resendInvite
    // -------------------------------------------------------------------------

    @Test
    fun `resendInvite generates new code and updates expiry`() = runCoroutineTest {
        // Arrange
        val futureExpiry = clock.now().plus(kotlin.time.Duration.parse("7d"))
        val inviteId = createTestInvite(
            email = "resend-${testPrefix}@test.com",
            organizationId = orgId,
            expiration = futureExpiry,
        )
        val originalInvite = userDatastore.getInvite(inviteId).getOrThrow()!!
        val newCode = UUID.random()
        val newExpiry = clock.now().plus(kotlin.time.Duration.parse("14d"))

        // Act
        val result = membershipDatastore.resendInvite(inviteId, newCode, newExpiry)

        // Assert
        assertTrue(result.isSuccess)
        val updated = result.getOrThrow()
        assertEquals(inviteId, updated.id)
        assertEquals(newCode, updated.inviteCode)
        assertTrue(updated.inviteCode != originalInvite.inviteCode)
    }

    // -------------------------------------------------------------------------
    // getInviteByCode
    // -------------------------------------------------------------------------

    @Test
    fun `getInviteByCode finds invite by code`() = runCoroutineTest {
        // Arrange
        val futureExpiry = clock.now().plus(kotlin.time.Duration.parse("7d"))
        val inviteId = createTestInvite(
            email = "bycode-${testPrefix}@test.com",
            organizationId = orgId,
            expiration = futureExpiry,
        )
        val invite = userDatastore.getInvite(inviteId).getOrThrow()!!

        // Act
        val result = membershipDatastore.getInviteByCode(invite.inviteCode)

        // Assert
        assertTrue(result.isSuccess)
        val found = result.getOrThrow()
        assertNotNull(found)
        assertEquals(inviteId, found.id)
    }

    @Test
    fun `getInviteByCode returns null for invalid code`() = runCoroutineTest {
        // Act
        val result = membershipDatastore.getInviteByCode("invalid-code-${testPrefix}")

        // Assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow())
    }

    // -------------------------------------------------------------------------
    // transferOwnership
    // -------------------------------------------------------------------------

    @Test
    fun `transferOwnership atomically swaps roles`() = runCoroutineTest {
        // Arrange
        val newOwnerId = createTestUser("newowner-${testPrefix}@test.com")
        organizationDatastore.addUserToOrganization(newOwnerId, orgId, OrgRole.ADMIN)

        // Act
        val result = membershipDatastore.transferOwnership(orgId, newOwnerId, ownerUserId)

        // Assert
        assertTrue(result.isSuccess)
        val callerRole = organizationDatastore.getUserRole(ownerUserId, orgId).getOrThrow()
        val newOwnerRole = organizationDatastore.getUserRole(newOwnerId, orgId).getOrThrow()
        assertEquals(OrgRole.ADMIN, callerRole)
        assertEquals(OrgRole.OWNER, newOwnerRole)
    }

    // -------------------------------------------------------------------------
    // purgeOrgMember
    // -------------------------------------------------------------------------

    @Test
    fun `purgeOrgMember hard-deletes membership row`() = runCoroutineTest {
        // Arrange
        val memberId = createTestUser("purge-${testPrefix}@test.com")
        organizationDatastore.addUserToOrganization(memberId, orgId, OrgRole.EMPLOYEE)

        // Act
        val result = membershipDatastore.purgeOrgMember(orgId, memberId)

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow())
        val roleResult = organizationDatastore.getUserRole(memberId, orgId)
        assertTrue(roleResult.isSuccess)
        assertNull(roleResult.getOrThrow())
    }

    @Test
    fun `purgeOrgMember returns false for non-existent member`() = runCoroutineTest {
        // Arrange
        val nonMember = createTestUser("nonmember-${testPrefix}@test.com")

        // Act
        val result = membershipDatastore.purgeOrgMember(orgId, nonMember)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrThrow())
    }
}
