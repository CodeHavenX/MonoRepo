package com.cramsan.devtools.core

import java.nio.file.Path
import kotlin.io.path.readText

/** Generates an API interface and its network request/response models. */
fun generateApi(
    repoRoot: Path,
    name: String,
    app: String,
): GenerationResult {
    val appPascal = toPascal(app)
    val nameLower = toLowerCamel(name)

    val tmplApi =
        repoRoot.resolve("devtools/templates/api/ComponentReplacemeApi.kt")
    val tmplReq =
        repoRoot.resolve("devtools/templates/api/CreateComponentReplacemeNetworkRequest.kt")
    val tmplResp =
        repoRoot.resolve("devtools/templates/api/ComponentReplacemeNetworkResponse.kt")

    val destApi = repoRoot.resolve("$app/api/src/commonMain/kotlin/com/cramsan/$app/api/${name}Api.kt")
    val destReq =
        repoRoot.resolve(
            "$app/shared/src/commonMain/kotlin/com/cramsan/$app/lib/model/network/Create${name}NetworkRequest.kt",
        )
    val destResp =
        repoRoot.resolve(
            "$app/shared/src/commonMain/kotlin/com/cramsan/$app/lib/model/network/${name}NetworkResponse.kt",
        )

    applySubs(tmplApi, destApi, appPascal, app, name, nameLower)
    applySubs(tmplReq, destReq, appPascal, app, name, nameLower)
    applySubs(tmplResp, destResp, appPascal, app, name, nameLower)

    return GenerationResult(
        createdFiles =
        listOf(
            destApi.toString(),
            destReq.toString(),
            destResp.toString(),
        ),
        postGenerationChecklist =
        listOf(
            "[ ] Wire-up this Api with its respective Controller",
        ),
    )
}

/** Generates a back-end Controller and its unit test. */
fun generateController(
    repoRoot: Path,
    name: String,
    app: String,
): GenerationResult {
    val appPascal = toPascal(app)
    val nameLower = toLowerCamel(name)

    val tmpl =
        repoRoot.resolve("devtools/templates/backend/controller/ComponentReplacemeController.kt")
    val tmplTest =
        repoRoot.resolve("devtools/templates/backend/controller/ComponentReplacemeControllerTest.kt")

    val dest = repoRoot.resolve("$app/back-end/src/main/kotlin/com/cramsan/$app/server/controller/${name}Controller.kt")
    val destTest =
        repoRoot.resolve(
            "$app/back-end/src/test/kotlin/com/cramsan/$app/server/controller/${name}ControllerTest.kt",
        )

    applySubs(tmpl, dest, appPascal, app, name, nameLower)
    applySubs(tmplTest, destTest, appPascal, app, name, nameLower)

    return GenerationResult(
        createdFiles =
        listOf(
            dest.toString(),
            destTest.toString(),
        ),
        postGenerationChecklist =
        listOf(
            "# Add to $app/back-end/src/main/kotlin/com/cramsan/$app/server/dependencyinjection/ControllerModule.kt",
            "singleOf(::${name}Controller) { bind<Controller>() }",
            "",
            "# Also verify the API route is registered in registerRoutes.",
        ),
    )
}

/** Generates a back-end Service and its unit test. */
fun generateService(
    repoRoot: Path,
    name: String,
    app: String,
): GenerationResult {
    val appPascal = toPascal(app)
    val nameLower = toLowerCamel(name)

    val tmpl =
        repoRoot.resolve("devtools/templates/backend/service/ComponentReplacemeService.kt")
    val tmplTest =
        repoRoot.resolve("devtools/templates/backend/service/ComponentReplacemeServiceTest.kt")

    val dest = repoRoot.resolve("$app/back-end/src/main/kotlin/com/cramsan/$app/server/service/${name}Service.kt")
    val destTest =
        repoRoot.resolve(
            "$app/back-end/src/test/kotlin/com/cramsan/$app/server/service/${name}ServiceTest.kt",
        )

    applySubs(tmpl, dest, appPascal, app, name, nameLower)
    applySubs(tmplTest, destTest, appPascal, app, name, nameLower)

    return GenerationResult(
        createdFiles =
        listOf(
            dest.toString(),
            destTest.toString(),
        ),
        postGenerationChecklist =
        listOf(
            "# Add to $app/back-end/src/main/kotlin/com/cramsan/$app/server/dependencyinjection/ServicesModule.kt",
            "singleOf(::${name}Service)",
        ),
    )
}

