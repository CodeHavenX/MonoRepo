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

class GeneratorsTest {
    private lateinit var repoRoot: Path

    @BeforeEach
    fun setUp() {
        repoRoot = createTempDirectory("devtools-generators-test")
        createComponentStubs()
        createFeatureTemplateStubs()
        createActivityTemplateStubs()
    }

    @AfterEach
    fun tearDown() {
        repoRoot.toFile().deleteRecursively()
    }

    // region generateApi

    @Test
    fun `generateController fails when output files already exist`() {
        generateController(repoRoot, "Employee", "edifikana")

        val result = runCatching { generateController(repoRoot, "Employee", "edifikana") }
        assertTrue(result.isFailure)
        val message = result.exceptionOrNull()?.message.orEmpty()
        assertTrue(message.contains("already exist"), "Error should mention already exist: $message")
    }

    @Test
    fun `generateApi fails when app directory does not exist`() {
        val result = runCatching { generateApi(repoRoot, "Property", "nonexistentapp") }
        assertTrue(result.isFailure)
        val message = result.exceptionOrNull()?.message.orEmpty()
        assertTrue(message.contains("nonexistentapp"), "Error should mention the missing app: $message")
    }

    @Test
    fun `generateApi creates all three files`() {
        val result = generateApi(repoRoot, "Property", "edifikana")

        assertTrue(result.createdFiles.size == 3)
        result.createdFiles.forEach { assertTrue(Path.of(it).toFile().exists(), "Missing: $it") }
    }

    @Test
    fun `generateApi substitutes app and name placeholders`() {
        generateApi(repoRoot, "Property", "edifikana")

        val apiFile = repoRoot.resolve("edifikana/api/src/commonMain/kotlin/com/cramsan/edifikana/api/PropertyApi.kt")
        val content = apiFile.readText()
        assertTrue(content.contains("edifikana"))
        assertTrue(content.contains("Property"))
        assertFalse(content.contains("templatereplaceme"))
        assertFalse(content.contains("ComponentReplaceMe"))
    }

    // endregion

    // region generateController

    @Test
    fun `generateController creates source and test files`() {
        val result = generateController(repoRoot, "Employee", "edifikana")

        assertTrue(result.createdFiles.size == 4)
        result.createdFiles.forEach { assertTrue(Path.of(it).toFile().exists(), "Missing: $it") }
    }

    @Test
    fun `generateController substitutes placeholders in both files`() {
        generateController(repoRoot, "Employee", "edifikana")

        val src =
            repoRoot.resolve(
                "edifikana/back-end/src/main/kotlin/com/cramsan/edifikana/server/controller/EmployeeController.kt",
            )
        val test =
            repoRoot.resolve(
                "edifikana/back-end/src/test/kotlin/com/cramsan/edifikana/server/controller/EmployeeControllerTest.kt",
            )

        listOf(src, test).forEach { file ->
            val content = file.readText()
            assertTrue(content.contains("edifikana"), "Missing 'edifikana' in ${file.fileName}")
            assertTrue(content.contains("Employee"), "Missing 'Employee' in ${file.fileName}")
            assertFalse(content.contains("templatereplaceme"), "Still has 'templatereplaceme' in ${file.fileName}")
        }
    }

    // endregion

    // region generateService

    @Test
    fun `generateService creates source and test files`() {
        val result = generateService(repoRoot, "Property", "edifikana")

        assertTrue(result.createdFiles.size == 2)
        result.createdFiles.forEach { assertTrue(Path.of(it).toFile().exists(), "Missing: $it") }
    }

    @Test
    fun `generateService substitutes placeholders correctly`() {
        generateService(repoRoot, "Property", "edifikana")

        val src =
            repoRoot.resolve(
                "edifikana/back-end/src/main/kotlin/com/cramsan/edifikana/server/service/PropertyService.kt",
            )
        assertTrue(src.readText().contains("PropertyService"))
        assertFalse(src.readText().contains("ComponentReplaceMeService"))
    }

