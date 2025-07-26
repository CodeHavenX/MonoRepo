package com.cramsan.sample.frontend.architecture.shared.domain.usecase

import kotlinx.coroutines.test.runTest as runTestImpl

actual fun runTest(block: suspend () -> Unit) = runTestImpl { block() }