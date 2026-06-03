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
        repoRoot.resolve(
            "templatereplaceme/api/src/commonMain/kotlin/com/cramsan/templatereplaceme/api/ComponentReplacemeApi.kt",
        )
    val tmplReq =
        repoRoot.resolve(
            "templatereplaceme/shared/src/commonMain/kotlin/com/cramsan/templatereplaceme/lib/model/network/CreateComponentReplacemeNetworkRequest.kt",
        )
    val tmplResp =
        repoRoot.resolve(
            "templatereplaceme/shared/src/commonMain/kotlin/com/cramsan/templatereplaceme/lib/model/network/ComponentReplacemeNetworkResponse.kt",
        )

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
        repoRoot.resolve(
            "templatereplaceme/back-end/src/main/kotlin/com/cramsan/templatereplaceme/server/controller/ComponentReplacemeController.kt",
        )
    val tmplTest =
        repoRoot.resolve(
            "templatereplaceme/back-end/src/test/kotlin/com/cramsan/templatereplaceme/server/controller/ComponentReplacemeControllerTest.kt",
        )

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
        repoRoot.resolve(
            "templatereplaceme/back-end/src/main/kotlin/com/cramsan/templatereplaceme/server/service/ComponentReplacemeService.kt",
        )
    val tmplTest =
        repoRoot.resolve(
            "templatereplaceme/back-end/src/test/kotlin/com/cramsan/templatereplaceme/server/service/ComponentReplacemeServiceTest.kt",
        )

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
        repoRoot.resolve(
            "templatereplaceme/back-end/src/main/kotlin/com/cramsan/templatereplaceme/server/datastore/ComponentReplacemeDatastore.kt",
        )
    val tmplImpl =
        repoRoot.resolve(
            "templatereplaceme/back-end/src/main/kotlin/com/cramsan/templatereplaceme/server/datastore/impl/ExampleComponentReplacemeDatastore.kt",
        )
    val tmplTest =
        repoRoot.resolve(
            "templatereplaceme/back-end/src/test/kotlin/com/cramsan/templatereplaceme/server/datastore/impl/ComponentReplacemeDatastoreImplTest.kt",
        )

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
        repoRoot.resolve(
            "templatereplaceme/front-end/shared-app/src/commonMain/kotlin/com/cramsan/templatereplaceme/client/lib/managers/ComponentReplacemeManager.kt",
        )
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
        repoRoot.resolve(
            "templatereplaceme/front-end/shared-app/src/commonMain/kotlin/com/cramsan/templatereplaceme/client/lib/service/ComponentReplacemeService.kt",
        )
    val tmplImpl =
        repoRoot.resolve(
            "templatereplaceme/front-end/shared-app/src/commonMain/kotlin/com/cramsan/templatereplaceme/client/lib/service/impl/ComponentReplacemeServiceImpl.kt",
        )

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

private const val FEATURE_TMPL_COMMON =
    "templatereplaceme/front-end/shared-app/src/commonMain/kotlin/com/cramsan/templatereplaceme/client/lib/features/main/menu"

private const val FEATURE_TMPL_TEST =
    "templatereplaceme/front-end/shared-app/src/jvmTest/kotlin/com/cramsan/templatereplaceme/client/lib/features/main/menu"

private const val ACTIVITY_TMPL_COMMON =
    "templatereplaceme/front-end/shared-app/src/commonMain/kotlin/com/cramsan/templatereplaceme/client/lib/features/main"

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
    val app = parentRel.substringBefore("/")
    val appPascal = toPascal(app)
    val featurePackage = featureName.lowercase()

    val commonDir = repoRoot.resolve("$parentRel/$featurePackage")
    val testDir = repoRoot.resolve("${parentRel.replace("/commonMain/", "/jvmTest/")}/$featurePackage")
    val srcCommon = repoRoot.resolve(FEATURE_TMPL_COMMON)
    val srcTest = repoRoot.resolve(FEATURE_TMPL_TEST)

    val subs =
        listOf(
            "TemplateReplaceMe" to appPascal,
            "templatereplaceme" to app,
            "MainMenu" to featureName,
            "main.menu" to featurePackage,
        )

    val fileMap =
        listOf(
            srcCommon.resolve("MainMenuScreen.kt") to commonDir.resolve("${featureName}Screen.kt"),
            srcCommon.resolve("MainMenuEvent.kt") to commonDir.resolve("${featureName}Event.kt"),
            srcCommon.resolve("MainMenuUIState.kt") to commonDir.resolve("${featureName}UIState.kt"),
            srcCommon.resolve("MainMenuViewModel.kt") to commonDir.resolve("${featureName}ViewModel.kt"),
            srcCommon.resolve("MainMenuScreenPreview.kt") to commonDir.resolve("${featureName}ScreenPreview.kt"),
            srcTest.resolve("MainMenuViewModelTest.kt") to testDir.resolve("${featureName}ViewModelTest.kt"),
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
    val app = parentRel.substringBefore("/")
    val appPascal = toPascal(app)
    val activityPackage = activityName.lowercase()

    val activityDir = repoRoot.resolve("$parentRel/$activityPackage")
    val srcCommon = repoRoot.resolve(ACTIVITY_TMPL_COMMON)

    val subs =
        listOf(
            "TemplateReplaceMe" to appPascal,
            "templatereplaceme" to app,
            "Main" to activityName,
            "main" to activityPackage,
        )

    val fileMap =
        listOf(
            srcCommon.resolve("MainActivityScreen.kt") to activityDir.resolve("${activityName}ActivityScreen.kt"),
            srcCommon.resolve("MainDestination.kt") to activityDir.resolve("${activityName}Destination.kt"),
        )

    fileMap.forEach { (src, dst) -> applyOrderedSubs(src, dst, subs) }

    return GenerationResult(
        createdFiles = fileMap.map { it.second.toString() },
        postGenerationChecklist =
        listOf(
            "# Register this nav graph within the root nav host (usually WindowNavigationHost).",
            "# Add ${activityName}NavGraphDestination to ApplicationNavGraphDestination.",
            "# Call ${activityPackage}NavGraphNavigation() in the root nav host.",
        ),
    )
}

private fun applyOrderedSubs(src: Path, dst: Path, subs: List<Pair<String, String>>) {
    dst.parent.toFile().mkdirs()
    var content = src.readText()
    for ((from, to) in subs) {
        content = content.replace(from, to)
    }
    dst.toFile().writeText(content)
}
