package com.cramsan.agentic.input

import com.cramsan.agentic.core.AgenticDocument
import com.cramsan.agentic.core.ValidationIssue
import com.cramsan.agentic.core.ValidationReport

interface ValidationService {
    suspend fun reviewDocument(document: AgenticDocument): List<ValidationIssue>
    suspend fun runValidationPass(): ValidationReport
}
