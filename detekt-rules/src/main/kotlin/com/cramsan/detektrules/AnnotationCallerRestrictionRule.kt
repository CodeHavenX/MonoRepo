package com.cramsan.detektrules

import com.intellij.psi.PsiElement
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunctionType
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * Enforces architectural layer boundaries using explicit annotations.
 *
 * Each layer declares which annotations are allowed to reference it via the `layers` config list.
 * Any annotated class that references a type from a layer it is not permitted to use is reported.
 *
 * Unannotated classes (DI modules, utilities, tests) are not checked.
 *
 * Three categories of usage are detected:
 * - Type references in declarations (constructor params, property types, return types)
 * - Simple constructor call expressions: `MyType(...)`
 * - Qualified constructor call expressions: `Outer.Inner(...)` — the outer KtDotQualifiedExpression
 *   carries the fully-resolved type; the inner KtCallExpression alone cannot be resolved.
 *
 * Config example:
 * ```yaml
 * architecture:
 *   AnnotationCallerRestrictionRule:
 *     active: true
 *     layers:
 *       - 'BackendDatastore:BackendService'
 *       - 'BackendService:BackendController,BackendService'
 *       - 'FrontendService:FrontendManager'
 *       - 'FrontendManager:FrontendViewModel'
 * ```
 */
class AnnotationCallerRestrictionRule(config: Config) :
    Rule(config, DESCRIPTION),
    RequiresAnalysisApi {
    /**
     * Layer definitions in the format `'InjectableAnnotation:AllowedCaller1,AllowedCaller2'`.
     * The left side is the annotation on the type being referenced; the right side lists the
     * annotations the referencing class must have (at least one).
     */
    @Suppress("UnusedPrivateProperty")
    private val layers: List<String> by config(defaultValue = emptyList())

    private val layerRules: Map<String, Set<String>> by lazy {
        layers.associate { entry ->
            val colon = entry.indexOf(':')
            require(colon > 0) { "Invalid layer definition '$entry'. Expected format 'Injectable:Caller1,Caller2'" }
            val injectable = entry.substring(0, colon).trim()
            val callers =
                entry
                    .substring(colon + 1)
                    .split(",")
                    .map(String::trim)
                    .toSet()
            injectable to callers
        }
    }

    private val allKnownAnnotations: Set<String> by lazy {
        (layerRules.keys + layerRules.values.flatten()).toSet()
    }

    private val reported = mutableSetOf<Pair<String, String>>()

    override fun preVisit(root: KtFile) {
        reported.clear()
    }

    // ── Explicit type references (declarations) ───────────────────────────────

    override fun visitTypeReference(typeReference: KtTypeReference) {
        super.visitTypeReference(typeReference)

        // Function types (e.g. `() -> Unit`, `suspend Foo.() -> Bar`) cannot carry architecture
        // annotations. Attempting to call typeReference.type on them throws in the alpha Analysis API.
        if (typeReference.typeElement is KtFunctionType) return

        val (containingClass, callerAnnotations) = callerContext(typeReference) ?: return

        val isInheritance =
            generateSequence(typeReference.parent) { it.parent }
                .any { it is KtSuperTypeListEntry }

        analyze(typeReference) {
            val classSymbol = (typeReference.type as? KaClassType)?.symbol ?: return@analyze
            val typeAnnotations =
                classSymbol.annotations.classIds
                    .map { it.shortClassName.asString() }
                    .toSet()
            if (isInheritance && typeAnnotations.any { it in callerAnnotations }) return@analyze
            checkAnnotations(
                typeAnnotations,
                callerAnnotations,
                containingClass,
                typeReference,
            )
        }
    }

    // ── Simple constructor calls: MyType(...) ─────────────────────────────────

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        // Skip if this call is already the selector of a dot-qualified expression — the outer
        // KtDotQualifiedExpression visitor handles that case with full type context.
        if (expression.parent is KtDotQualifiedExpression) return

        val (containingClass, callerAnnotations) = callerContext(expression) ?: return

        analyze(expression) {
            val classSymbol = (expression.expressionType as? KaClassType)?.symbol ?: return@analyze
            checkAnnotations(
                classSymbol.annotations.classIds
                    .map { it.shortClassName.asString() }
                    .toSet(),
                callerAnnotations,
                containingClass,
                expression,
            )
        }
    }

    // ── Qualified calls: Outer.Inner(...) ─────────────────────────────────────

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)

        // Only care about dot-qualified expressions whose selector is a call (constructor/factory).
        if (expression.selectorExpression !is KtCallExpression) return

        val (containingClass, callerAnnotations) = callerContext(expression) ?: return

        analyze(expression) {
            // The outer expression always has full qualified-type context that the inner
            // KtCallExpression alone (e.g. CreateUserEntity) cannot resolve in isolation.
            val classSymbol = (expression.expressionType as? KaClassType)?.symbol ?: return@analyze
            checkAnnotations(
                classSymbol.annotations.classIds
                    .map { it.shortClassName.asString() }
                    .toSet(),
                callerAnnotations,
                containingClass,
                expression,
            )
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun callerContext(element: PsiElement): Pair<KtClass, Set<String>>? {
        val containingClass =
            generateSequence(element.parent) { it.parent }
                .filterIsInstance<KtClass>()
                .firstOrNull() ?: return null
        val callerAnnotations =
            containingClass.annotationEntries
                .mapNotNull { it.shortName?.asString() }
                .toSet()
        if (callerAnnotations.none { it in allKnownAnnotations }) return null
        return containingClass to callerAnnotations
    }

    private fun checkAnnotations(
        typeAnnotationNames: Set<String>,
        callerAnnotations: Set<String>,
        containingClass: KtClass,
        entity: PsiElement,
    ) {
        val injectableAnnotation = typeAnnotationNames.firstOrNull { it in layerRules } ?: return
        val allowedCallers = layerRules[injectableAnnotation] ?: return
        if (callerAnnotations.none { it in allowedCallers }) {
            val key = (containingClass.fqName?.asString() ?: containingClass.name.orEmpty()) to injectableAnnotation
            if (reported.add(key)) {
                val allowed = allowedCallers.joinToString(", ") { "@$it" }
                report(
                    Finding(
                        entity = Entity.from(entity),
                        message =
                        "'${containingClass.name}' may not reference a type annotated " +
                            "@$injectableAnnotation. Only $allowed may use this layer.",
                    ),
                )
            }
        }
    }

    companion object {
        const val DESCRIPTION =
            "A class may only reference types from the architectural layer directly below it."
    }
}
