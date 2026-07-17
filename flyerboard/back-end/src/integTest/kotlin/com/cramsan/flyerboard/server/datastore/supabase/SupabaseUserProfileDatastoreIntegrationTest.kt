package com.cramsan.flyerboard.server.datastore.supabase

import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import com.cramsan.framework.utils.uuid.UUID
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertInstanceOf
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Integration tests for [SupabaseUserProfileDatastore] against a real Supabase instance.
 */
class SupabaseUserProfileDatastoreIntegrationTest : SupabaseIntegrationTest() {

    private lateinit var testPrefix: String

    @BeforeTest
    fun setup() {
        testPrefix = UUID.random()
    }

    @Test
    fun `getUserProfile should return null when no profile exists`() =
        runBlocking {
            val userId = createTestAuthUser("${testPrefix}_noprofile@example.com")

            val result = userProfileDatastore.getUserProfile(userId)

            assertTrue(result.isSuccess)
            assertNull(result.getOrNull())
        }

    @Test
    fun `createUserProfile should create a profile with the given role`() =
        runBlocking {
            val userId = createTestAuthUser("${testPrefix}_profile@example.com")

            val result = userProfileDatastore.createUserProfile(userId, UserRole.ADMIN)

            assertTrue(result.isSuccess)
            val profile = result.getOrThrow()
            assertEquals(userId, profile.id)
            assertEquals(UserRole.ADMIN, profile.role)
        }

    @Test
    fun `createUserProfile should fail with ForbiddenException when a profile already exists`() =
        runBlocking {
            val userId = createTestAuthUser("${testPrefix}_dupprofile@example.com")
            userProfileDatastore.createUserProfile(userId, UserRole.USER).getOrThrow()

            val result = userProfileDatastore.createUserProfile(userId, UserRole.USER)

            assertTrue(result.isFailure)
            assertInstanceOf<ClientRequestExceptions.ForbiddenException>(result.exceptionOrNull())
            Unit
        }

    @Test
    fun `getUserProfile should return the created profile`() =
        runBlocking {
            val userId = createTestAuthUser("${testPrefix}_getprofile@example.com")
            createTestUserProfile(userId, UserRole.USER)

            val result = userProfileDatastore.getUserProfile(userId)

            assertTrue(result.isSuccess)
            assertEquals(UserRole.USER, result.getOrNull()?.role)
        }

    @Test
    fun `updateUserRole should change the role of an existing profile`() =
        runBlocking {
            val userId = createTestAuthUser("${testPrefix}_updaterole@example.com")
            createTestUserProfile(userId, UserRole.USER)

            val result = userProfileDatastore.updateUserRole(userId, UserRole.ADMIN)

            assertTrue(result.isSuccess)
            assertEquals(UserRole.ADMIN, result.getOrNull()?.role)
            val fetched = userProfileDatastore.getUserProfile(userId).getOrThrow()
            assertEquals(UserRole.ADMIN, fetched?.role)
        }

    @Test
    fun `updateUserRole should fail when no profile exists`() =
        runBlocking {
            val userId = createTestAuthUser("${testPrefix}_noprofileupdate@example.com")

            val result = userProfileDatastore.updateUserRole(userId, UserRole.ADMIN)

            assertTrue(result.isFailure)
        }
}
