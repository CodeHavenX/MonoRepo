package com.cramsan.framework.configuration

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import kotlin.test.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows

class EnvironmentConfigurationTest {

    @Test
    fun `transformKey builds env var name`() {
        val cfg = EnvironmentConfiguration("MYAPP")
        val transformed = cfg.transformKey("some.Key-Value.123")
        // letters become uppercase, non-alnum become underscore, and prefix added
        assertEquals("MYAPP_SOME_KEY_VALUE_123", transformed)
    }

    @Test
    fun `constructor rejects invalid prefix`() {
        val ex = assertThrows(IllegalArgumentException::class.java) {
            EnvironmentConfiguration("myapp-lower")
        }
        assertEquals(ex.message?.contains("Domain prefix"), true)
    }

    @Test
    fun `readString returns null when env var missing`() {
        val cfg = EnvironmentConfiguration("NO_SUCH_PREFIX")
        // We shouldn't rely on environment being set in tests; ensure missing returns null
        assertNull(cfg.readString("someKeyThatDoesNotExist"))
    }
}

