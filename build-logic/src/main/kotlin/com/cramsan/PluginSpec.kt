package com.cramsan

import javax.inject.Inject
import org.gradle.api.provider.Property

/**
 * Describes a single required IDE plugin entry in [IdeCheckExtension.requiredPlugins].
 *
 * [name] is a human-readable display name (e.g. `"Kotlin Multiplatform"`).
 * [dirName] must match the actual subdirectory name inside the IDE's `plugins/` folder
 * (both the user-installed and bundled plugin directories are scanned).
 */
abstract class PluginSpec @Inject constructor(val name: String) {
    /**
     * The directory name of this plugin inside the IDE's `plugins/` folder.
     * Must be set; if omitted the entry is skipped with a warning.
     */
    abstract val dirName: Property<String>
}