    // endregion

    // region generateDatastore

    @Test
    fun `generateDatastore creates interface, implementation, and test files`() {
        val result = generateDatastore(repoRoot, "Property", "edifikana", "Supabase")

        assertTrue(result.createdFiles.size == 3)
        result.createdFiles.forEach { assertTrue(Path.of(it).toFile().exists(), "Missing: $it") }
    }

    @Test
    fun `generateDatastore applies provider substitution to impl and test`() {
        generateDatastore(repoRoot, "Property", "edifikana", "Supabase")

        val impl =
            repoRoot.resolve(
                "edifikana/back-end/src/main/kotlin/com/cramsan/edifikana/server/datastore/impl/SupabasePropertyDatastore.kt",
            )
        val test =
            repoRoot.resolve(
                "edifikana/back-end/src/test/kotlin/com/cramsan/edifikana/server/datastore/impl/PropertyDatastoreImplTest.kt",
            )

        assertTrue(impl.readText().contains("Supabase"))
        assertTrue(test.toFile().exists(), "Test file not found: $test")
        assertFalse(impl.readText().contains("Example"))
    }

    @Test
    fun `generateDatastore interface does not contain provider name`() {
        generateDatastore(repoRoot, "Property", "edifikana", "Supabase")

        val iface =
            repoRoot.resolve(
                "edifikana/back-end/src/main/kotlin/com/cramsan/edifikana/server/datastore/PropertyDatastore.kt",
            )
        assertFalse(iface.readText().contains("Supabase"))
    }

    // endregion

    // region generateManager

    @Test
    fun `generateManager creates single file`() {
        val result = generateManager(repoRoot, "Property", "edifikana")

        assertTrue(result.createdFiles.size == 1)
        assertTrue(Path.of(result.createdFiles[0]).toFile().exists())
    }

    @Test
    fun `generateManager substitutes placeholders`() {
        generateManager(repoRoot, "Property", "edifikana")

        val dest =
            repoRoot.resolve(
                "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/managers/PropertyManager.kt",
            )
        assertTrue(dest.readText().contains("PropertyManager"))
        assertFalse(dest.readText().contains("ComponentReplaceMeManager"))
    }

    // endregion

    // region generateFrontendService

    @Test
    fun `generateFrontendService creates interface and implementation`() {
        val result = generateFrontendService(repoRoot, "Auth", "edifikana")

        assertTrue(result.createdFiles.size == 2)
        result.createdFiles.forEach { assertTrue(Path.of(it).toFile().exists(), "Missing: $it") }
    }

    @Test
    fun `generateFrontendService substitutes placeholders in both files`() {
        generateFrontendService(repoRoot, "Auth", "edifikana")

        val iface =
            repoRoot.resolve(
                "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/service/AuthService.kt",
            )
        val impl =
            repoRoot.resolve(
                "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/service/impl/AuthServiceImpl.kt",
            )

        assertTrue(iface.readText().contains("AuthService"))
        assertTrue(impl.readText().contains("AuthServiceImpl"))
        assertFalse(iface.readText().contains("ComponentReplaceMeService"))
        assertFalse(impl.readText().contains("ComponentReplaceMeServiceImpl"))
    }

    // endregion

    // region generateFeature

    private val featureParent =
        "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features/auth"

    @Test
    fun `generateFeature fails when output files already exist`() {
        generateFeature(repoRoot, "AddProperty", featureParent)

        val result = runCatching { generateFeature(repoRoot, "AddProperty", featureParent) }
        assertTrue(result.isFailure)
        val message = result.exceptionOrNull()?.message.orEmpty()
        assertTrue(message.contains("already exist"), "Error should mention already exist: $message")
    }

