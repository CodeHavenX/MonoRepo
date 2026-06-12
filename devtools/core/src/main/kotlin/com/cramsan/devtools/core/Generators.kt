package com.cramsan.devtools.core

import java.nio.file.Path

/** Generates an API interface and its network request/response models. */
fun generateApi(
    repoRoot: Path,
    name: String,
    app: String,
): GenerationResult =
    generateFromTemplates(
        repoRoot,
        name,
        app,
        filePairs =
        listOf(
            "devtools/templates/api/ComponentReplaceMeApi.kt" to
                "$app/api/src/commonMain/kotlin/com/cramsan/$app/api/${name}Api.kt",
            "devtools/templates/api/CreateComponentReplaceMeNetworkRequest.kt" to
                "$app/shared/src/commonMain/kotlin/com/cramsan/$app/lib/model/network/Create${name}NetworkRequest.kt",
            "devtools/templates/api/ComponentReplaceMeNetworkResponse.kt" to
                "$app/shared/src/commonMain/kotlin/com/cramsan/$app/lib/model/network/${name}NetworkResponse.kt",
        ),
        checklist = listOf("[ ] Wire-up this Api with its respective Controller"),
    )

/** Generates a back-end Controller and its unit test. */
fun generateController(
    repoRoot: Path,
    name: String,
    app: String,
): GenerationResult =
    generateFromTemplates(
        repoRoot,
        name,
        app,
        filePairs =
        listOf(
            "devtools/templates/backend/controller/ComponentReplaceMeController.kt" to
                "$app/back-end/src/main/kotlin/com/cramsan/$app/server/controller/${name}Controller.kt",
            "devtools/templates/backend/controller/ComponentReplaceMeControllerTest.kt" to
                "$app/back-end/src/test/kotlin/com/cramsan/$app/server/controller/${name}ControllerTest.kt",
            "devtools/templates/backend/controller/requests/componentreplaceme_request.json" to
                "$app/back-end/src/test/resources/requests/${name.lowercase()}_request.json",
            "devtools/templates/backend/controller/requests/componentreplaceme_response.json" to
                "$app/back-end/src/test/resources/requests/${name.lowercase()}_response.json",
        ),
        checklist =
        listOf(
            "# Add to $app/back-end/src/main/kotlin/com/cramsan/$app/server/dependencyinjection/ControllerModule.kt",
            "singleOf(::${name}Controller) { bind<Controller>() }",
            "",
            "# Also verify the API route is registered in registerRoutes.",
        ),
    )

/** Generates a back-end Service and its unit test. */
fun generateService(
    repoRoot: Path,
    name: String,
    app: String,
): GenerationResult =
    generateFromTemplates(
        repoRoot,
        name,
        app,
        filePairs =
        listOf(
            "devtools/templates/backend/service/ComponentReplaceMeService.kt" to
                "$app/back-end/src/main/kotlin/com/cramsan/$app/server/service/${name}Service.kt",
            "devtools/templates/backend/service/ComponentReplaceMeServiceTest.kt" to
                "$app/back-end/src/test/kotlin/com/cramsan/$app/server/service/${name}ServiceTest.kt",
        ),
        checklist =
        listOf(
            "# Add to $app/back-end/src/main/kotlin/com/cramsan/$app/server/dependencyinjection/ServicesModule.kt",
            "singleOf(::${name}Service)",
        ),
    )

/** Generates a back-end Datastore interface, a provider implementation, and its unit test. */
fun generateDatastore(
    repoRoot: Path,
    name: String,
    app: String,
    provider: String,
): GenerationResult =
    generateFromTemplates(
        repoRoot,
        name,
        app,
        filePairs =
        listOf(
            "devtools/templates/backend/datastore/ComponentReplaceMeDatastore.kt" to
                "$app/back-end/src/main/kotlin/com/cramsan/$app/server/datastore/${name}Datastore.kt",
            "devtools/templates/backend/datastore/impl/ExampleComponentReplaceMeDatastore.kt" to
                "$app/back-end/src/main/kotlin/com/cramsan/$app/server/datastore/impl/${provider}${name}Datastore.kt",
            "devtools/templates/backend/datastore/impl/ComponentReplaceMeDatastoreImplTest.kt" to
                "$app/back-end/src/test/kotlin/com/cramsan/$app/server/datastore/impl/${name}DatastoreImplTest.kt",
        ),
        checklist =
        listOf(
            "# Add to $app/back-end/src/main/kotlin/com/cramsan/$app/server/dependencyinjection/DatastoreModule.kt",
            "singleOf(::${provider}${name}Datastore) { bind<${name}Datastore>() }",
        ),
        extraSubs = mapOf("Example" to provider),
    )

