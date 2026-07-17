package com.cramsan.flyerboard.server.datastore.supabase

import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.service.models.User
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import com.cramsan.framework.utils.uuid.UUID
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertInstanceOf
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration tests for [SupabaseUserDatastore] against a real Supabase instance.
 */
class SupabaseUserDatastoreIntegrationTest : SupabaseIntegrationTest() {

    private lateinit var testPrefix: String

    @BeforeTest
    fun setup() {
        testPrefix = UUID.random()
    }

    @Test
    fun `createUser should create a user row for an existing auth user`() =
        runBlocking {
            val userId = createTestAuthUser("${testPrefix}_user@example.com")

            val result = userDatastore.createUser(userId, "First", "Last")

            assertTrue(result.isSuccess)
            assertEquals(User(id = userId, firstName = "First", lastName = "Last"), result.getOrNull())
        }

    @Test
    fun `createUser should fail when the auth user does not exist`() =
        runBlocking {
            val result = userDatastore.createUser(UserId(UUID.random()), "First", "Last")

            assertTrue(result.isFailure)
        }

    @Test
    fun `createUser should fail with ForbiddenException when a user row already exists`() =
        runBlocking {
            val userId = createTestAuthUser("${testPrefix}_dup@example.com")
            userDatastore.createUser(userId, "First", "Last").getOrThrow()

            val result = userDatastore.createUser(userId, "First", "Last")

            assertTrue(result.isFailure)
            assertInstanceOf<ClientRequestExceptions.ForbiddenException>(result.exceptionOrNull())
            Unit
        }

    @Test
    fun `getUser should return the created user`() =
        runBlocking {
            val userId = createTestAuthUser("${testPrefix}_get@example.com")
            createTestUser(userId, "Get", "User")

            val result = userDatastore.getUser(userId)

            assertTrue(result.isSuccess)
            assertEquals(User(id = userId, firstName = "Get", lastName = "User"), result.getOrNull())
        }

    @Test
    fun `getUser should fail when the user does not exist`() =
        runBlocking {
            val result = userDatastore.getUser(UserId(UUID.random()))

            assertTrue(result.isFailure)
            assertInstanceOf<ClientRequestExceptions.NotFoundException>(result.exceptionOrNull())
            Unit
        }
}
