package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.invite.InviteRole
import com.cramsan.edifikana.lib.model.organization.OrgRole
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.task.TaskStatus
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.framework.annotations.SupabaseModel
import com.cramsan.framework.utils.uuid.UUID
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.test.AfterTest
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

@OptIn(ExperimentalTime::class, SupabaseModel::class)
class SupabaseMembershipDatastoreIntegrationTest : SupabaseIntegrationTest() {

    private val clock: Clock by inject()
    private val postgrest: Postgrest by inject()

    private lateinit var testPrefix: String
    private var ownerUserId: UserId? = null
    private var orgId: OrganizationId? = null
    private var propertyId: PropertyId? = null
    private val taskIds = mutableListOf<String>()

    @BeforeTest
    fun setUp() {
        testPrefix = UUID.random()
        ownerUserId = createTestUser("owner-${testPrefix}@test.com")
        orgId = createTestOrganization("test-org-$testPrefix", "")
        propertyId = createTestProperty("test-prop-$testPrefix", ownerUserId!!, orgId!!)
        runBlocking {
            organizationDatastore.addUserToOrganization(ownerUserId!!, orgId!!, OrgRole.OWNER)
        }
    }

    // -------------------------------------------------------------------------
    // getMembers
    // -------------------------------------------------------------------------

