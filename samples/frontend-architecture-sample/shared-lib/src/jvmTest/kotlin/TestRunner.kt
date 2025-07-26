package com.cramsan.sample.frontend.architecture.shared.domain.usecase

import kotlinx.coroutines.test.runTest

actual fun runTest(block: suspend () -> Unit) = runTest { block() }