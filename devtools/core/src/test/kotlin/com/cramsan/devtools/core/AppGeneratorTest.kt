package com.cramsan.devtools.core

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.readText
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppGeneratorTest {
    private lateinit var repoRoot: Path

    @BeforeEach
    fun setUp() {
        repoRoot = createTempDirectory("devtools-appgen-test")
        createTemplateStubs()
    }

    @AfterEach
    fun tearDown() {
        repoRoot.toFile().deleteRecursively()
    }

    @Test
    fun `generateApp creates app directory`() {
        generateApp(repoRoot, "myapp", "MyApp")

        assertTrue(repoRoot.resolve("myapp").toFile().exists())
    }

    @Test
    fun `generateApp fails if destination already exists`() {
        repoRoot.resolve("myapp").toFile().mkdirs()

        val threw = runCatching { generateApp(repoRoot, "myapp", "MyApp") }.isFailure
        assertTrue(threw)
    }

    @Test
    fun `generateApp substitutes all content placeholders`() {
        generateApp(repoRoot, "myapp", "MyApp")

        val file = repoRoot.resolve("myapp/src/main.kt")
        val content = file.readText()
        assertTrue(content.contains("MyApp"))
        assertTrue(content.contains("myapp"))
        assertFalse(content.contains("templatereplaceme"))
        assertFalse(content.contains("TemplateReplaceMe"))
    }

    @Test
    fun `generateApp renames files containing templatereplaceme`() {
        generateApp(repoRoot, "myapp", "MyApp")

        val renamed = repoRoot.resolve("myapp/src/MyApp.kt")
        val original = repoRoot.resolve("myapp/src/TemplateReplaceMe.kt")
        assertTrue(renamed.toFile().exists())
        assertFalse(original.toFile().exists())
    }

    @Test
    fun `generateApp appends includes to settings gradle`() {
        generateApp(repoRoot, "myapp", "MyApp")

        val content = repoRoot.resolve("settings.gradle.kts").readText()
        assertTrue(content.contains("include(\"myapp:api\")"))
        assertTrue(content.contains("include(\"myapp:back-end\")"))
        assertTrue(content.contains("include(\"myapp:front-end:shared-app\")"))
    }

    @Test
    fun `generateApp inserts dependsOn entries into releaseAll`() {
        generateApp(repoRoot, "myapp", "MyApp")

        val content = repoRoot.resolve("build.gradle.kts").readText()
        assertTrue(content.contains("dependsOn(\"myapp:api:release\")"))
        assertTrue(content.contains("dependsOn(\"myapp:back-end:release\")"))
        // marker must still be present
        assertTrue(content.contains("dependsOn(\"generateBuildArtifacts\")"))
    }

    @Test
    fun `generateApp includes all platforms by default`() {
        generateApp(repoRoot, "myapp", "MyApp")

        val content = repoRoot.resolve("settings.gradle.kts").readText()
        assertTrue(content.contains("app-wasm"))
        assertTrue(content.contains("app-android"))
        assertTrue(content.contains("app-jvm"))
    }

    @Test
    fun `generateApp excludes wasm when no-wasm is set`() {
        generateApp(repoRoot, "myapp", "MyApp", includeWasm = false)

        val content = repoRoot.resolve("settings.gradle.kts").readText()
        assertFalse(content.contains("myapp:front-end:app-wasm"))
    }

    @Test
    fun `generateApp excludes android when no-android is set`() {
        generateApp(repoRoot, "myapp", "MyApp", includeAndroid = false)

        val content = repoRoot.resolve("settings.gradle.kts").readText()
        assertFalse(content.contains("myapp:front-end:app-android"))
    }

    @Test
    fun `generateApp excludes jvm when no-jvm is set`() {
        generateApp(repoRoot, "myapp", "MyApp", includeJvm = false)

        val content = repoRoot.resolve("settings.gradle.kts").readText()
        assertFalse(content.contains("myapp:front-end:app-jvm"))
    }

    @Test
    fun `generateApp returns app directory in createdFiles`() {
        val result = generateApp(repoRoot, "myapp", "MyApp")

        assertTrue(result.createdFiles.size == 1)
        assertTrue(result.createdFiles[0].endsWith("myapp"))
    }

    @Test
    fun `generateApp returns non-empty checklist`() {
        val result = generateApp(repoRoot, "myapp", "MyApp")

        assertTrue(result.postGenerationChecklist.isNotEmpty())
    }

    private fun createStub(path: Path, content: String) {
        path.parent.createDirectories()
        path.toFile().writeText(content)
    }

    private fun createTemplateStubs() {
        // A source file with all placeholder variants
        createStub(
            repoRoot.resolve("templatereplaceme/src/main.kt"),
            "// TEMPLATEREPLACEME\n// template-replace-me\n// template_replace_me\npackage com.cramsan.templatereplaceme\nclass TemplateReplaceMe",
        )

        // A file whose name contains the placeholder — for rename testing
        createStub(
            repoRoot.resolve("templatereplaceme/src/TemplateReplaceMe.kt"),
            "class TemplateReplaceMe",
        )

        // Minimal settings.gradle.kts with the marker the generator looks for
        createStub(
            repoRoot.resolve("settings.gradle.kts"),
            "// root settings\n",
        )

        // Minimal build.gradle.kts with the releaseAll marker
        createStub(
            repoRoot.resolve("build.gradle.kts"),
            "tasks.register(\"releaseAll\") {\n    dependsOn(\"generateBuildArtifacts\")\n}\n",
        )
    }
}