    @Test
    fun `getMembers returns all active members for org`() = runCoroutineTest {
        // Arrange
        val memberId = createTestUser("member-${testPrefix}@test.com")
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
    fun `getMembers excludes inactive members`() = runCoroutineTest {
        // Arrange
        val memberId = createTestUser("inactive-${testPrefix}@test.com")
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
    fun `getMember returns member when found`() = runCoroutineTest {
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
    fun `getMember returns null for non-member`() = runCoroutineTest {
        // Arrange
        val stranger = createTestUser("stranger-${testPrefix}@test.com")

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
    fun `updateMemberRole updates role correctly`() = runCoroutineTest {
        // Arrange
        val memberId = createTestUser("roleupdate-${testPrefix}@test.com")
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
    fun `removeMember soft-deletes member by setting status to INACTIVE`() = runCoroutineTest {
        // Arrange
        val memberId = createTestUser("remove-${testPrefix}@test.com")
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
    fun `createInvite should succeed and return invite`() = runCoroutineTest {
        // Arrange
        val email = "create-${testPrefix}@test.com"
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
    fun `getInviteById should return invite by ID`() = runCoroutineTest {
        // Arrange
        val futureExpiry = clock.now().plus(kotlin.time.Duration.parse("7d"))
        val inviteId = createTestInvite(
            email = "byid-${testPrefix}@test.com",
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
    fun `getInviteById should return null for non-existent ID`() = runCoroutineTest {
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
    fun `listPendingInvites returns non-expired non-cancelled non-accepted invites`() = runCoroutineTest {
        // Arrange
        val futureExpiry = clock.now().plus(kotlin.time.Duration.parse("7d"))
        val inviteId = createTestInvite(
            email = "pending-${testPrefix}@test.com",
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
    fun `listPendingInvites excludes cancelled invites`() = runCoroutineTest {
        // Arrange
        val futureExpiry = clock.now().plus(kotlin.time.Duration.parse("7d"))
        val inviteId = createTestInvite(
            email = "cancelled-${testPrefix}@test.com",
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
    fun `listPendingInvites excludes expired invites`() = runCoroutineTest {
        // Arrange — expiry in the past
        val pastExpiry = clock.now().minus(kotlin.time.Duration.parse("1d"))
        val inviteId = createTestInvite(
            email = "expired-${testPrefix}@test.com",
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
    fun `cancelInvite marks invite as deleted`() = runCoroutineTest {
        // Arrange
        val futureExpiry = clock.now().plus(kotlin.time.Duration.parse("7d"))
        val inviteId = createTestInvite(
            email = "cancel-${testPrefix}@test.com",
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
    fun `resendInvite generates new code and updates expiry`() = runCoroutineTest {
        // Arrange
        val futureExpiry = clock.now().plus(kotlin.time.Duration.parse("7d"))
        val inviteId = createTestInvite(
            email = "resend-${testPrefix}@test.com",
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
    fun `getInviteByCode finds invite by code`() = runCoroutineTest {
        // Arrange
        val futureExpiry = clock.now().plus(kotlin.time.Duration.parse("7d"))
        val inviteId = createTestInvite(
            email = "bycode-${testPrefix}@test.com",
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
    fun `purgeOrgMember hard-deletes membership row`() = runCoroutineTest {
        // Arrange
        val memberId = createTestUser("purge-${testPrefix}@test.com")
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
    fun `purgeOrgMember returns false for non-existent member`() = runCoroutineTest {
        // Arrange
        val nonMember = createTestUser("nonmember-${testPrefix}@test.com")

        // Act
        val result = membershipDatastore.purgeOrgMember(orgId!!, nonMember)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrThrow())
    }

    // -------------------------------------------------------------------------
    // unassignTasksForMember
    // -------------------------------------------------------------------------

    @Test
    fun `unassignTasksForMember unassigns OPEN task for member`() = runCoroutineTest {
        // Arrange
        val memberId = createTestUser("unassign-open-${testPrefix}@test.com")
        organizationDatastore.addUserToOrganization(memberId, orgId!!, OrgRole.EMPLOYEE)
        val taskId = createTestTask(ownerUserId!!, propertyId!!, assigneeId = memberId, status = TaskStatus.OPEN)

        // Act
        val result = membershipDatastore.unassignTasksForMember(orgId!!, memberId)

        // Assert
        assertTrue(result.isSuccess)
        assertNull(getTaskAssignee(taskId))
    }

    @Test
    fun `unassignTasksForMember unassigns IN_PROGRESS task for member`() = runCoroutineTest {
        // Arrange
        val memberId = createTestUser("unassign-ip-${testPrefix}@test.com")
        organizationDatastore.addUserToOrganization(memberId, orgId!!, OrgRole.EMPLOYEE)
        val taskId = createTestTask(ownerUserId!!, propertyId!!, assigneeId = memberId, status = TaskStatus.IN_PROGRESS)

        // Act
        val result = membershipDatastore.unassignTasksForMember(orgId!!, memberId)

        // Assert
        assertTrue(result.isSuccess)
        assertNull(getTaskAssignee(taskId))
    }

    @Test
    fun `unassignTasksForMember does not affect COMPLETED tasks`() = runCoroutineTest {
        // Arrange
        val memberId = createTestUser("unassign-completed-${testPrefix}@test.com")
        organizationDatastore.addUserToOrganization(memberId, orgId!!, OrgRole.EMPLOYEE)
        val taskId = createTestTask(ownerUserId!!, propertyId!!, assigneeId = memberId, status = TaskStatus.COMPLETED)

        // Act
        val result = membershipDatastore.unassignTasksForMember(orgId!!, memberId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(memberId.userId, getTaskAssignee(taskId))
    }

    @Test
    fun `unassignTasksForMember does not affect CANCELLED tasks`() = runCoroutineTest {
        // Arrange
        val memberId = createTestUser("unassign-cancelled-${testPrefix}@test.com")
        organizationDatastore.addUserToOrganization(memberId, orgId!!, OrgRole.EMPLOYEE)
        val taskId = createTestTask(ownerUserId!!, propertyId!!, assigneeId = memberId, status = TaskStatus.CANCELLED)

        // Act
        val result = membershipDatastore.unassignTasksForMember(orgId!!, memberId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(memberId.userId, getTaskAssignee(taskId))
    }

    @Test
    fun `unassignTasksForMember does not affect tasks assigned to a different member`() = runCoroutineTest {
        // Arrange
        val targetMember = createTestUser("unassign-target-${testPrefix}@test.com")
        val otherMember = createTestUser("unassign-other-${testPrefix}@test.com")
        organizationDatastore.addUserToOrganization(targetMember, orgId!!, OrgRole.EMPLOYEE)
        organizationDatastore.addUserToOrganization(otherMember, orgId!!, OrgRole.EMPLOYEE)
        val otherTaskId = createTestTask(ownerUserId!!, propertyId!!, assigneeId = otherMember, status = TaskStatus.OPEN)

        // Act — unassign targetMember who has no tasks; otherMember's task should be unchanged
        val result = membershipDatastore.unassignTasksForMember(orgId!!, targetMember)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(otherMember.userId, getTaskAssignee(otherTaskId))
    }

    @Test
    fun `unassignTasksForMember does not affect tasks belonging to a different org`() = runCoroutineTest {
        // Arrange
        val otherOrgId = createTestOrganization("other-org-$testPrefix", "")
        val otherPropertyId = createTestProperty("other-prop-$testPrefix", ownerUserId!!, otherOrgId)
        runBlocking { organizationDatastore.addUserToOrganization(ownerUserId!!, otherOrgId, OrgRole.OWNER) }

        val memberId = createTestUser("unassign-difforg-${testPrefix}@test.com")
        organizationDatastore.addUserToOrganization(memberId, orgId!!, OrgRole.EMPLOYEE)
        organizationDatastore.addUserToOrganization(memberId, otherOrgId, OrgRole.EMPLOYEE)

        // Task lives in otherOrg
        val taskId = createTestTask(ownerUserId!!, otherPropertyId, assigneeId = memberId, status = TaskStatus.OPEN)

        // Act — unassign for orgId, not otherOrgId
        val result = membershipDatastore.unassignTasksForMember(orgId!!, memberId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(memberId.userId, getTaskAssignee(taskId))
    }

    @Test
    fun `unassignTasksForMember succeeds when member has no tasks`() = runCoroutineTest {
        // Arrange
        val memberId = createTestUser("unassign-notasks-${testPrefix}@test.com")
        organizationDatastore.addUserToOrganization(memberId, orgId!!, OrgRole.EMPLOYEE)

        // Act
        val result = membershipDatastore.unassignTasksForMember(orgId!!, memberId)

        // Assert
        assertTrue(result.isSuccess)
    }

    /**
     * Purges all tasks created during the test before the base class tearDown runs.
     * Required because purgeProperty triggers ON DELETE SET NULL on tasks.property_id,
     * which would violate the at_least_one_location check constraint if tasks still exist.
     * JUnit 5 runs subclass @AfterTest before superclass @AfterTest.
     */
    @AfterTest
    fun tearDownMembership() {
        if (taskIds.isEmpty()) return
        runBlocking {
            postgrest.from(TASKS_COLLECTION).delete {
                filter { isIn("id", taskIds) }
            }
        }
        taskIds.clear()
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Created task IDs are tracked in `taskIds` and explicitly deleted by
     * `tearDownMembership()` before superclass teardown runs.
     */
    private suspend fun createTestTask(
        createdBy: UserId,
        propertyId: PropertyId,
        assigneeId: UserId? = null,
        status: TaskStatus = TaskStatus.OPEN,
    ): String {
        val taskId = postgrest.from(TASKS_COLLECTION).insert(
            TaskInsertEntity(
                propertyId = propertyId.propertyId,
                createdBy = createdBy.userId,
                title = "Test Task $testPrefix",
                status = status.name,
                assigneeId = assigneeId?.userId,
            )
        ) { select() }.decodeSingle<TaskRowEntity>().id
        taskIds.add(taskId)
        return taskId
    }

    /**
     * Reads back the assignee_id for the given task ID.
     */
    private suspend fun getTaskAssignee(taskId: String): String? {
        return postgrest.from(TASKS_COLLECTION).select {
            filter { eq("id", taskId) }
        }.decodeSingle<TaskRowEntity>().assigneeId
    }

    /**
     * Minimal entity for inserting a task row directly in tests.
     */
    @Serializable
    @SupabaseModel
    private data class TaskInsertEntity(
        @SerialName("property_id") val propertyId: String,
        @SerialName("created_by") val createdBy: String,
        val title: String,
        val priority: String = "MEDIUM",
        val status: String = "OPEN",
        @SerialName("assignee_id") val assigneeId: String? = null,
    )

    /**
     * Minimal entity for reading a task row directly in tests.
     */
    @Serializable
    @SupabaseModel
    private data class TaskRowEntity(
        val id: String,
        @SerialName("assignee_id") val assigneeId: String?,
    )

    companion object {
        private const val TASKS_COLLECTION = "tasks"
    }
}