/** Generates a back-end Datastore interface, a provider implementation, and its unit test. */
fun generateDatastore(
    repoRoot: Path,
    name: String,
    app: String,
    provider: String,
): GenerationResult {
    val appPascal = toPascal(app)
    val nameLower = toLowerCamel(name)

    val tmplIface =
        repoRoot.resolve("devtools/templates/backend/datastore/ComponentReplacemeDatastore.kt")
    val tmplImpl =
        repoRoot.resolve("devtools/templates/backend/datastore/impl/ExampleComponentReplacemeDatastore.kt")
    val tmplTest =
        repoRoot.resolve("devtools/templates/backend/datastore/impl/ComponentReplacemeDatastoreImplTest.kt")

    val destIface =
        repoRoot.resolve(
            "$app/back-end/src/main/kotlin/com/cramsan/$app/server/datastore/${name}Datastore.kt",
        )
    val destImpl =
        repoRoot.resolve(
            "$app/back-end/src/main/kotlin/com/cramsan/$app/server/datastore/impl/${provider}${name}Datastore.kt",
        )
    val destTest =
        repoRoot.resolve(
            "$app/back-end/src/test/kotlin/com/cramsan/$app/server/datastore/impl/${provider}${name}DatastoreTest.kt",
        )

    val providerSub = mapOf("Example" to provider)

    applySubs(tmplIface, destIface, appPascal, app, name, nameLower)
    applySubs(tmplImpl, destImpl, appPascal, app, name, nameLower, providerSub)
    applySubs(tmplTest, destTest, appPascal, app, name, nameLower, providerSub)

    return GenerationResult(
        createdFiles =
        listOf(
            destIface.toString(),
            destImpl.toString(),
            destTest.toString(),
        ),
        postGenerationChecklist =
        listOf(
            "# Add to $app/back-end/src/main/kotlin/com/cramsan/$app/server/dependencyinjection/DatastoreModule.kt",
            "singleOf(::${provider}${name}Datastore) { bind<${name}Datastore>() }",
        ),
    )
}

/** Generates a front-end Manager. */
fun generateManager(
    repoRoot: Path,
    name: String,
    app: String,
): GenerationResult {
    val appPascal = toPascal(app)
    val nameLower = toLowerCamel(name)

    val tmpl =
        repoRoot.resolve("devtools/templates/frontend/manager/ComponentReplacemeManager.kt")
    val dest =
        repoRoot.resolve(
            "$app/front-end/shared-app/src/commonMain/kotlin/com/cramsan/$app/client/lib/managers/${name}Manager.kt",
        )

    applySubs(tmpl, dest, appPascal, app, name, nameLower)

    return GenerationResult(
        createdFiles = listOf(dest.toString()),
        postGenerationChecklist =
        listOf(
            "# Add to $app/front-end/shared-app/src/commonMain/kotlin/com/cramsan/$app/client/lib/di/ManagerModule.kt",
            "singleOf(::${name}Manager)",
            "",
            "# Note: No jvmTest template exists for managers. Write tests manually in:",
            "#   $app/front-end/shared-app/src/jvmTest/kotlin/com/cramsan/$app/client/lib/managers/${name}ManagerTest.kt",
        ),
    )
}