/** Generates a front-end Manager. */
fun generateManager(
    repoRoot: Path,
    name: String,
    app: String,
): GenerationResult =
    generateFromTemplates(
        repoRoot,
        name,
        app,
        filePairs =
        listOf(
            "devtools/templates/frontend/manager/ComponentReplaceMeManager.kt" to
                "$app/front-end/app/src/commonMain/kotlin/com/cramsan/$app/client/lib/managers/${name}Manager.kt",
        ),
        checklist =
        listOf(
            "# Add to $app/front-end/app/src/commonMain/kotlin/com/cramsan/$app/client/lib/di/ManagerModule.kt",
            "singleOf(::${name}Manager)",
            "",
            "# Note: No jvmTest template exists for managers. Write tests manually in:",
            "#   $app/front-end/app/src/jvmTest/kotlin/com/cramsan/$app/client/lib/managers/${name}ManagerTest.kt",
        ),
    )

/** Generates a front-end Service interface and its implementation. */
fun generateFrontendService(
    repoRoot: Path,
    name: String,
    app: String,
): GenerationResult =
    generateFromTemplates(
        repoRoot,
        name,
        app,
        filePairs =
        listOf(
            "devtools/templates/frontend/service/ComponentReplaceMeService.kt" to
                "$app/front-end/app/src/commonMain/kotlin/com/cramsan/$app/client/lib/service/${name}Service.kt",
            "devtools/templates/frontend/service/impl/ComponentReplaceMeServiceImpl.kt" to
                "$app/front-end/app/src/commonMain/kotlin/com/cramsan/$app/client/lib/service/impl/${name}ServiceImpl.kt",
        ),
        checklist =
        listOf(
            "# Add to $app/front-end/app/src/commonMain/kotlin/com/cramsan/$app/client/lib/di/ServiceModule.kt",
            "singleOf(::${name}ServiceImpl) { bind<${name}Service>() }",
            "",
            "# Note: No jvmTest template exists for frontend services. Write tests manually in:",
            "#   $app/front-end/app/src/jvmTest/kotlin/com/cramsan/$app/client/lib/service/impl/${name}ServiceImplTest.kt",
        ),
    )

private const val FEATURE_TMPL_DIR = "devtools/templates/frontend/feature"

private const val ACTIVITY_TMPL_COMMON = "devtools/templates/frontend/activity"

/**
 * Generates a full Compose feature: Screen, Event, UIState, ViewModel, ScreenPreview, and ViewModelTest.
 *
 * Template source: `devtools/templates/frontend/feature/`
 *
 * @param parentRel repo-relative path to the parent activity directory,
 *   e.g. `edifikana/front-end/app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features/auth`
 */
