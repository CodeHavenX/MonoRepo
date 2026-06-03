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
    fun `generateApp fails when app name contains a hyphen`() {
        val result = runCatching { generateApp(repoRoot, "my-app", "MyApp") }
        assertTrue(result.isFailure)
        val message = result.exceptionOrNull()?.message.orEmpty()
        assertTrue(message.contains("hyphen"), "Error message should mention 'hyphen': $message")
        assertTrue(
            message.contains("myapp") || message.contains("my_app"),
            "Error should suggest alternatives: $message",
        )
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
        assertFalse(content.contains("ComponentReplaceme"))
        assertFalse(content.contains("MainMenu"))
        assertFalse(content.contains("main.menu"))
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

    @Test
    fun `generateApp substitutes ComponentReplaceme with initialComponent`() {
        generateApp(repoRoot, "myapp", "MyApp", initialComponent = "Widget")

        val file = repoRoot.resolve("myapp/src/main.kt")
        val content = file.readText()
        assertTrue(content.contains("Widget"))
        assertTrue(content.contains("widget"))
        assertFalse(content.contains("ComponentReplaceme"))
        assertFalse(content.contains("componentreplaceme"))
    }

    @Test
    fun `generateApp renames ComponentReplaceme files`() {
        generateApp(repoRoot, "myapp", "MyApp", initialComponent = "Widget")

        val renamed = repoRoot.resolve("myapp/src/WidgetController.kt")
        val original = repoRoot.resolve("myapp/src/ComponentReplacemeController.kt")
        assertTrue(renamed.toFile().exists())
        assertFalse(original.toFile().exists())
    }

    @Test
    fun `generateApp substitutes FeatureReplaceme with Home`() {
        generateApp(repoRoot, "myapp", "MyApp")

        val file = repoRoot.resolve("myapp/src/main.kt")
        val content = file.readText()
        assertTrue(content.contains("HomeScreen"))
        assertTrue(content.contains("home"))
        assertFalse(content.contains("FeatureReplaceme"))
        assertFalse(content.contains("featurereplaceme"))
    }

    @Test
    fun `generateApp substitutes ActivityReplaceme with Main`() {
        generateApp(repoRoot, "myapp", "MyApp")

        val file = repoRoot.resolve("myapp/src/main.kt")
        val content = file.readText()
        assertTrue(content.contains("MainNavGraph"))
        assertFalse(content.contains("ActivityReplaceme"))
        assertFalse(content.contains("activityreplaceme"))
    }

    @Test
    fun `generateApp renames featurereplaceme directory to home`() {
        generateApp(repoRoot, "myapp", "MyApp")

        val renamedDir = repoRoot.resolve("myapp/features/main/home")
        val originalDir = repoRoot.resolve("myapp/features/main/featurereplaceme")
        assertTrue(renamedDir.toFile().isDirectory)
        assertFalse(originalDir.toFile().exists())
    }

    @Test
    fun `generateApp renames FeatureReplaceme files to Home`() {
        generateApp(repoRoot, "myapp", "MyApp")

        val renamed = repoRoot.resolve("myapp/features/main/home/HomeScreen.kt")
        val original = repoRoot.resolve("myapp/features/main/featurereplaceme/FeatureReplacemeScreen.kt")
        assertTrue(renamed.toFile().exists())
        assertFalse(original.toFile().exists())
    }

    private fun createTemplateStubs() {
        // A source file with all placeholder variants
        createStub(
            repoRoot.resolve("templatereplaceme/src/main.kt"),
            "// TEMPLATEREPLACEME\n// template-replace-me\n// template_replace_me\n" +
                "package com.cramsan.templatereplaceme\n" +
                "class TemplateReplaceMe\n" +
                "class ComponentReplacemeController\n" +
                "val componentreplaceme = ComponentReplaceme()\n" +
                "fun FeatureReplacemeScreen() {}\n" +
                "val pkg = \"featurereplaceme\"\n" +
                "fun NavGraphBuilder.activityreplacemeNavGraphNavigation() {}\n" +
                "val mainNavGraph = \"ActivityReplacemeNavGraph\"\n",
        )

        // A file whose name contains the placeholder — for rename testing
        createStub(
            repoRoot.resolve("templatereplaceme/src/TemplateReplaceMe.kt"),
            "class TemplateReplaceMe",
        )

        // A ComponentReplaceme file — for rename testing
        createStub(
            repoRoot.resolve("templatereplaceme/src/ComponentReplacemeController.kt"),
            "class ComponentReplacemeController",
        )

        // Feature template files in the featurereplaceme directory — for rename/restructure testing
        createStub(
            repoRoot.resolve("templatereplaceme/features/main/featurereplaceme/FeatureReplacemeScreen.kt"),
            "fun FeatureReplacemeScreen() {}",
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
