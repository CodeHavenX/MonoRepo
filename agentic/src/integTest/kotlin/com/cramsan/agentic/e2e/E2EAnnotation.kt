package com.cramsan.agentic.e2e

import org.junit.jupiter.api.Tag

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Tag("E2E")
annotation class E2ETest
