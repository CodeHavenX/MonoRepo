package com.cramsan.framework.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class HashingTest {

    @Test
    fun `insecureHash returns same result for same input`() {
        val data = "hello world".encodeToByteArray()
        val hash1 = com.cramsan.framework.core.Hashing.insecureHash(data)
        val hash2 = com.cramsan.framework.core.Hashing.insecureHash(data)
        assertEquals(hash1, hash2, "Hash should be consistent for same input")
    }

    @Test
    fun `insecureHash returns different result for different input`() {
        val data1 = "hello".encodeToByteArray()
        val data2 = "world".encodeToByteArray()
        val hash1 = com.cramsan.framework.core.Hashing.insecureHash(data1)
        val hash2 = Hashing.insecureHash(data2)
        assertNotEquals(hash1, hash2, "Hash should differ for different input")
    }

    @Test
    fun `insecureHash handles empty array`() {
        val data = ByteArray(0)
        val hash = com.cramsan.framework.core.Hashing.insecureHash(data)
        assertEquals(0, hash, "Hash of empty array should be 0")
    }

    @Test
    fun `insecureHash handles single byte`() {
        val data = byteArrayOf(42)
        val hash = com.cramsan.framework.core.Hashing.insecureHash(data)
        // 1 * 31 + 42 = 73
        assertEquals(73, hash)
    }

    @Test
    fun `insecureHash handles negative bytes`() {
        val data = byteArrayOf(-1, -2, -3)
        val hash = com.cramsan.framework.core.Hashing.insecureHash(data)
        // Just check it runs and returns an Int
        assertEquals(hash, com.cramsan.framework.core.Hashing.insecureHash(data))
    }
}