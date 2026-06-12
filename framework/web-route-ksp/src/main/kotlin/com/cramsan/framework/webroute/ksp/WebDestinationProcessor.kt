package com.cramsan.framework.webroute.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier

internal const val WEB_PATH_FQN = "com.cramsan.framework.annotations.WebPath"
internal const val WEB_DESTINATION_FQN = "com.cramsan.framework.core.compose.navigation.WebDestination"

/**
 * For every `sealed class` implementing `WebDestination`, generates an `internal object
 * <Name>WebRoutes` in the same package that implements the bidirectional mapping between the
 * sealed hierarchy and its canonical browser URLs.
 *
 * Every direct subclass of such a sealed class must carry a [com.cramsan.framework.annotations.WebPath]
 * annotation; a subclass without one is reported as an error (failing the build) via [logger].
 */
class WebDestinationProcessor(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) :
    SymbolProcessor {
    private var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) return emptyList()
        invoked = true

        resolver
            .getAllFiles()
            .flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()
            .filter { Modifier.SEALED in it.modifiers && it.implementsWebDestination() }
            .forEach { generateRoutes(it) }

        return emptyList()
    }

    private fun generateRoutes(root: KSClassDeclaration) {
        val containingFile = root.containingFile
        if (containingFile == null) {
            logger.error("${root.simpleName.asString()} has no containing file", root)
            return
        }

        val packageName = root.packageName.asString()
        val rootName = root.simpleName.asString()

        val entries =
            root
                .getSealedSubclasses()
                .map { sub ->
                    val path = sub.webPathOrNull()
                    if (path == null) {
                        logger.error(
                            "$rootName.${sub.simpleName.asString()} implements WebDestination but is " +
                                "missing @WebPath(\"/...\")",
                            sub,
                        )
                    }
                    RouteEntry(sub.simpleName.asString(), path)
                }.toList()

        if (entries.any { it.path == null }) {
            // Errors already reported above; skip generation so the build fails clearly.
            return
        }

        val source = generateWebRoutesSource(packageName, rootName, entries)

        codeGenerator
            .createNewFile(
                dependencies = Dependencies(aggregating = false, containingFile),
                packageName = packageName,
                fileName = "${rootName}WebRoutes",
            ).use { stream ->
                stream.writer().use { it.write(source) }
            }
    }
}

internal fun generateWebRoutesSource(packageName: String, rootName: String, entries: List<RouteEntry>): String =
    buildString {
        appendLine("package $packageName")
        appendLine()
        appendLine("import androidx.navigation.NavBackStackEntry")
        appendLine("import com.cramsan.framework.core.compose.navigation.WebRouteRegistry")
        appendLine("import com.cramsan.framework.core.compose.navigation.webRouteEntry")
        appendLine()
        appendLine("internal object ${rootName}WebRoutes {")
        entries.forEach { entry ->
            appendLine(
                "    private val ${entry.propertyName} = " +
                    "webRouteEntry<$rootName.${entry.simpleName}>(\"${entry.path}\")",
            )
        }
        appendLine()
        appendLine("    private val registry = WebRouteRegistry<$rootName>(")
        appendLine("        listOf(")
        entries.forEach { entry -> appendLine("            ${entry.propertyName},") }
        appendLine("        ),")
        appendLine("    )")
        appendLine()
        appendLine("    fun toWebPath(destination: $rootName): String = when (destination) {")
        entries.forEach { entry ->
            appendLine(
                "        is $rootName.${entry.simpleName} -> ${entry.propertyName}.route.toWebPath(destination)",
            )
        }
        appendLine("    }")
        appendLine()
        appendLine("    fun fromWebPath(path: String): $rootName? = registry.fromWebPath(path)")
        appendLine()
        appendLine("    fun toWebPath(entry: NavBackStackEntry): String? = registry.toWebPath(entry)")
        appendLine("}")
    }

internal data class RouteEntry(val simpleName: String, val path: String?) {
    /** e.g. "FlyerListDestination" -> "flyerListEntry" */
    val propertyName: String =
        simpleName.removeSuffix("Destination").replaceFirstChar { it.lowercaseChar() } + "Entry"
}

internal fun KSClassDeclaration.implementsWebDestination(): Boolean {
    if (qualifiedName?.asString() == WEB_DESTINATION_FQN) return true
    return superTypes.any { ref ->
        (ref.resolve().declaration as? KSClassDeclaration)?.implementsWebDestination() == true
    }
}

internal fun KSClassDeclaration.webPathOrNull(): String? =
    annotations
        .firstOrNull {
            it.annotationType
                .resolve()
                .declaration.qualifiedName
                ?.asString() == WEB_PATH_FQN
        }?.arguments
        ?.firstOrNull { it.name?.asString() == "path" }
        ?.value as? String