    @Test
    fun `generateFeature fails when parent path ends at the features root`() {
        val featuresRoot =
            "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features"
        val result = runCatching { generateFeature(repoRoot, "Login", featuresRoot) }
        assertTrue(result.isFailure)
        val message = result.exceptionOrNull()?.message.orEmpty()
        assertTrue(message.contains("features"), "Error should mention features directory: $message")
    }

    @Test
    fun `generateFeature creates six files`() {
        val result = generateFeature(repoRoot, "AddProperty", featureParent)

        assertTrue(result.createdFiles.size == 6)
        result.createdFiles.forEach { assertTrue(Path.of(it).toFile().exists(), "Missing: $it") }
    }

    @Test
    fun `generateFeature substitutes all placeholders including activity package`() {
        generateFeature(repoRoot, "AddProperty", featureParent)

        val screen =
            repoRoot.resolve(
                "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features/auth/addproperty/AddPropertyScreen.kt",
            )
        val content = screen.readText()
        assertTrue(content.contains("AddProperty"))
        assertTrue(content.contains("addproperty"))
        assertTrue(content.contains("com.cramsan.edifikana"))
        assertFalse(content.contains("FeatureReplaceme"))
        assertFalse(content.contains("featurereplaceme"))
        assertFalse(content.contains("templatereplaceme"))
        assertFalse(content.contains("activityreplaceme"))
        assertFalse(content.contains("ActivityReplaceme"))
    }

    @Test
    fun `generateFeature creates ScreenPreview file`() {
        generateFeature(repoRoot, "AddProperty", featureParent)

        val preview =
            repoRoot.resolve(
                "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features/auth/addproperty/AddPropertyScreenPreview.kt",
            )
        assertTrue(preview.toFile().exists())
    }

    @Test
    fun `generateFeature places ViewModelTest in jvmTest source set`() {
        generateFeature(repoRoot, "AddProperty", featureParent)

        val vmTest =
            repoRoot.resolve(
                "edifikana/front-end/shared-app/src/jvmTest/kotlin/com/cramsan/edifikana/client/lib/features/auth/addproperty/AddPropertyViewModelTest.kt",
            )
        assertTrue(vmTest.toFile().exists())
    }

    // endregion

    // region generateActivity

    @Test
    fun `generateActivity creates two files`() {
        val result =
            generateActivity(
                repoRoot,
                "Auth",
                "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features",
            )

        assertTrue(result.createdFiles.size == 2)
        result.createdFiles.forEach { assertTrue(Path.of(it).toFile().exists(), "Missing: $it") }
    }

    @Test
    fun `generateActivity creates ActivityScreen and Destination files`() {
        generateActivity(
            repoRoot,
            "Auth",
            "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features",
        )

        val activityScreen =
            repoRoot.resolve(
                "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features/auth/AuthActivityScreen.kt",
            )
        val destination =
            repoRoot.resolve(
                "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features/auth/AuthDestination.kt",
            )
        assertTrue(activityScreen.toFile().exists())
        assertTrue(destination.toFile().exists())
    }

    @Test
    fun `generateActivity substitutes ActivityReplaceme and package placeholders`() {
        generateActivity(
            repoRoot,
            "Auth",
            "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features",
        )

        val activityScreen =
            repoRoot.resolve(
                "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features/auth/AuthActivityScreen.kt",
            )
        val content = activityScreen.readText()
        assertTrue(content.contains("Auth"))
        assertTrue(content.contains("auth"))
        assertTrue(content.contains("com.cramsan.edifikana"))
        assertFalse(content.contains("templatereplaceme"))
        assertFalse(content.contains("ActivityReplacemeActivityScreen"))
        assertFalse(content.contains("activityreplacemeNavGraph"))
        assertTrue(content.contains("authNavGraphNavigation"))
        assertFalse(content.contains("FeatureReplacemeScreen"))
        assertFalse(content.contains("FeatureReplacemeDestination"))
        assertFalse(content.contains("TemplateReplaceMeWindowNavGraphDestination"))
        assertTrue(content.contains("EdifikanáWindowNavGraphDestination"))
        assertTrue(content.contains("AuthNavGraphDestination"))
    }