fun generateFeature(
    repoRoot: Path,
    featureName: String,
    parentRel: String,
): GenerationResult {
    val normalizedParent = toRepoRelative(repoRoot, parentRel)
    val app = normalizedParent.substringBefore("/")
    val appPascal = toPascal(app)
    val activityPackage = normalizedParent.substringAfterLast("/")
    require(activityPackage != "features") {
        "The --parent path '$parentRel' points to the features/ directory itself. " +
            "It must point one level deeper, to the specific activity directory " +
            "(e.g. .../features/auth, not .../features)."
    }
    val activityName = toPascal(activityPackage)
    val featurePackage = featureName.lowercase()

    val commonDir = repoRoot.resolve("$normalizedParent/$featurePackage")
    val testDir = repoRoot.resolve("${normalizedParent.replace("/commonMain/", "/jvmTest/")}/$featurePackage")
    val srcCommon = repoRoot.resolve(FEATURE_TMPL_DIR)

    val subs =
        listOf(
            "TemplateReplaceMe" to appPascal,
            "templatereplaceme" to app,
            "ActivityReplaceme" to activityName,
            "activityreplaceme" to activityPackage,
            "FeatureReplaceme" to featureName,
            "featurereplaceme" to featurePackage,
        )

    val fileMap =
        listOf(
            srcCommon.resolve("FeatureReplacemeScreen.kt") to commonDir.resolve("${featureName}Screen.kt"),
            srcCommon.resolve("FeatureReplacemeEvent.kt") to commonDir.resolve("${featureName}Event.kt"),
            srcCommon.resolve("FeatureReplacemeUIState.kt") to commonDir.resolve("${featureName}UIState.kt"),
            srcCommon.resolve("FeatureReplacemeViewModel.kt") to commonDir.resolve("${featureName}ViewModel.kt"),
            srcCommon.resolve(
                "FeatureReplacemeScreenPreview.kt",
            ) to commonDir.resolve("${featureName}ScreenPreview.kt"),
            srcCommon.resolve("FeatureReplacemeViewModelTest.kt") to testDir.resolve("${featureName}ViewModelTest.kt"),
        )

    val featureConflicts = fileMap.map { it.second }.filter { it.toFile().exists() }
    require(featureConflicts.isEmpty()) {
        "Output file(s) already exist. Delete them first or choose a different name:\n" +
            featureConflicts.joinToString("\n") { "  $it" }
    }
    fileMap.forEach { (src, dst) -> applySubs(src, dst, subs) }

    return GenerationResult(
        createdFiles = fileMap.map { it.second.toString() },
        postGenerationChecklist =
        listOf(
            "# Register the ViewModel in the appropriate DI module (ViewModelModule.kt or ViewModelPlatformModule):",
            "viewModelOf(::${featureName}ViewModel)",
            "",
            "# Register the destination as a route in the appropriate router.",
        ),
    )
}

/**
 * Generates a Compose activity: a NavGraphBuilder extension and a Destination sealed class.
 *
 * Template source: `devtools/templates/frontend/activity/`
 *
 * @param parentRel repo-relative path to the parent features directory,
 *   e.g. `edifikana/front-end/app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features`
 */
fun generateActivity(
    repoRoot: Path,
    activityName: String,
    parentRel: String,
): GenerationResult {
    val normalizedParent = toRepoRelative(repoRoot, parentRel)
    val app = normalizedParent.substringBefore("/")
    val appPascal = toPascal(app)
    val activityPackage = activityName.lowercase()

    val activityDir = repoRoot.resolve("$normalizedParent/$activityPackage")
    val srcCommon = repoRoot.resolve(ACTIVITY_TMPL_COMMON)

    val windowClass = findWindowNavGraphDestinationClass(repoRoot, app)

    val subs =
        listOf(
            "TemplateReplaceMeWindowNavGraphDestination" to windowClass,
            "TemplateReplaceMe" to appPascal,
            "templatereplaceme" to app,
            "ActivityReplaceme" to activityName,
            "activityReplaceme" to toLowerCamel(activityName),
            "activityreplaceme" to activityPackage,
            "FeatureReplaceme" to TEMPLATE_DEFAULT_FEATURE,
            "featurereplaceme" to TEMPLATE_DEFAULT_FEATURE.lowercase(),
        )

    val fileMap =
        listOf(
            srcCommon.resolve(
                "ActivityReplacemeActivityScreen.kt",
            ) to activityDir.resolve("${activityName}ActivityScreen.kt"),
            srcCommon.resolve(
                "ActivityReplacemeDestination.kt",
            ) to activityDir.resolve("${activityName}Destination.kt"),
        )

    val activityConflicts = fileMap.map { it.second }.filter { it.toFile().exists() }
    require(activityConflicts.isEmpty()) {
        "Output file(s) already exist. Delete them first or choose a different name:\n" +
            activityConflicts.joinToString("\n") { "  $it" }
    }
    fileMap.forEach { (src, dst) -> applySubs(src, dst, subs) }

    return GenerationResult(
        createdFiles = fileMap.map { it.second.toString() },
        postGenerationChecklist = activityChecklist(activityName, app, appPascal, activityPackage, windowClass),
    )
}

