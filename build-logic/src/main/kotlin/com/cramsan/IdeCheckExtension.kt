package com.cramsan

import javax.inject.Inject
import org.gradle.api.model.ObjectFactory
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.provider.Property

/**
 * Configuration for the IDE compatibility check applied by [ide-check.gradle.kts].
 *
 * Populate [ides] with one entry per supported IDE. Each entry's name must match
 * the `idea.platform.prefix` system property value for that IDE (e.g. `"AndroidStudio"`,
 * `"Idea"`). Any IDE whose prefix is absent from the container is considered unsupported.
 *
 * Set [failOnUnsupportedIde] to `false` to demote violations from a build failure to a warning
 * (useful when temporarily onboarding a new IDE or debugging sync issues).
 *
 * ```kotlin
 * ideCheck {
 *     failOnUnsupportedIde.set(true)   // default; set false to warn instead of fail
 *     ides {
 *         register("AndroidStudio") { minVersion.set("2026.2") }
 *         register("Idea")          { minVersion.set("2026.3") }
 *     }
 * }
 * ```
 */
abstract class IdeCheckExtension @Inject constructor(objects: ObjectFactory) {
    /**
     * When `true` (the default), any IDE violation throws a [org.gradle.api.GradleException]
     * and aborts the sync/build immediately. Set to `false` to downgrade violations to warnings.
     */
    val failOnUnsupportedIde: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    /** Supported IDEs keyed by `idea.platform.prefix`. */
    val ides: NamedDomainObjectContainer<IdeSpec> = objects.domainObjectContainer(IdeSpec::class.java)
}
