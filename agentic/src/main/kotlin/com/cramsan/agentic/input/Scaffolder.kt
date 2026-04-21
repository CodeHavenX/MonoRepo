package com.cramsan.agentic.input

import java.nio.file.Path

/**
 * Generates the initial set of planning documents and configuration templates that a user
 * edits before running `agentic plan`. Called once during `agentic init`.
 *
 * [scaffold] writes files to [outputDir] and is expected to be idempotent: running it a
 * second time on an already-initialised directory should not destroy user edits. Implementations
 * should skip files that already exist rather than overwriting them.
 */
interface Scaffolder {
    /** Writes starter documents and templates into [outputDir]. */
    fun scaffold(outputDir: Path)
}
