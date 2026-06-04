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
        assertFalse(content.contains("ComponentReplaceMe"))
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

    @Test
    fun `generateApp substitutes ComponentReplaceMe with initialComponent`() {
        generateApp(repoRoot, "myapp", "MyApp", initialComponent = "Widget")

        val file = repoRoot.resolve("myapp/src/main.kt")
        val content = file.readText()
        assertTrue(content.contains("Widget"))
        assertTrue(content.contains("widget"))
        assertFalse(content.contains("ComponentReplaceMe"))
        assertFalse(content.contains("componentreplaceme"))
    }

    @Test
    fun `generateApp delegates initial component to sub-generators`() {
        generateApp(repoRoot, "myapp", "MyApp", initialComponent = "Widget")

        // Each delegated generator places its output at the standard package path
        val controllerPath =
            "myapp/back-end/src/main/kotlin/com/cramsan/myapp/server/controller/WidgetController.kt"
        val servicePath =
            "myapp/back-end/src/main/kotlin/com/cramsan/myapp/server/service/WidgetService.kt"
        val datastorePath =
            "myapp/back-end/src/main/kotlin/com/cramsan/myapp/server/datastore/WidgetDatastore.kt"
        val frontendServicePath =
            "myapp/front-end/shared-app/src/commonMain/kotlin/com/cramsan/myapp/client/lib/service/WidgetService.kt"
        val managerPath =
            "myapp/front-end/shared-app/src/commonMain/kotlin/com/cramsan/myapp/client/lib/managers/WidgetManager.kt"
        val apiPath =
            "myapp/api/src/commonMain/kotlin/com/cramsan/myapp/api/WidgetApi.kt"

        listOf(controllerPath, servicePath, datastorePath, frontendServicePath, managerPath, apiPath).forEach {
            assertTrue(repoRoot.resolve(it).toFile().exists(), "Missing delegated file: $it")
        }
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

    // region helpers

    private fun createStub(path: Path, content: String) {
        path.parent.createDirectories()
        path.toFile().writeText(content)
    }

    private fun createTemplateStubs() {
        createAppSkeletonStubs()
        createBackendGeneratorStubs()
        createFrontendGeneratorStubs()
        createGradleStubs()
    }

    /**
     * Skeleton stubs in devtools/templates/app/ — app-level boilerplate that is copied
     * directly (no delegation to component generators).
     */
    private fun createAppSkeletonStubs() {
        // A source file with all placeholder variants — for content substitution testing
        createStub(
            repoRoot.resolve("devtools/templates/app/src/main.kt"),
            "// TEMPLATEREPLACEME\n// template-replace-me\n// template_replace_me\n" +
                "package com.cramsan.templatereplaceme\n" +
                "class TemplateReplaceMe\n" +
                "val componentreplaceme = ComponentReplaceMe()\n" +
                "fun FeatureReplacemeScreen() {}\n" +
                "val pkg = \"featurereplaceme\"\n" +
                "fun NavGraphBuilder.activityreplacemeNavGraphNavigation() {}\n" +
                "val mainNavGraph = \"ActivityReplacemeNavGraph\"\n",
        )

        // A file whose name contains the app placeholder — for filename rename testing
        createStub(
            repoRoot.resolve("devtools/templates/app/src/TemplateReplaceMe.kt"),
            "class TemplateReplaceMe",
        )

        // Feature template files in the featurereplaceme directory — for directory/file rename testing
        createStub(
            repoRoot.resolve("devtools/templates/app/features/main/featurereplaceme/FeatureReplacemeScreen.kt"),
            "fun FeatureReplacemeScreen() {}",
        )
    }

    private fun createBackendGeneratorStubs() {
        createStub(
            repoRoot.resolve("devtools/templates/api/ComponentReplaceMeApi.kt"),
            "package com.cramsan.templatereplaceme.api\nobject ComponentReplaceMeApi",
        )
        createStub(
            repoRoot.resolve("devtools/templates/api/ComponentReplaceMeNetworkResponse.kt"),
            "package com.cramsan.templatereplaceme.lib.model.network\ndata class ComponentReplaceMeNetworkResponse(val id: String)",
        )
        createStub(
            repoRoot.resolve("devtools/templates/api/CreateComponentReplaceMeNetworkRequest.kt"),
            "package com.cramsan.templatereplaceme.lib.model.network\ndata class CreateComponentReplaceMeNetworkRequest(val id: String)",
        )
        createStub(
            repoRoot.resolve("devtools/templates/backend/controller/ComponentReplaceMeController.kt"),
            "package com.cramsan.templatereplaceme.server.controller\nclass ComponentReplaceMeController",
        )
        createStub(
            repoRoot.resolve("devtools/templates/backend/controller/ComponentReplaceMeControllerTest.kt"),
            "package com.cramsan.templatereplaceme.server.controller\nclass ComponentReplaceMeControllerTest",
        )
        createStub(
            repoRoot.resolve("devtools/templates/backend/controller/requests/componentreplaceme_request.json"),
            """{"id": "test-id"}""",
        )
        createStub(
            repoRoot.resolve("devtools/templates/backend/controller/requests/componentreplaceme_response.json"),
            """{"id": "test-id"}""",
        )
        createStub(
            repoRoot.resolve("devtools/templates/backend/service/ComponentReplaceMeService.kt"),
            "package com.cramsan.templatereplaceme.server.service\nclass ComponentReplaceMeService",
        )
        createStub(
            repoRoot.resolve("devtools/templates/backend/service/ComponentReplaceMeServiceTest.kt"),
            "package com.cramsan.templatereplaceme.server.service\nclass ComponentReplaceMeServiceTest",
        )
        createStub(
            repoRoot.resolve("devtools/templates/backend/datastore/ComponentReplaceMeDatastore.kt"),
            "package com.cramsan.templatereplaceme.server.datastore\ninterface ComponentReplaceMeDatastore",
        )
        createStub(
            repoRoot.resolve("devtools/templates/backend/datastore/impl/ExampleComponentReplaceMeDatastore.kt"),
            "package com.cramsan.templatereplaceme.server.datastore.impl\nclass ExampleComponentReplaceMeDatastore",
        )
        createStub(
            repoRoot.resolve("devtools/templates/backend/datastore/impl/ComponentReplaceMeDatastoreImplTest.kt"),
            "package com.cramsan.templatereplaceme.server.datastore.impl\nclass ComponentReplaceMeDatastoreImplTest",
        )
    }

    private fun createFrontendGeneratorStubs() {
        createStub(
            repoRoot.resolve("devtools/templates/frontend/service/ComponentReplaceMeService.kt"),
            "package com.cramsan.templatereplaceme.client.lib.service\ninterface ComponentReplaceMeService",
        )
        createStub(
            repoRoot.resolve("devtools/templates/frontend/service/impl/ComponentReplaceMeServiceImpl.kt"),
            "package com.cramsan.templatereplaceme.client.lib.service.impl\nclass ComponentReplaceMeServiceImpl",
        )
        createStub(
            repoRoot.resolve("devtools/templates/frontend/manager/ComponentReplaceMeManager.kt"),
            "package com.cramsan.templatereplaceme.client.lib.managers\nclass ComponentReplaceMeManager",
        )
    }

    private fun createGradleStubs() {
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

    // endregion
}
