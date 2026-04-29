package com.cramsan.detektrules

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtClass

/**
 * Enforces bidirectional consistency between class names and architectural annotations.
 *
 * Two invariants are checked for each class:
 * 1. Name → Annotation: if a class name ends with a known architectural suffix, the class must
 *    carry at least one of the annotations configured for that suffix.
 * 2. Annotation → Name: if a class carries a known architectural annotation, its name must end
 *    with at least one of the suffixes configured for that annotation.
 *
 * Annotation class declarations are excluded from both checks.
 *
 * Config example:
 * ```yaml
 * architecture:
 *   ArchitectureNamingRule:
 *     active: true
 *     namingSuffixes:
 *       - 'ViewModel:FrontendViewModel'
 *       - 'Manager:FrontendManager'
 *       - 'Service:FrontendService,BackendService'
 *       - 'Controller:BackendController'
 *       - 'Datastore:BackendDatastore'
 * ```
 */
class ArchitectureNamingRule(config: Config) : Rule(config, DESCRIPTION) {
    @Suppress("UnusedPrivateProperty")
    private val namingSuffixes: List<String> by config(defaultValue = emptyList())

    private val suffixToAnnotations: Map<String, Set<String>> by lazy {
        namingSuffixes.associate { entry ->
            val colon = entry.indexOf(':')
            require(colon > 0) {
                "Invalid naming suffix entry '$entry'. Expected format 'Suffix:Annotation1,Annotation2'"
            }
            val suffix = entry.substring(0, colon).trim()
            val annotations =
                entry
                    .substring(colon + 1)
                    .split(",")
                    .map(String::trim)
                    .toSet()
            suffix to annotations
        }
    }

    private val annotationToSuffixes: Map<String, Set<String>> by lazy {
        val result = mutableMapOf<String, MutableSet<String>>()
        for ((suffix, annotations) in suffixToAnnotations) {
            for (annotation in annotations) {
                result.getOrPut(annotation) { mutableSetOf() }.add(suffix)
            }
        }
        result
    }

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        if (klass.isAnnotation()) return

        val className = klass.name ?: return
        val classAnnotations = klass.annotationEntries.mapNotNull { it.shortName?.asString() }.toSet()

        for ((suffix, requiredAnnotations) in suffixToAnnotations) {
            if (className.endsWith(suffix) && classAnnotations.none { it in requiredAnnotations }) {
                val required = requiredAnnotations.joinToString(", ") { "@$it" }
                report(
                    Finding(
                        entity = Entity.from(klass),
                        message = "'$className' ends with '$suffix' but is not annotated with any of: $required.",
                    ),
                )
            }
        }

        for (annotation in classAnnotations) {
            val requiredSuffixes = annotationToSuffixes[annotation] ?: continue
            if (requiredSuffixes.none { className.endsWith(it) || className.endsWith("${it}Impl") }) {
                val required = requiredSuffixes.joinToString(", ") { "'...$it'" }
                report(
                    Finding(
                        entity = Entity.from(klass),
                        message =
                        "'$className' is annotated with @$annotation but its" +
                            " name does not end with any of: $required.",
                    ),
                )
            }
        }
    }

    companion object {
        const val DESCRIPTION =
            "Classes following architectural naming conventions must be annotated, and annotated classes must follow naming conventions."
    }
}
