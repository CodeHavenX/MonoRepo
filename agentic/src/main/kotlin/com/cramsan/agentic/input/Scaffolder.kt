package com.cramsan.agentic.input

import java.nio.file.Path

interface Scaffolder {
    fun scaffold(outputDir: Path)
}