/** Generates a front-end Service interface and its implementation. */
fun generateFrontendService(
    repoRoot: Path,
    name: String,
    app: String,
): GenerationResult {
    val appPascal = toPascal(app)
    val nameLower = toLowerCamel(name)

    val tmplIface =
        repoRoot.resolve("devtools/templates/frontend/service/ComponentReplacemeService.kt")
    val tmplImpl =
        repoRoot.resolve("devtools/templates/frontend/service/impl/ComponentReplacemeServiceImpl.kt")

    val destIface =
        repoRoot.resolve(
            "$app/front-end/shared-app/src/commonMain/kotlin/com/cramsan/$app/client/lib/service/${name}Service.kt",
        )
    val destImpl =
        repoRoot.resolve(
            "$app/front-end/shared-app/src/commonMain/kotlin/com/cramsan/$app/client/lib/service/impl/${name}ServiceImpl.kt",
        )

    applySubs(tmplIface, destIface, appPascal, app, name, nameLower)
    applySubs(tmplImpl, destImpl, appPascal, app, name, nameLower)

    return GenerationResult(
        createdFiles =
        listOf(
            destIface.toString(),
            destImpl.toString(),
        ),
        postGenerationChecklist =
        listOf(
            "# Add to $app/front-end/shared-app/src/commonMain/kotlin/com/cramsan/$app/client/lib/di/ServiceModule.kt",
            "singleOf(::${name}ServiceImpl) { bind<${name}Service>() }",
            "",
            "# Note: No jvmTest template exists for frontend services. Write tests manually in:",
            "#   $app/front-end/shared-app/src/jvmTest/kotlin/com/cramsan/$app/client/lib/service/impl/${name}ServiceImplTest.kt",
        ),
    )
}

private const val FEATURE_TMPL_COMMON = "devtools/templates/frontend/feature"

private const val FEATURE_TMPL_TEST = "devtools/templates/frontend/feature"

private const val ACTIVITY_TMPL_COMMON = "devtools/templates/frontend/activity"

/**
 * Generates a full Compose feature: Screen, Event, UIState, ViewModel, ScreenPreview, and ViewModelTest.
 *
 * Uses `templatereplaceme/front-end/.../features/main/menu` as the template source.
 *
 * @param parentRel repo-relative path to the parent features directory,
 *   e.g. `edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features`
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
    val activityName = toPascal(activityPackage)
    val featurePackage = featureName.lowercase()

    val commonDir = repoRoot.resolve("$normalizedParent/$featurePackage")
    val testDir = repoRoot.resolve("${normalizedParent.replace("/commonMain/", "/jvmTest/")}/$featurePackage")
    val srcCommon = repoRoot.resolve(FEATURE_TMPL_COMMON)
    val srcTest = repoRoot.resolve(FEATURE_TMPL_TEST)

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
            srcTest.resolve("FeatureReplacemeViewModelTest.kt") to testDir.resolve("${featureName}ViewModelTest.kt"),
        )

    fileMap.forEach { (src, dst) -> applyOrderedSubs(src, dst, subs) }

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
 * Uses `templatereplaceme/front-end/.../features/main` as the template source.
 *
 * @param parentRel repo-relative path to the parent features directory,
 *   e.g. `edifikana/front-end/shared-app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features`
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
            "activityreplaceme" to activityPackage,
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

    fileMap.forEach { (src, dst) -> applyOrderedSubs(src, dst, subs) }

    return GenerationResult(
        createdFiles = fileMap.map { it.second.toString() },
        postGenerationChecklist =
        listOf(
            "# Register this nav graph within the root nav host (usually WindowNavigationHost).",
            "# Add ${activityName}NavGraphDestination to $windowClass sealed class.",
            "# Call ${activityPackage}NavGraphNavigation() in the root nav host.",
        ),
    )
}

private fun findWindowNavGraphDestinationClass(repoRoot: Path, app: String): String {
    val windowDir =
        repoRoot.resolve(
            "$app/front-end/shared-app/src/commonMain/kotlin/com/cramsan/$app/client/lib/features/window",
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

private fun applyOrderedSubs(src: Path, dst: Path, subs: List<Pair<String, String>>) {
    dst.parent.toFile().mkdirs()
    var content = src.readText()
    for ((from, to) in subs) {
        content = content.replace(from, to)
    }
    dst.toFile().writeText(content)
}
