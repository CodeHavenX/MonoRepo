package com.cramsan.agentic.core

import java.nio.file.Path

/**
 * Resolves a path string against a base directory.
 *
 * - If [pathString] is an absolute path, it is returned directly.
 * - If [pathString] is a relative path, it is resolved against [baseDir].
 */
fun resolvePath(baseDir: Path, pathString: String): Path {
    val path = Path.of(pathString)
    return if (path.isAbsolute) path else baseDir.resolve(path)
}
