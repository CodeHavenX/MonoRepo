package com.cramsan.devtools.core

/** Returned by every generator: the list of created file paths and post-generation instructions. */
data class GenerationResult(val createdFiles: List<String>, val postGenerationChecklist: List<String>)
