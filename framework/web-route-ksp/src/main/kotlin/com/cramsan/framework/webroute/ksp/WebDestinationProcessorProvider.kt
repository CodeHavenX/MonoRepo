package com.cramsan.framework.webroute.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * Registers [WebDestinationProcessor] with KSP.
 *
 * Reads the optional `webRouteAggregatorPackage`/`webRouteAggregatorName` KSP arguments
 * (`ksp { arg("webRouteAggregatorPackage", "...") ; arg("webRouteAggregatorName", "...") }`)
 * to opt a module into per-module path-navigation aggregator generation.
 */
class WebDestinationProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        WebDestinationProcessor(
            environment.codeGenerator,
            environment.logger,
            environment.options["webRouteAggregatorPackage"],
            environment.options["webRouteAggregatorName"],
        )
}
