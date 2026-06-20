package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.edifikana.lib.model.invite.InviteRole
import com.cramsan.edifikana.lib.model.organization.OrgRole
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.user.UserId
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
    private var ownerUserId: UserId? = null
    private var orgId: OrganizationId? = null

    @BeforeTest
    fun setUp() {
        testPrefix = UUID.random()
        ownerUserId = createTestUser("owner-${testPrefix}@example.com")
        orgId = createTestOrganization("test-org-$testPrefix", "")
        runBlocking {
            organizationDatastore.addUserToOrganization(ownerUserId!!, orgId!!, OrgRole.OWNER)
        }
    }

    // -------------------------------------------------------------------------
    // getMembers
    // -------------------------------------------------------------------------

    @Test
    fun `getMembers returns all active members for org`() = runBlocking {
        // Arrange
        val memberId = createTestUser("member-${testPrefix}@example.com")
        organizationDatastore.addUserToOrganization(memberId, orgId!!, OrgRole.EMPLOYEE)

        // Act
        val result = membershipDatastore.getMembers(orgId!!)

        // Assert
        assertTrue(result.isSuccess)
        val members = result.getOrThrow()
        assertTrue(members.any { it.userId == ownerUserId })
        assertTrue(members.any { it.userId == memberId })
    }

    @Test
    fun `getMembers excludes inactive members`() = runBlocking {
        // Arrange
        val memberId = createTestUser("inactive-${testPrefix}@example.com")
        organizationDatastore.addUserToOrganization(memberId, orgId!!, OrgRole.EMPLOYEE)
        membershipDatastore.removeMember(orgId!!, memberId)

        // Act
        val result = membershipDatastore.getMembers(orgId!!)

        // Assert
        assertTrue(result.isSuccess)
        val members = result.getOrThrow()
        assertTrue(members.none { it.userId == memberId })
    }

    // -------------------------------------------------------------------------
    // getMember
    // -------------------------------------------------------------------------

    @Test
    fun `getMember returns member when found`() = runBlocking {
        // Act
        val result = membershipDatastore.getMember(orgId!!, ownerUserId!!)

        // Assert
        assertTrue(result.isSuccess)
        val member = result.getOrThrow()
        assertNotNull(member)
        assertEquals(ownerUserId!!, member.userId)
        assertEquals(OrgRole.OWNER, member.role)
    }

    @Test
    fun `getMember returns null for non-member`() = runBlocking {
        // Arrange
        val stranger = createTestUser("stranger-${testPrefix}@example.com")

        // Act
        val result = membershipDatastore.getMember(orgId!!, stranger)

        // Assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow())
    }

    // -------------------------------------------------------------------------
    // updateMemberRole
    // -------------------------------------------------------------------------

    @Test
    fun `updateMemberRole updates role correctly`() = runBlocking {
        // Arrange
        val memberId = createTestUser("roleupdate-${testPrefix}@example.com")
        organizationDatastore.addUserToOrganization(memberId, orgId!!, OrgRole.EMPLOYEE)

        // Act
        val result = membershipDatastore.updateMemberRole(orgId!!, memberId, OrgRole.MANAGER)

        // Assert
        assertTrue(result.isSuccess)
        val roleResult = organizationDatastore.getUserRole(memberId, orgId!!)
        assertEquals(OrgRole.MANAGER, roleResult.getOrThrow())
    }

    // -------------------------------------------------------------------------
    // removeMember
    // -------------------------------------------------------------------------

    @Test
    fun `removeMember soft-deletes member by setting status to INACTIVE`() = runBlocking {
        // Arrange
        val memberId = createTestUser("remove-${testPrefix}@example.com")
        organizationDatastore.addUserToOrganization(memberId, orgId!!, OrgRole.EMPLOYEE)

        // Act
        val result = membershipDatastore.removeMember(orgId!!, memberId)

        // Assert
        assertTrue(result.isSuccess)
        val memberResult = membershipDatastore.getMember(orgId!!, memberId)
        assertTrue(memberResult.isSuccess)
        // v_org_members only shows ACTIVE members, so INACTIVE member should not appear
        assertNull(memberResult.getOrThrow())
    }

    // -------------------------------------------------------------------------
    // createInvite
    // -------------------------------------------------------------------------

    @Test
    fun `createInvite should succeed and return invite`() = runBlocking {
        // Arrange
        val email = "create-${testPrefix}@example.com"
        val futureExpiry = clock.now().plus(kotlin.time.Duration.parse("7d"))
        val inviteCode = UUID.random().replace("-", "").take(12).uppercase()

        // Act
        val result = membershipDatastore.createInvite(
            email = email,
            organizationId = orgId!!,
            expiration = futureExpiry,
            role = InviteRole.EMPLOYEE,
            inviteCode = inviteCode,
        )

        // Assert
        assertTrue(result.isSuccess)
        val invite = result.getOrThrow()
        assertEquals(email, invite.email)
        assertEquals(orgId!!, invite.organizationId)
        assertEquals(InviteRole.EMPLOYEE, invite.role)
        assertEquals(inviteCode, invite.inviteCode)

        // Register for cleanup
        membershipDatastore.cancelInvite(invite.id)
        membershipDatastore.purgeInvite(invite.id)
    }

    // -------------------------------------------------------------------------
    // getInviteById
    // -------------------------------------------------------------------------

    @Test
    fun `getInviteById should return invite by ID`() = runBlocking {
        // Arrange
        val futureExpiry = clock.now().plus(kotlin.time.Duration.parse("7d"))
        val inviteId = createTestInvite(
            email = "byid-${testPrefix}@example.com",
            organizationId = orgId!!,
            expiration = futureExpiry,
        )

        // Act
        val result = membershipDatastore.getInviteById(inviteId)

        // Assert
        assertTrue(result.isSuccess)
        val invite = result.getOrThrow()
        assertNotNull(invite)
        assertEquals(inviteId, invite.id)
    }

    @Test
    fun `getInviteById should return null for non-existent ID`() = runBlocking {
        // Act
        val result = membershipDatastore.getInviteById(
            InviteId("00000000-0000-0000-0000-000000000000")
        )

        // Assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow())
    }

    // -------------------------------------------------------------------------
    // listPendingInvites
    // -------------------------------------------------------------------------

    @Test
    fun `listPendingInvites returns non-expired non-cancelled non-accepted invites`() = runBlocking {
        // Arrange
        val futureExpiry = clock.now().plus(kotlin.time.Duration.parse("7d"))
        val inviteId = createTestInvite(
            email = "pending-${testPrefix}@example.com",
            organizationId = orgId!!,
            expiration = futureExpiry,
            role = InviteRole.EMPLOYEE,
        )

        // Act
        val result = membershipDatastore.listPendingInvites(orgId!!)

        // Assert
        assertTrue(result.isSuccess)
        val invites = result.getOrThrow()
        assertTrue(invites.any { it.id == inviteId })
    }

    @Test
    fun `listPendingInvites excludes cancelled invites`() = runBlocking {
        // Arrange
        val futureExpiry = clock.now().plus(kotlin.time.Duration.parse("7d"))
        val inviteId = createTestInvite(
            email = "cancelled-${testPrefix}@example.com",
            organizationId = orgId!!,
            expiration = futureExpiry,
        )
        membershipDatastore.cancelInvite(inviteId)

        // Act
        val result = membershipDatastore.listPendingInvites(orgId!!)

        // Assert
        assertTrue(result.isSuccess)
        val invites = result.getOrThrow()
        assertTrue(invites.none { it.id == inviteId })
    }

    @Test
    fun `listPendingInvites excludes expired invites`() = runBlocking {
        // Arrange — expiry in the past
        val pastExpiry = clock.now().minus(kotlin.time.Duration.parse("1d"))
        val inviteId = createTestInvite(
            email = "expired-${testPrefix}@example.com",
            organizationId = orgId!!,
            expiration = pastExpiry,
        )

        // Act
        val result = membershipDatastore.listPendingInvites(orgId!!)

        // Assert
        assertTrue(result.isSuccess)
        val invites = result.getOrThrow()
        assertTrue(invites.none { it.id == inviteId })
    }

    // -------------------------------------------------------------------------
    // cancelInvite
    // -------------------------------------------------------------------------

    @Test
    fun `cancelInvite marks invite as deleted`() = runBlocking {
        // Arrange
        val futureExpiry = clock.now().plus(kotlin.time.Duration.parse("7d"))
        val inviteId = createTestInvite(
            email = "cancel-${testPrefix}@example.com",
            organizationId = orgId!!,
            expiration = futureExpiry,
        )

        // Act
        val result = membershipDatastore.cancelInvite(inviteId)

        // Assert
        assertTrue(result.isSuccess)
        val pendingResult = membershipDatastore.listPendingInvites(orgId!!)
        assertTrue(pendingResult.isSuccess)
        assertTrue(pendingResult.getOrThrow().none { it.id == inviteId })
    }

    // -------------------------------------------------------------------------
    // resendInvite
    // -------------------------------------------------------------------------

    @Test
    fun `resendInvite generates new code and updates expiry`() = runBlocking {
        // Arrange
        val futureExpiry = clock.now().plus(kotlin.time.Duration.parse("7d"))
        val inviteId = createTestInvite(
            email = "resend-${testPrefix}@example.com",
            organizationId = orgId!!,
            expiration = futureExpiry,
        )
        val originalInvite = membershipDatastore.getInviteById(inviteId).getOrThrow()!!
        val newCode = UUID.random().replace("-", "").take(12).uppercase()
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
    fun `getInviteByCode finds invite by code`() = runBlocking {
        // Arrange
        val futureExpiry = clock.now().plus(kotlin.time.Duration.parse("7d"))
        val inviteId = createTestInvite(
            email = "bycode-${testPrefix}@example.com",
            organizationId = orgId!!,
            expiration = futureExpiry,
        )
        val invite = membershipDatastore.getInviteById(inviteId).getOrThrow()!!

        // Act
        val result = membershipDatastore.getInviteByCode(invite.inviteCode)

        // Assert
        assertTrue(result.isSuccess)
        val found = result.getOrThrow()
        assertNotNull(found)
        assertEquals(inviteId, found.id)
    }

    @Test
    fun `getInviteByCode returns null for invalid code`() = runBlocking {
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
    fun `transferOwnership atomically swaps roles`() = runBlocking {
        // Arrange
        val newOwnerId = createTestUser("newowner-${testPrefix}@example.com")
        organizationDatastore.addUserToOrganization(newOwnerId, orgId!!, OrgRole.ADMIN)

        // Act
        val result = membershipDatastore.transferOwnership(orgId!!, newOwnerId, ownerUserId!!)

        // Assert
        assertTrue(result.isSuccess)
        val callerRole = organizationDatastore.getUserRole(ownerUserId!!, orgId!!).getOrThrow()
        val newOwnerRole = organizationDatastore.getUserRole(newOwnerId, orgId!!).getOrThrow()
        assertEquals(OrgRole.ADMIN, callerRole)
        assertEquals(OrgRole.OWNER, newOwnerRole)
    }

    // -------------------------------------------------------------------------
    // purgeOrgMember
    // -------------------------------------------------------------------------

    @Test
    fun `purgeOrgMember hard-deletes membership row`() = runBlocking {
        // Arrange
        val memberId = createTestUser("purge-${testPrefix}@example.com")
        organizationDatastore.addUserToOrganization(memberId, orgId!!, OrgRole.EMPLOYEE)

        // Act
        val result = membershipDatastore.purgeOrgMember(orgId!!, memberId)

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow())
        val roleResult = organizationDatastore.getUserRole(memberId, orgId!!)
        assertTrue(roleResult.isSuccess)
        assertNull(roleResult.getOrThrow())
    }

    @Test
    fun `purgeOrgMember returns false for non-existent member`() = runBlocking {
        // Arrange
        val nonMember = createTestUser("nonmember-${testPrefix}@example.com")

        // Act
        val result = membershipDatastore.purgeOrgMember(orgId!!, nonMember)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrThrow())
    }

    // -------------------------------------------------------------------------
    // acceptInviteByCode
    // -------------------------------------------------------------------------

    @Test
    fun `acceptInviteByCode creates ACTIVE membership with role from invite`() = runBlocking {
        // Arrange
        val email = "acceptinvite-${testPrefix}@example.com"
        val userId = createTestUser(email)
        val inviteId = createTestInvite(
            email = email,
            organizationId = orgId!!,
            expiration = clock.now().plus(kotlin.time.Duration.parse("7d")),
            role = InviteRole.EMPLOYEE,
        )

        // Act
        val result = membershipDatastore.acceptInviteByCode(inviteId, userId)

        // Assert
        assertTrue(result.isSuccess)
        val member = membershipDatastore.getMember(orgId!!, userId).getOrThrow()
        assertNotNull(member)
        assertEquals(OrgRole.EMPLOYEE, member.role)
    }

    @Test
    fun `acceptInviteByCode reactivates previously-inactive member with new role from invite`() = runBlocking {
        // Arrange
        val email = "reactivate-${testPrefix}@example.com"
        val userId = createTestUser(email)
        organizationDatastore.addUserToOrganization(userId, orgId!!, OrgRole.EMPLOYEE)
        membershipDatastore.removeMember(orgId!!, userId)
        val inviteId = createTestInvite(
            email = email,
            organizationId = orgId!!,
            expiration = clock.now().plus(kotlin.time.Duration.parse("7d")),
            role = InviteRole.ADMIN,
        )

        // Act
        val result = membershipDatastore.acceptInviteByCode(inviteId, userId)

        // Assert
        assertTrue(result.isSuccess)
        val member = membershipDatastore.getMember(orgId!!, userId).getOrThrow()
        assertNotNull(member)
        assertEquals(OrgRole.ADMIN, member.role)
    }

    @Test
    fun `acceptInviteByCode second call on same invite fails because invite is already accepted`() = runBlocking {
        // Arrange
        val email = "doubleclaim-${testPrefix}@example.com"
        val userId = createTestUser(email)
        val inviteId = createTestInvite(
            email = email,
            organizationId = orgId!!,
            expiration = clock.now().plus(kotlin.time.Duration.parse("7d")),
            role = InviteRole.EMPLOYEE,
        )
        val firstResult = membershipDatastore.acceptInviteByCode(inviteId, userId)
        assertTrue(firstResult.isSuccess)

        // Act
        val secondResult = membershipDatastore.acceptInviteByCode(inviteId, userId)

        // Assert — acceptedAt is already set; filter returns 0 rows and decodeSingle() throws
        assertTrue(secondResult.isFailure)
        assertNotNull(membershipDatastore.getMember(orgId!!, userId).getOrThrow())
    }
}
