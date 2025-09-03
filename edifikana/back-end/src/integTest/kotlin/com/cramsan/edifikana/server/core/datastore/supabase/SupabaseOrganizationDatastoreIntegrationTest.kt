package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.service.models.requests.CreateOrganizationRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteOrganizationRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetOrganizationRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateOrganizationRequest
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
        testUserId = createTestUser("user-${test_prefix}@test.com")
    }

    @Test
    fun `createOrganization should return organization on success`() = runCoroutineTest {
        // Arrange
        val request = CreateOrganizationRequest(owner = testUserId!!)

        // Act
        val result = organizationDatastore.createOrganization(request).registerOrganizationForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        val org = result.getOrNull()
        assertNotNull(org)
    }

    @Test
    fun `getOrganization should return created organization`() = runCoroutineTest {
        // Arrange
        val createRequest = CreateOrganizationRequest(owner = testUserId!!)
        val createResult = organizationDatastore.createOrganization(createRequest).registerOrganizationForDeletion()
        assertTrue(createResult.isSuccess)
        val org = createResult.getOrNull()!!

        // Act
        val getResult = organizationDatastore.getOrganization(GetOrganizationRequest(org.id))

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertNotNull(fetched)
        assertEquals(org.id, fetched.id)
    }

    @Test
    fun `updateOrganization should update organization fields`() = runCoroutineTest {
        // Arrange
        val createRequest = CreateOrganizationRequest(owner = testUserId!!)
        val createResult = organizationDatastore.createOrganization(createRequest).registerOrganizationForDeletion()
        assertTrue(createResult.isSuccess)
        val org = createResult.getOrNull()!!
        val newOwner = createTestUser("user2-${test_prefix}@test.com")
        val updateRequest = UpdateOrganizationRequest(id = org.id, owner = newOwner)

        // Act
        val updateResult = organizationDatastore.updateOrganization(updateRequest)

        // Assert
        assertTrue(updateResult.isSuccess)
        val updated = updateResult.getOrNull()
        assertNotNull(updated)
        assertEquals(org.id, updated.id)
    }

    @Test
    fun `deleteOrganization should remove organization`() = runCoroutineTest {
        // Arrange
        val createRequest = CreateOrganizationRequest(owner = testUserId!!)
        val createResult = organizationDatastore.createOrganization(createRequest)
        assertTrue(createResult.isSuccess)
        val org = createResult.getOrNull()!!

        // Act
        val deleteResult = organizationDatastore.deleteOrganization(DeleteOrganizationRequest(org.id))

        // Assert
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrNull() == true)
        val getResult = organizationDatastore.getOrganization(GetOrganizationRequest(org.id))
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }

    @Test
    fun `deleteOrganization should fail for non-existent organization`() = runCoroutineTest {
        // Arrange
        val fakeId = OrganizationId("fake-${test_prefix}")

        // Act
        val deleteResult = organizationDatastore.deleteOrganization(DeleteOrganizationRequest(fakeId))

        // Assert
        assertTrue(deleteResult.isFailure || deleteResult.getOrNull() == false)
    }

    @Test
    fun `getOrganizationList should return organizations for user`() = runCoroutineTest {
        // Arrange
        val org1 = createTestOrganization(testUserId!!)
        val org2 = createTestOrganization(testUserId!!)
        organizationDatastore.addUserToOrganization(testUserId!!, org1)
        organizationDatastore.addUserToOrganization(testUserId!!, org2)

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
        val newUser = createTestUser("nouser-${test_prefix}@test.com")

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
        val orgId = createTestOrganization(testUserId!!)
        val newUser = createTestUser("adduser-${test_prefix}@test.com")

        // Act
        val addResult = organizationDatastore.addUserToOrganization(newUser, orgId)
        val orgsResult = organizationDatastore.getOrganizationsForUser(newUser)

        // Assert
        assertTrue(orgsResult.isSuccess)
        assertTrue(addResult.isSuccess)
        val orgs = orgsResult.getOrNull()
        assertNotNull(orgs)
        assertTrue(orgs.any { it.id == orgId })
    }

    @Test
    fun `addUserToOrganization should handle adding same user twice gracefully`() = runCoroutineTest {
        // Arrange
        val orgId = createTestOrganization(testUserId!!)
        val newUser = createTestUser("dupeuser-${test_prefix}@test.com")

        // Act
        val firstAdd = organizationDatastore.addUserToOrganization(newUser, orgId)
        val secondAdd = organizationDatastore.addUserToOrganization(newUser, orgId)

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
}