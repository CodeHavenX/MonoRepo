package com.cramsan.framework.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class HashingTest {

    @Test
    fun `murmurhash returns same result for same input`() {
        val data = "hello world".encodeToByteArray()
        val hash1 = com.cramsan.framework.core.Hashing.murmurhash(data)
        val hash2 = com.cramsan.framework.core.Hashing.murmurhash(data)
        assertEquals(hash1, hash2, "Hash should be consistent for same input")
    }

    @Test
    fun `murmurhash returns different result for different input`() {
        val data1 = "hello".encodeToByteArray()
        val data2 = "world".encodeToByteArray()
        val hash1 = com.cramsan.framework.core.Hashing.murmurhash(data1)
        val hash2 = com.cramsan.framework.core.Hashing.murmurhash(data2)
        assertNotEquals(hash1, hash2, "Hash should differ for different input")
    }

    @Test
    fun `murmurhash handles empty array`() {
        val data = ByteArray(0)
        val hash = com.cramsan.framework.core.Hashing.murmurhash(data)
        assertEquals(0, hash, "Hash of empty array should be 0")
    }

    @Test
    fun `murmurhash handles single byte`() {
        val data = byteArrayOf(42)
        val hash = com.cramsan.framework.core.Hashing.murmurhash(data)
        // 1 * 31 + 42 = 73
        assertEquals(73, hash)
    }

    @Test
    fun `murmurhash handles negative bytes`() {
        val data = byteArrayOf(-1, -2, -3)
        val hash = com.cramsan.framework.core.Hashing.murmurhash(data)
        // Just check it runs and returns an Int
        assertEquals(hash, com.cramsan.framework.core.Hashing.murmurhash(data))
    }
}