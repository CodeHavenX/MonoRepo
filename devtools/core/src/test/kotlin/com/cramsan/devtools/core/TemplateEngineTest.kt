package com.cramsan.devtools.core

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.readText
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TemplateEngineTest {
    private lateinit var tempDir: Path

    @BeforeEach
    fun setUp() {
        tempDir = createTempDirectory("devtools-engine-test")
    }

    @AfterEach
    fun tearDown() {
        tempDir.toFile().deleteRecursively()
    }

    @Test
    fun `toPascal converts single word`() {
        assertEquals("Myapp", toPascal("myapp"))
    }

    @Test
    fun `toPascal converts dash-separated words`() {
        assertEquals("MyApp", toPascal("my-app"))
    }

    @Test
    fun `toPascal converts multiple segments`() {
        assertEquals("FooBarBaz", toPascal("foo-bar-baz"))
    }

    @Test
    fun `toPascal leaves already-pascal input unchanged`() {
        assertEquals("Edifikana", toPascal("edifikana"))
    }

    @Test
    fun `toLowerCamel lowercases first character`() {
        assertEquals("userName", toLowerCamel("UserName"))
    }

    @Test
    fun `toLowerCamel is no-op for already-lower string`() {
        assertEquals("user", toLowerCamel("user"))
    }

    @Test
    fun `applySubs replaces all four standard placeholders`() {
        val src = tempDir.resolve("template.kt")
        src.toFile().writeText(
            "package com.cramsan.templatereplaceme.api\nclass ComponentReplaceMeApi(val field: templatereplaceme) : TemplateReplaceMe { val x: componentreplaceme = ComponentReplaceMe() }",
        )

        val dst = tempDir.resolve("output.kt")
        applySubs(
            src,
            dst,
            listOf(
                "TemplateReplaceMe" to "Edifikana",
                "templatereplaceme" to "edifikana",
                "ComponentReplaceMe" to "Property",
                "componentreplaceme" to "property",
            ),
        )

        val content = dst.readText()
        assertTrue(content.contains("com.cramsan.edifikana.api"))
        assertTrue(content.contains("PropertyApi"))
        assertTrue(content.contains("edifikana"))
        assertTrue(content.contains("Edifikana"))
        assertTrue(content.contains("property"))
        assertTrue(content.contains("Property"))
        assertFalse(content.contains("templatereplaceme"))
        assertFalse(content.contains("TemplateReplaceMe"))
        assertFalse(content.contains("ComponentReplaceMeApi"))
        assertFalse(content.contains(": ComponentReplaceMe()"))
    }

    @Test
    fun `applySubs applies extra substitutions between app and name subs`() {
        val src = tempDir.resolve("template.kt")
        src.toFile().writeText("class ExampleComponentReplaceMeDatastore : ComponentReplaceMeDatastore")

        val dst = tempDir.resolve("output.kt")
        applySubs(
            src,
            dst,
            listOf(
                "TemplateReplaceMe" to "Edifikana",
                "templatereplaceme" to "edifikana",
                "Example" to "Supabase",
                "ComponentReplaceMe" to "Property",
                "componentreplaceme" to "property",
            ),
        )

        val content = dst.readText()
        assertTrue(content.contains("SupabasePropertyDatastore"))
        assertFalse(content.contains("Example"))
    }

    @Test
    fun `applySubsToContent replaces all occurrences`() {
        val result =
            applySubsToContent(
                "templatereplaceme.TemplateReplaceMe",
                listOf(
                    "TemplateReplaceMe" to "Edifikana",
                    "templatereplaceme" to "edifikana",
                ),
            )
        assertEquals("edifikana.Edifikana", result)
    }

    @Test
    fun `applySubsToContent applies substitutions sequentially so later subs see earlier output`() {
        val result =
            applySubsToContent(
                "ComponentReplaceMe",
                listOf(
                    "ComponentReplaceMe" to "Employee",
                    "Employee" to "Person",
                ),
            )
        assertEquals("Person", result)
    }

    @Test
    fun `applySubs creates intermediate directories`() {
        val src = tempDir.resolve("template.kt")
        src.toFile().writeText("content")

        val dst = tempDir.resolve("deep/nested/dir/output.kt")
        applySubs(
            src,
            dst,
            listOf(
                "TemplateReplaceMe" to "A",
                "templatereplaceme" to "a",
                "ComponentReplaceMe" to "B",
                "componentreplaceme" to "b",
            ),
        )

        assertTrue(dst.toFile().exists())
    }
}