    @Test
    fun `generateActivity destination file contains AuthDestination and TODO comment`() {
        generateActivity(
            repoRoot,
            "Auth",
            "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features",
        )

        val destination =
            repoRoot.resolve(
                "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features/auth/AuthDestination.kt",
            )
        val content = destination.readText()
        assertTrue(content.contains("AuthDestination"))
        assertFalse(content.contains("ActivityReplacemeDestination"))
        assertFalse(content.contains("templatereplaceme"))
        assertTrue(content.contains("TODO"))
        assertTrue(content.contains("AuthNavGraphDestination"))
        assertTrue(content.contains("EdifikanáWindowNavGraphDestination"))
        assertFalse(content.contains("FeatureReplacemeDestination"))
        assertTrue(content.contains("PlaceholderDestination"))
    }

    @Test
    fun `generateActivity replaces FeatureReplacemeDestination in activity screen startDestination`() {
        generateActivity(
            repoRoot,
            "Auth",
            "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features",
        )

        val activityScreen =
            repoRoot.resolve(
                "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features/auth/AuthActivityScreen.kt",
            )
        val content = activityScreen.readText()
        assertFalse(content.contains("FeatureReplacemeDestination"))
        assertTrue(content.contains("PlaceholderDestination"))
    }

    @Test
    fun `generateActivity produces lowerCamelCase function name for multi-word activity`() {
        generateActivity(
            repoRoot,
            "UserProfile",
            "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features",
        )

        val activityScreen =
            repoRoot.resolve(
                "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features/userprofile/UserProfileActivityScreen.kt",
            )
        val content = activityScreen.readText()
        assertTrue(content.contains("userProfileNavGraphNavigation"))
        assertFalse(content.contains("userprofileNavGraphNavigation"))
    }

    @Test
    fun `generateActivity fails when output files already exist`() {
        val parent =
            "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features"
        generateActivity(repoRoot, "Auth", parent)

        val result = runCatching { generateActivity(repoRoot, "Auth", parent) }
        assertTrue(result.isFailure)
        val message = result.exceptionOrNull()?.message.orEmpty()
        assertTrue(message.contains("already exist"), "Error should mention already exist: $message")
    }

    // endregion

    // region helpers

    private fun createStub(path: Path, content: String) {
        path.parent.createDirectories()
        path.toFile().writeText(content)
    }

    private fun createComponentStubs() {
        // The app directory must exist before component generators can target it.
        repoRoot.resolve("edifikana").createDirectories()
        createBackEndStubs()
        createFrontEndComponentStubs()
    }

    private fun createBackEndStubs() {
        createStub(
            repoRoot.resolve("devtools/templates/api/ComponentReplaceMeApi.kt"),
            "package com.cramsan.templatereplaceme.api\nobject ComponentReplaceMeApi",
        )
        createStub(
            repoRoot.resolve("devtools/templates/api/CreateComponentReplaceMeNetworkRequest.kt"),
            "package com.cramsan.templatereplaceme.lib.model.network\ndata class CreateComponentReplaceMeNetworkRequest(val templatereplaceme: String)",
        )
        createStub(
            repoRoot.resolve("devtools/templates/api/ComponentReplaceMeNetworkResponse.kt"),
            "package com.cramsan.templatereplaceme.lib.model.network\ndata class ComponentReplaceMeNetworkResponse(val id: String)",
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
            "package com.cramsan.templatereplaceme.server.datastore.impl\nclass ExampleComponentReplaceMeDatastore : ComponentReplaceMeDatastore",
        )
        createStub(
            repoRoot.resolve("devtools/templates/backend/datastore/impl/ComponentReplaceMeDatastoreImplTest.kt"),
            "package com.cramsan.templatereplaceme.server.datastore.impl\nclass ComponentReplaceMeDatastoreImplTest { val d = ExampleComponentReplaceMeDatastore() }",
        )
    }

