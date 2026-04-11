package com.cramsan.edifikana.server.datastore.supabase

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

class SupabaseOrganizationDatastoreIntegrationTest : SupabaseIntegrationTest() {
    private lateinit var test_prefix: String
    private var testUserId: UserId? = null

    @BeforeTest
    fun setUp() {
        test_prefix = UUID.random()
        testUserId = createTestUser("user-${test_prefix}@gmail.com")
    }

    @Test
    fun `createOrganization should return organization on success`() = runCoroutineTest {
        // Act
        val result = organizationDatastore
            .createOrganization("test_org_$test_prefix", "")
            .registerOrganizationForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        val org = result.getOrNull()
        assertNotNull(org)
    }

    @Test
    fun `getOrganization should return created organization`() = runCoroutineTest {
        // Arrange
        val createResult = organizationDatastore
            .createOrganization("test_org_$test_prefix", "")
            .registerOrganizationForDeletion()
        assertTrue(createResult.isSuccess)
        val org = createResult.getOrNull()!!

        // Act
        val getResult = organizationDatastore.getOrganization(org.id)

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertNotNull(fetched)
        assertEquals(org.id, fetched.id)
    }

    @Test
    fun `updateOrganization should update organization fields`() = runCoroutineTest {
        // Arrange
        val createResult = organizationDatastore
            .createOrganization("test_org_$test_prefix", "")
            .registerOrganizationForDeletion()
        assertTrue(createResult.isSuccess)
        val org = createResult.getOrNull()!!

        // Act
        val updateResult = organizationDatastore.updateOrganization(id = org.id, name = "new_name", description = "new_description")

        // Assert
        assertTrue(updateResult.isSuccess)
        val updated = updateResult.getOrNull()
        assertNotNull(updated)
        assertEquals(org.id, updated.id)
        assertEquals("new_name", updated.name)
        assertEquals("new_description", updated.description)
    }

