package com.cramsan.flyerboard.server.datastore.supabase

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.service.models.Flyer
import com.cramsan.framework.utils.uuid.UUID
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Integration tests for [SupabaseFlyerDatastore] against a real Supabase instance.
 */
@OptIn(ExperimentalTime::class)
class SupabaseFlyerDatastoreIntegrationTest : SupabaseIntegrationTest() {

    private lateinit var testPrefix: String
    private var uploaderId: UserId = UserId("")

    @BeforeTest
    fun setup() {
        testPrefix = UUID.random()
        uploaderId = createTestAuthUser("${testPrefix}_uploader@example.com")
        createTestUserProfile(uploaderId)
    }

    @Test
    fun `createFlyer should create a flyer with PENDING status`() =
        runBlocking {
            val flyerId = FlyerId(UUID.random())

            val result =
                flyerDatastore.createFlyer(
                    id = flyerId,
                    title = "$testPrefix Title",
                    description = "Description",
                    filePath = flyerId.flyerId,
                    uploaderId = uploaderId,
                    expiresAt = null,
                )

            assertTrue(result.isSuccess)
            val flyer = result.getOrThrow()
            assertEquals(flyerId, flyer.id)
            assertEquals(FlyerStatus.PENDING, flyer.status)
            assertEquals(uploaderId, flyer.uploaderId)
        }

    @Test
    fun `getFlyer should return the created flyer`() =
        runBlocking {
            val flyer = createTestFlyer()

            val result = flyerDatastore.getFlyer(flyer.id)

            assertTrue(result.isSuccess)
            assertEquals(flyer.id, result.getOrNull()?.id)
        }

    @Test
    fun `getFlyer should return null when not found`() =
        runBlocking {
            val result = flyerDatastore.getFlyer(FlyerId(UUID.random()))

            assertTrue(result.isSuccess)
            assertNull(result.getOrNull())
        }

    @Test
    fun `listFlyers should filter by status`() =
        runBlocking {
            val pending = createTestFlyer()
            val approved = createTestFlyer()
            flyerDatastore.updateFlyer(approved.id, null, null, FlyerStatus.APPROVED, null).getOrThrow()

            val result = flyerDatastore.listFlyers(status = FlyerStatus.APPROVED, query = null, offset = 0, limit = 20)

            assertTrue(result.isSuccess)
            val ids = result.getOrThrow().items.map { it.id }
            assertTrue(ids.contains(approved.id))
            assertTrue(!ids.contains(pending.id))
        }

    @Test
    fun `listFlyers should match search query against title`() =
        runBlocking {
            val flyer = createTestFlyer(title = "$testPrefix Concert Night")

            val result = flyerDatastore.listFlyers(status = null, query = "Concert", offset = 0, limit = 20)

            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow().items.any { it.id == flyer.id })
        }

    @Test
    fun `updateFlyer should update provided fields and leave others unchanged`() =
        runBlocking {
            val flyer = createTestFlyer(title = "Original Title")

            val result =
                flyerDatastore.updateFlyer(
                    id = flyer.id,
                    title = "Updated Title",
                    description = null,
                    status = null,
                    expiresAt = null,
                )

            assertTrue(result.isSuccess)
            val updated = result.getOrThrow()
            assertEquals("Updated Title", updated.title)
            assertEquals(flyer.description, updated.description)
        }

    @Test
    fun `listExpiredFlyers should return approved flyers past their expiry`() =
        runBlocking {
            val past = Instant.fromEpochSeconds(0)
            val flyer = createTestFlyer(expiresAt = past)
            flyerDatastore.updateFlyer(flyer.id, null, null, FlyerStatus.APPROVED, null).getOrThrow()

            val result = flyerDatastore.listExpiredFlyers(now = Clock.System.now())

            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow().any { it.id == flyer.id })
        }

    @Test
    fun `listFlyersByUploader should return only the uploader's flyers`() =
        runBlocking {
            val ownFlyer = createTestFlyer()
            val otherUploaderId = createTestAuthUser("${testPrefix}_other@example.com")
            createTestUserProfile(otherUploaderId)
            val otherFlyer = createTestFlyer(uploaderId = otherUploaderId)

            val result = flyerDatastore.listFlyersByUploader(uploaderId, offset = 0, limit = 20)

            assertTrue(result.isSuccess)
            val ids = result.getOrThrow().items.map { it.id }
            assertTrue(ids.contains(ownFlyer.id))
            assertTrue(!ids.contains(otherFlyer.id))
        }

    private suspend fun createTestFlyer(
        title: String = "$testPrefix Flyer",
        expiresAt: Instant? = null,
        uploaderId: UserId = this.uploaderId,
    ): Flyer {
        val flyerId = FlyerId(UUID.random())
        return flyerDatastore
            .createFlyer(
                id = flyerId,
                title = title,
                description = "Description",
                filePath = flyerId.flyerId,
                uploaderId = uploaderId,
                expiresAt = expiresAt,
            ).getOrThrow()
    }
}
