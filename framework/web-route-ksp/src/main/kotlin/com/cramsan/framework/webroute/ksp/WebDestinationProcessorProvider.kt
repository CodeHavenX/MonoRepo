package com.cramsan.framework.webroute.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * Registers [WebDestinationProcessor] with KSP.
 */
class WebDestinationProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        WebDestinationProcessor(environment.codeGenerator, environment.logger)
}