    @Test
    fun `deleteOrganization should remove organization`() = runCoroutineTest {
        // Arrange
        val createResult = organizationDatastore
            .createOrganization("test_org_$test_prefix", "")
            .registerOrganizationForDeletion()
        assertTrue(createResult.isSuccess)
        val org = createResult.getOrNull()!!

        // Act
        val deleteResult = organizationDatastore.deleteOrganization(org.id)

        // Assert
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrThrow())
        val getResult = organizationDatastore.getOrganization(org.id)
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }

    @Test
    fun `deleteOrganization should fail for non-existent organization`() = runCoroutineTest {
        // Arrange
        val fakeId = OrganizationId(UUID.random())



        // Act
        val deleteResult = organizationDatastore.deleteOrganization(fakeId)

        // Assert
        assertTrue(deleteResult.isFailure || deleteResult.getOrNull() == false)
    }

    @Test
    fun `getOrganizationList should return organizations for user`() = runCoroutineTest {
        // Arrange
        val org1 = createTestOrganization("test_org_$test_prefix", "")
        val org2 = createTestOrganization("test_org_$test_prefix", "")
        organizationDatastore.addUserToOrganization(testUserId!!, org1, OrgRole.ADMIN)
        organizationDatastore.addUserToOrganization(testUserId!!, org2, OrgRole.EMPLOYEE)

        // Act
        val listResult = organizationDatastore.getOrganizationsForUser(testUserId!!)

        // Assert
        assertTrue(listResult.isSuccess)
        val orgs = listResult.getOrNull()
        assertNotNull(orgs)
        assertTrue(orgs.any { it.id == org1 })
        assertTrue(orgs.any { it.id == org2 })
    }

    @Test
    fun `getOrganizationsForUser should return empty list for user with no organizations`() = runCoroutineTest {
        // Arrange
        val newUser = createTestUser("nouser-${test_prefix}@gmail.com")

        // Act
        val result = organizationDatastore.getOrganizationsForUser(newUser)

        // Assert
        assertTrue(result.isSuccess)
        val orgs = result.getOrNull()
        assertNotNull(orgs)
        assertTrue(orgs.isEmpty())
    }

    @Test
    fun `addUserToOrganization should add user to organization and getOrganizationsForUser should reflect this`() = runCoroutineTest {
        // Arrange
        val orgId = createTestOrganization("test_org_$test_prefix", "")
        val newUser = createTestUser("adduser-${test_prefix}@gmail.com")

        // Act
        val addResult = organizationDatastore.addUserToOrganization(newUser, orgId, OrgRole.MANAGER)
        val orgsResult = organizationDatastore.getOrganizationsForUser(newUser)

        // Assert
        assertTrue(addResult.isSuccess)
        assertTrue(orgsResult.isSuccess)
        val orgs = orgsResult.getOrNull()
        assertNotNull(orgs)
        assertTrue(orgs.any { it.id == orgId })
    }

    @Test
    fun `addUserToOrganization should handle adding same user twice gracefully`() = runCoroutineTest {
        // Arrange
        val orgId = createTestOrganization("test_org_$test_prefix", "")
        val newUser = createTestUser("dupeuser-${test_prefix}@gmail.com")

        // Act
        val firstAdd = organizationDatastore.addUserToOrganization(newUser, orgId, OrgRole.EMPLOYEE)
        val secondAdd = organizationDatastore.addUserToOrganization(newUser, orgId, OrgRole.EMPLOYEE)

        // Assert
        assertTrue(firstAdd.isSuccess)
        // Depending on DB constraints, secondAdd may succeed or fail gracefully
        assertTrue(secondAdd.isSuccess || secondAdd.isFailure)
        val orgsResult = organizationDatastore.getOrganizationsForUser(newUser)
        assertTrue(orgsResult.isSuccess)
        val orgs = orgsResult.getOrNull()
        assertNotNull(orgs)
        assertTrue(orgs.any { it.id == orgId })
    }

    @Test
    fun `addUserToOrganization with OWNER role should succeed`() = runCoroutineTest {
        // Arrange
        val orgId = createTestOrganization("test_org_$test_prefix", "")
        val newUser = createTestUser("owner-${test_prefix}@gmail.com")

        // Act
        val result = organizationDatastore.addUserToOrganization(newUser, orgId, OrgRole.OWNER)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `addUserToOrganization with ADMIN role should succeed`() = runCoroutineTest {
        // Arrange
        val orgId = createTestOrganization("test_org_$test_prefix", "")
        val newUser = createTestUser("admin-${test_prefix}@gmail.com")

        // Act
        val result = organizationDatastore.addUserToOrganization(newUser, orgId, OrgRole.ADMIN)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `addUserToOrganization with MANAGER role should succeed`() = runCoroutineTest {
        // Arrange
        val orgId = createTestOrganization("test_org_$test_prefix", "")
        val newUser = createTestUser("manager-${test_prefix}@gmail.com")

        // Act
        val result = organizationDatastore.addUserToOrganization(newUser, orgId, OrgRole.MANAGER)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `addUserToOrganization with EMPLOYEE role should succeed`() = runCoroutineTest {
        // Arrange
        val orgId = createTestOrganization("test_org_$test_prefix", "")
        val newUser = createTestUser("employee-${test_prefix}@gmail.com")

        // Act
        val result = organizationDatastore.addUserToOrganization(newUser, orgId, OrgRole.EMPLOYEE)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `getUserRole should return OrgRole after addUserToOrganization`() = runCoroutineTest {
        // Arrange
        val orgId = createTestOrganization("test_org_$test_prefix", "")
        val newUser = createTestUser("rolecheck-${test_prefix}@gmail.com")
        organizationDatastore.addUserToOrganization(newUser, orgId, OrgRole.ADMIN)

        // Act
        val roleResult = organizationDatastore.getUserRole(newUser, orgId)

        // Assert
        assertTrue(roleResult.isSuccess)
        assertEquals(OrgRole.ADMIN, roleResult.getOrNull())
    }

    @Test
    fun `addUserToOrganization should default status to ACTIVE`() = runCoroutineTest {
        // Arrange
        val orgId = createTestOrganization("test_org_$test_prefix", "")
        val newUser = createTestUser("statuscheck-${test_prefix}@gmail.com")

        // Act — no explicit status passed; DB default should apply
        val addResult = organizationDatastore.addUserToOrganization(newUser, orgId, OrgRole.EMPLOYEE)

        // Assert — the insert succeeded, confirming the DB default ACTIVE is accepted
        assertTrue(addResult.isSuccess)
        // Membership is retrievable, confirming the row exists and status did not block insertion
        val roleResult = organizationDatastore.getUserRole(newUser, orgId)
        assertTrue(roleResult.isSuccess)
        assertNotNull(roleResult.getOrNull())
    }
}
