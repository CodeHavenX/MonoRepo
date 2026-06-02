package com.cramsan.devtools.cli

import java.nio.file.Path
import kotlin.io.path.exists

/** Walks up from the current working directory until a directory containing `settings.gradle.kts` is found. */
internal fun detectRepoRoot(): Path {
    var current: Path? = Path.of(System.getProperty("user.dir"))
    while (current != null) {
        if (current.resolve("settings.gradle.kts").exists()) return current
        current = current.parent
    }
    error(
        "Could not locate the repo root. " +
            "Run devtools from within the monorepo, or pass --repo-root explicitly.",
    )
}
