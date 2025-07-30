package com.cramsan.framework.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CoreUriTest {

    @Test
    fun testCoreUriHashCode() {
        // Test that same URIs produce same hash codes
        val uri1 = CoreUri.createUri("https://example.com/path")
        val uri2 = CoreUri.createUri("https://example.com/path")
        
        assertEquals(uri1.hashCode(), uri2.hashCode())
    }

    @Test
    fun testCoreUriEquals() {
        // Test that same URIs are equal
        val uri1 = CoreUri.createUri("https://example.com/path")
        val uri2 = CoreUri.createUri("https://example.com/path")
        val uri3 = CoreUri.createUri("https://different.com/path")
        
        assertEquals(uri1, uri2)
        assertNotEquals(uri1, uri3)
        assertFalse(uri1.equals(null))
        assertFalse(uri1.equals("not a uri"))
        
        // Test reflexivity
        assertEquals(uri1, uri1)
    }

    @Test
    fun testCoreUriGetUri() {
        val uriString = "https://example.com/path?param=value"
        val uri = CoreUri.createUri(uriString)
        
        assertEquals(uriString, uri.getUri())
    }

    @Test
    fun testCoreUriToString() {
        val uriString = "https://example.com/path?param=value"
        val uri = CoreUri.createUri(uriString)
        
        assertEquals(uriString, uri.toString())
    }

    @Test
    fun testDifferentUrisHaveDifferentHashCodes() {
        // Test that different URIs typically produce different hash codes
        val uri1 = CoreUri.createUri("https://example.com/path1")
        val uri2 = CoreUri.createUri("https://example.com/path2")
        
        // While hash collision is possible, these specific strings should have different hashes
        assertNotEquals(uri1.hashCode(), uri2.hashCode())
    }

    @Test
    fun testHashCodeConsistency() {
        // Test that hash code is consistent across multiple calls
        val uri = CoreUri.createUri("https://example.com/path")
        val hash1 = uri.hashCode()
        val hash2 = uri.hashCode()
        
        assertEquals(hash1, hash2)
    }

    @Test
    fun testCrossTargetConsistency() {
        // Test that hashing should work consistently across platforms
        val testCases = listOf(
            "https://example.com",
            "http://test.com/path",
            "https://api.example.com/v1/users?id=123&name=test",
            "file:///local/path/to/file.txt",
            ""
        )
        
        testCases.forEach { uriString ->
            val uri1 = CoreUri.createUri(uriString)
            val uri2 = CoreUri.createUri(uriString)
            
            // Same URI strings should always produce same CoreUri objects
            assertEquals(uri1, uri2)
            assertEquals(uri1.hashCode(), uri2.hashCode())
            assertEquals(uri1.getUri(), uri2.getUri())
            assertEquals(uri1.toString(), uri2.toString())
        }
    }
}