private fun activityChecklist(
    activityName: String,
    app: String,
    appPascal: String,
    activityPackage: String,
    windowClass: String,
): List<String> =
    listOf(
        "# 1. Register this nav graph within the root nav host (usually WindowNavigationHost).",
        "# Add to $windowClass sealed class:",
        "@Serializable",
        "data object ${activityName}NavGraphDestination : $windowClass()",
        "",
        "# Call inside WindowNavigationHost in <App>WindowScreen.kt:",
        "${toLowerCamel(activityName)}NavGraphNavigation(typeMap)",
        "",
        "# 2. Wire PathNavigation.kt (WASM / URL routing) — add to both handler lists:",
        "import com.cramsan.$app.client.lib.features.$activityPackage.${activityName}Destination",
        "",
        "private val entryToPathHandlers: List<(NavBackStackEntry) -> String?> =",
        "    listOf({ entry -> ${activityName}Destination.toWebPath(entry) })",
        "",
        "private val pathToDestinationHandlers: List<(String) -> Destination?> =",
        "    listOf({ path -> ${activityName}Destination.fromWebPath(path) })",
        "",
        "# 3. If this is the app's first activity, update SplashViewModel.navigateToMainScreen",
        "#    (features/splash/SplashViewModel.kt) — without this the app hangs on splash:",
        "emitWindowEvent(",
        "    ${appPascal}WindowsEvent.NavigateToNavGraph(",
        "        $windowClass.${activityName}NavGraphDestination,",
        "        clearStack = true,",
        "    ),",
        ")",
        "",
        "# 4. Once you create your first real feature (see /create-feature), replace",
        "#    ${TEMPLATE_DEFAULT_FEATURE}Destination: remove it from ${activityName}Destination.kt and",
        "#    update startDestination in ${activityName}ActivityScreen.kt to point at the new feature.",
    )

private fun findWindowNavGraphDestinationClass(repoRoot: Path, app: String): String {
    val windowDir =
        repoRoot.resolve(
            "$app/front-end/app/src/commonMain/kotlin/com/cramsan/$app/client/lib/features/window",
        )
    val file =
        windowDir
            .toFile()
            .listFiles()
            ?.firstOrNull { it.name.endsWith("WindowNavGraphDestination.kt") }
    return file?.nameWithoutExtension ?: "${toPascal(app)}WindowNavGraphDestination"
}

private fun toRepoRelative(repoRoot: Path, parentRel: String): String {
    val rootPrefix = repoRoot.toString().trimEnd('/') + "/"
    return if (parentRel.startsWith(rootPrefix)) parentRel.removePrefix(rootPrefix) else parentRel
}

private fun generateFromTemplates(
    repoRoot: Path,
    name: String,
    app: String,
    filePairs: List<Pair<String, String>>,
    checklist: List<String>,
    extraSubs: Map<String, String> = emptyMap(),
): GenerationResult {
    require(repoRoot.resolve(app).toFile().isDirectory) {
        "App '$app' not found at ${repoRoot.resolve(app)}. " +
            "Run 'devtools create app --name $app' first, or check that --app matches an existing app directory."
    }
    val appPascal = toPascal(app)
    val namePackage = toLowerCamel(name)
    // Order matters: app-level subs run first so they cannot be clobbered by component or extra
    // subs. Extra subs (e.g. provider name) are inserted between app and component entries
    // intentionally so they can reference component placeholder text without conflicting.
    val subs =
        listOf(
            "TemplateReplaceMe" to appPascal,
            "templatereplaceme" to app,
        ) +
            extraSubs.toList() +
            listOf(
                "ComponentReplaceMe" to name,
                "componentreplaceme" to namePackage,
            )
    val resolved = filePairs.map { (t, d) -> repoRoot.resolve(t) to repoRoot.resolve(d) }
    val conflicts = resolved.map { it.second }.filter { it.toFile().exists() }
    require(conflicts.isEmpty()) {
        "Output file(s) already exist. Delete them first or choose a different name:\n" +
            conflicts.joinToString("\n") { "  $it" }
    }
    resolved.forEach { (src, dst) -> applySubs(src, dst, subs) }
    return GenerationResult(
        createdFiles = resolved.map { it.second.toString() },
        postGenerationChecklist = checklist,
    )
}
