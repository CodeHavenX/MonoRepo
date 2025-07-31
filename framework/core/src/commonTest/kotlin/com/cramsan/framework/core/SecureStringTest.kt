package com.cramsan.framework.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@OptIn(SecureStringAccess::class)
class SecureStringTest {

    @Test
    fun `default constructor creates empty SecureString`() {
        val secure = SecureString()
        assertEquals("", secure.reveal())
    }

    @Test
    fun `constructor stores and reveals content`() {
        val secure = SecureString("superSecret123")
        assertEquals("superSecret123", secure.reveal())
    }

    @Test
    fun `toString does not reveal content`() {
        val secure = SecureString("doNotLeakMe")
        assertEquals("SecureString(content=****)", secure.toString())
        assertNotEquals("doNotLeakMe", secure.toString())
    }

    @Test
    fun `reveal returns correct value for different inputs`() {
        val secure1 = SecureString("abc")
        val secure2 = SecureString("xyz")
        assertEquals("abc", secure1.reveal())
        assertEquals("xyz", secure2.reveal())
    }
}