package com.cramsan

import javax.inject.Inject
import org.gradle.api.provider.Property

/**
 * Describes a single supported IDE entry in [IdeCheckExtension.ides].
 *
 * [name] must match the value of the `idea.platform.prefix` system property
 * (e.g. `"AndroidStudio"`, `"Idea"`).
 */
abstract class IdeSpec @Inject constructor(val name: String) {
    /**
     * Minimum required IDE version, dot-separated (e.g. `"2026.2"`).
     * Compared component-by-component against the `idea.version` system property.
     * Leave unset to accept any version of this IDE.
     */
    abstract val minVersion: Property<String>
}
