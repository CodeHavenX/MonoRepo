package com.cramsan.framework.configuration

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
    fun `missing config file does not throw`(@TempDir tempDir: Path) {
        val missingPath = tempDir.resolve("does_not_exist.properties").toString()
        SimpleConfiguration(missingPath)
    }

    @Test
    fun `missing config file returns null for all key types`(@TempDir tempDir: Path) {
        val missingPath = tempDir.resolve("does_not_exist.properties").toString()
        val cfg = SimpleConfiguration(missingPath)

        assertNull(cfg.readString("any"))
        assertNull(cfg.readInt("any"))
        assertNull(cfg.readLong("any"))
        assertNull(cfg.readBoolean("any"))
    }

    @Test
    fun `missing config file is created on disk`(@TempDir tempDir: Path) {
        val missingPath = tempDir.resolve("does_not_exist.properties").toString()
        SimpleConfiguration(missingPath)
        assertTrue(File(missingPath).exists())
    }

    @Test
    fun `missing config file inside nested path is created`(@TempDir tempDir: Path) {
        val missingPath = tempDir.resolve("a/b/c/config.properties").toString()
        SimpleConfiguration(missingPath)
        assertTrue(File(missingPath).exists())
    }

    @Test
    fun `transformKey normalizes keys`() {
        val file = File.createTempFile("cfg", ".properties")
        file.deleteOnExit()
        val cfg = SimpleConfiguration(file.absolutePath)

        val transformed = cfg.transformKey("My.Key-With\$Weird")
        assertEquals("my.key_with_weird", transformed)
    }
}
