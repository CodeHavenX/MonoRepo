package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.asset.AssetId
import io.github.jan.supabase.storage.Storage
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Unit tests for [SupabaseStorageDatastore] parsing logic.
 */
class SupabaseStorageDatastoreTest {
    private val datastore = SupabaseStorageDatastore(storage = mockk<Storage>())

    // -------------------------------------------------------------------------
    // extractBucketAndObjectPath
    // -------------------------------------------------------------------------

    @Test
    fun `extractBucketAndObjectPath splits on first slash only`() {
        // Act
        val (bucket, objectPath) =
            datastore.extractBucketAndObjectPath(
                AssetId("images/timecard-images/employee.png"),
            )

        // Assert
        assertEquals("images", bucket)
        assertEquals("timecard-images/employee.png", objectPath)
    }

    @Test
    fun `extractBucketAndObjectPath handles deeply nested object path`() {
        // Act
        val (bucket, objectPath) =
            datastore.extractBucketAndObjectPath(
                AssetId("images/private/properties/abc123/photo.jpg"),
            )
        // Assert
        assertEquals("images", bucket)
        assertEquals("private/properties/abc123/photo.jpg", objectPath)
    }

    @Test
    fun `extractBucketAndObjectPath handles flat object path with no sub-folders`() {
        // Act
        val (bucket, objectPath) =
            datastore.extractBucketAndObjectPath(
                AssetId("documents/report.pdf"),
            )
        // Assert
        assertEquals("documents", bucket)
        assertEquals("report.pdf", objectPath)
    }

    @Test
    fun `extractBucketAndObjectPath throws for missing object path`() {
        // Act & Assert
        assertThrows<IllegalArgumentException> {
            datastore.extractBucketAndObjectPath(AssetId("images"))
        }
    }

    @Test
    fun `extractBucketAndObjectPath throws for blank bucket`() {
        // Act & Assert
        assertThrows<IllegalArgumentException> {
            datastore.extractBucketAndObjectPath(AssetId("/employee.png"))
        }
    }

    @Test
    fun `extractBucketAndObjectPath throws for blank object path`() {
        // Act & Assert
        assertThrows<IllegalArgumentException> {
            datastore.extractBucketAndObjectPath(AssetId("images/"))
        }
    }
}
