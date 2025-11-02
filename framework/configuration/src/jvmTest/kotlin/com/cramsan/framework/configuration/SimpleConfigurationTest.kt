package com.cramsan.framework.configuration

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals

class SimpleConfigurationTest {

    @Test
    fun `reads values from file`(@TempDir tempDir: Path) {
        val file = tempDir.resolve("config.properties").toFile()
        file.writeText(
            """
            stringKey=hello
            intKey=42
            longKey=1234567890123
            booleanKey=true
            """.trimIndent()
        )

        val cfg = SimpleConfiguration(file.absolutePath)

        assertEquals("hello", cfg.readString("stringKey"))
        assertEquals(42, cfg.readInt("intKey"))
        assertEquals(1234567890123L, cfg.readLong("longKey"))
        assertEquals(true, cfg.readBoolean("booleanKey"))
    }

    @Test
    fun `transformKey normalizes keys`() {
        val file = File.createTempFile("cfg", ".properties")
        file.deleteOnExit()
        val cfg = SimpleConfiguration(file.absolutePath)

        val transformed = cfg.transformKey("My.Key-With\$Weird")
        // expected: lowercase letters, dash and dollar replaced with underscore
        assertEquals("my.key_with_weird", transformed)
    }
}
