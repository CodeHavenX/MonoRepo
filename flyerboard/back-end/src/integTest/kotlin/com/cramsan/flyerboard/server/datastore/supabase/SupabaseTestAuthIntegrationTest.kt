package com.cramsan.flyerboard.server.datastore.supabase

import com.cramsan.framework.utils.password.generateRandomPassword
import com.cramsan.framework.utils.uuid.UUID
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Integration tests for [SupabaseIntegrationTest.createTestAuthSession] and
 * [SupabaseIntegrationTest.signInAsSeededUser] against a real Supabase instance.
 */
class SupabaseTestAuthIntegrationTest : SupabaseIntegrationTest() {

    private lateinit var testPrefix: String

    @BeforeTest
    fun setup() {
        testPrefix = UUID.random()
    }

    @Test
    fun `createTestAuthSession returns a real access token`() {
        val session = createTestAuthSession("${testPrefix}_session@example.com")

        assertTrue(session.accessToken.isNotBlank())
        assertTrue(session.userId.isNotBlank())
    }

    @Test
    fun `signInAsSeededUser returns a real access token for an existing user`() {
        val email = "${testPrefix}_seeded@example.com"
        val userId = createTestAuthUser(email)

        val session = signInAsSeededUser(userId, email, generateRandomPassword())

        assertTrue(session.accessToken.isNotBlank())
        assertTrue(session.userId == userId.userId)
    }

    @Test
    fun `access token from createTestAuthSession does not disrupt the shared service-role client`() =
        runBlocking {
            val session = createTestAuthSession("${testPrefix}_isolation@example.com")
            assertTrue(session.accessToken.isNotBlank())

            // If the shared Auth singleton had been signed in on, this would now run as the test
            // user (or fail) instead of falling back to the service-role key.
            val userId = createTestAuthUser("${testPrefix}_after@example.com")
            val result = userDatastore.createUser(userId, "First", "Last")

            assertTrue(result.isSuccess)
        }
}
