package com.cramsan.detektrules

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * Discourages the direct use of `@Preview` on composable functions.
 *
 * Using `@Preview` directly produces only a single rendering. The project-owned multipreview
 * annotations in the `ui-preview` module generate multiple variants (light/dark, mobile/desktop)
 * automatically.
 *
 * **Correct usage**
 * ```kotlin
 * // Full-screen composable → 4 variants (mobile/desktop × light/dark)
 * @ScreenPreviews
 * @Composable
 * private fun MyScreenPreview() = AppTheme { MyScreenContent(...) }
 *
 * // Individual component → 2 variants (light/dark)
 * @ComponentPreviews
 * @Composable
 * private fun MyComponentPreview() = AppTheme { MyComponent(...) }
 * ```
 *
 * **Exempt**: annotation class declarations that stack `@Preview` to define new multipreview
 * annotations — those are `KtClass` nodes, not `KtNamedFunction`, so they are never checked.
 */
class NoDirectPreviewAnnotationRule(config: Config) : Rule(config, DESCRIPTION) {
    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        val annotationNames = function.annotationEntries.mapNotNull { it.shortName?.asString() }

        val hasComposable = COMPOSABLE_ANNOTATION in annotationNames
        val hasDirectPreview = PREVIEW_ANNOTATION in annotationNames

        if (hasComposable && hasDirectPreview) {
            report(
                Finding(
                    Entity.from(function),
                    "Avoid using @$PREVIEW_ANNOTATION directly on composable functions. " +
                        "Use @ScreenPreviews for full-screen composables or @ComponentPreviews " +
                        "for individual components from the ui-preview module.",
                ),
            )
        }
    }

    private companion object {
        const val DESCRIPTION =
            "Discourages @Preview in favour of @ScreenPreviews / @ComponentPreviews."
        const val COMPOSABLE_ANNOTATION = "Composable"
        const val PREVIEW_ANNOTATION = "Preview"
    }
}