    private fun createFrontEndComponentStubs() {
        createStub(
            repoRoot.resolve("devtools/templates/frontend/manager/ComponentReplaceMeManager.kt"),
            "package com.cramsan.templatereplaceme.client.lib.managers\nclass ComponentReplaceMeManager",
        )
        createStub(
            repoRoot.resolve("devtools/templates/frontend/service/ComponentReplaceMeService.kt"),
            "package com.cramsan.templatereplaceme.client.lib.service\ninterface ComponentReplaceMeService",
        )
        createStub(
            repoRoot.resolve("devtools/templates/frontend/service/impl/ComponentReplaceMeServiceImpl.kt"),
            "package com.cramsan.templatereplaceme.client.lib.service.impl\nclass ComponentReplaceMeServiceImpl : ComponentReplaceMeService",
        )
    }

    private fun createFeatureTemplateStubs() {
        val featurePkg = "com.cramsan.templatereplaceme.client.lib.features.activityreplaceme.featurereplaceme"
        val base = "devtools/templates/frontend/feature"
        createStub(
            repoRoot.resolve("$base/FeatureReplacemeScreen.kt"),
            "package $featurePkg\nfun FeatureReplacemeScreen() {}",
        )
        createStub(
            repoRoot.resolve("$base/FeatureReplacemeEvent.kt"),
            "package $featurePkg\nsealed class FeatureReplacemeEvent",
        )
        createStub(
            repoRoot.resolve("$base/FeatureReplacemeUIState.kt"),
            "package $featurePkg\ndata class FeatureReplacemeUIState(val isLoading: Boolean)",
        )
        createStub(
            repoRoot.resolve("$base/FeatureReplacemeViewModel.kt"),
            "package $featurePkg\nclass FeatureReplacemeViewModel",
        )
        createStub(
            repoRoot.resolve("$base/FeatureReplacemeScreenPreview.kt"),
            "package $featurePkg\nfun FeatureReplacemeScreenPreview() {}",
        )
        createStub(
            repoRoot.resolve("$base/FeatureReplacemeViewModelTest.kt"),
            "package $featurePkg\nclass FeatureReplacemeViewModelTest",
        )
    }

    private fun createActivityTemplateStubs() {
        val activityPkg = "com.cramsan.templatereplaceme.client.lib.features.activityreplaceme"
        val base = "devtools/templates/frontend/activity"
        createStub(
            repoRoot.resolve("$base/ActivityReplacemeActivityScreen.kt"),
            """
            package $activityPkg
            import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowNavGraphDestination
            fun NavGraphBuilder.activityReplacemeNavGraphNavigation() {
                navigationGraph(
                    graphDestination = TemplateReplaceMeWindowNavGraphDestination.ActivityReplacemeNavGraphDestination::class,
                    startDestination = ActivityReplacemeDestination.FeatureReplacemeDestination,
                ) {
                    // TODO: add composable entries for each ActivityReplacemeDestination screen
                }
            }
            """.trimIndent(),
        )
        createStub(
            repoRoot.resolve("$base/ActivityReplacemeDestination.kt"),
            // Split long TODO comment across two lines to stay within max line length.
            "package $activityPkg\n" +
                "// TODO: Add ActivityReplacemeNavGraphDestination to\n" +
                "// TemplateReplaceMeWindowNavGraphDestination sealed class.\n" +
                "sealed class ActivityReplacemeDestination {\n" +
                "    data object FeatureReplacemeDestination : ActivityReplacemeDestination()\n" +
                "}",
        )
        createStub(
            repoRoot.resolve(
                "edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features/window/EdifikanáWindowNavGraphDestination.kt",
            ),
            "package com.cramsan.edifikana.client.lib.features.window\nsealed class EdifikanáWindowNavGraphDestination {\n}",
        )
    }

    // endregion
}
