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
 * Populate [plugins] with one entry per required IDE plugin. Each entry's [PluginSpec.dirName]
 * must match the subdirectory name inside the IDE's `plugins/` folder. Both the user-installed
 * plugin directory and the bundled plugin directory are scanned.
 *
 * Set [failOnUnsupportedIde] to `false` to demote IDE violations from a build failure to a
 * warning. Set [failOnMissingPlugin] to `false` to demote missing-plugin violations similarly.
 *
 * ```kotlin
 * ideCheck {
 *     failOnUnsupportedIde.set(true)   // default; set false to warn instead of fail
 *     failOnMissingPlugin.set(true)    // default; set false to warn instead of fail
 *     ides {
 *         register("AndroidStudio") { minVersion.set("2026.2") }
 *         register("Idea")          { minVersion.set("2026.3") }
 *     }
 *     requiredPlugins {
 *         register("Kotlin Multiplatform") { dirName.set("KotlinMultiplatform") }
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

    /**
     * When `true` (the default), missing required plugins throw a [org.gradle.api.GradleException]
     * and abort the sync/build. Set to `false` to downgrade to warnings.
     */
    val failOnMissingPlugin: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    /** Supported IDEs keyed by `idea.platform.prefix`. */
    val ides: NamedDomainObjectContainer<IdeSpec> = objects.domainObjectContainer(IdeSpec::class.java)

    /** Required IDE plugins. Each entry's [PluginSpec.dirName] must match the on-disk plugin folder name. */
    val requiredPlugins: NamedDomainObjectContainer<PluginSpec> = objects.domainObjectContainer(PluginSpec::class.java)
}
