package com.cramsan.framework.utils.hash

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class HashUtilsTest {

    @Test
    fun testHashEmptyArray() {
        val result = HashUtils.hash(byteArrayOf())
        assertEquals(8, result.length, "Hash should be 8 characters long")
        val hexPattern = Regex("^[0-9a-f]+$")
        assert(hexPattern.matches(result)) { "Hash should be a valid hexadecimal string" }
    }

    @Test
    fun testHashSingleByte() {
        val result = HashUtils.hash(byteArrayOf(65)) // 'A'
        assertEquals(8, result.length, "Hash should be 8 characters long")
        val hexPattern = Regex("^[0-9a-f]+$")
        assert(hexPattern.matches(result)) { "Hash should be a valid hexadecimal string" }
    }

    @Test
    fun testHashString() {
        val testString = "Hello, World!"
        val result = HashUtils.hash(testString.encodeToByteArray())
        // Should be consistent across multiple calls
        val result2 = HashUtils.hash(testString.encodeToByteArray())
        assertEquals(result, result2)
    }

    @Test
    fun testHashConsistency() {
        val data = "test data for hashing".encodeToByteArray()
        val hash1 = HashUtils.hash(data)
        val hash2 = HashUtils.hash(data)
        assertEquals(hash1, hash2, "Hash should be consistent across multiple calls")
    }

    @Test
    fun testDifferentInputsProduceDifferentHashes() {
        val data1 = "first string".encodeToByteArray()
        val data2 = "second string".encodeToByteArray()
        val hash1 = HashUtils.hash(data1)
        val hash2 = HashUtils.hash(data2)
        assertNotEquals(hash1, hash2, "Different inputs should produce different hashes")
    }

    @Test
    fun testHashLength() {
        val data = "any data".encodeToByteArray()
        val result = HashUtils.hash(data)
        assertEquals(8, result.length, "Hash should always be 8 characters (32-bit hex)")
    }

    @Test
    fun testHashIsHexadecimal() {
        val data = "test".encodeToByteArray()
        val result = HashUtils.hash(data)
        val hexPattern = Regex("^[0-9a-f]+$")
        assert(hexPattern.matches(result)) { "Hash should be a valid hexadecimal string" }
    }

    @Test
    fun testLargeData() {
        val largeData = ByteArray(1000) { it.toByte() }
        val result = HashUtils.hash(largeData)
        assertEquals(8, result.length, "Hash of large data should still be 8 characters")
    }
}