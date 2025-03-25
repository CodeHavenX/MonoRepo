package com.cramsan.framework.test

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
actual abstract class TestBase {

    actual val testCoroutineDispatcher: CoroutineDispatcher = UnconfinedTestDispatcher()
    private val _testCoroutineScope = TestScope(testCoroutineDispatcher)

    actual fun runBlockingTest(block: suspend TestScope.() -> Unit) {
        _testCoroutineScope.runTest { block() }
    }

    /**
     * Reference to the Scope used to run the tests. This scope can be injected into
     * classes as well.
     */
    actual val testCoroutineScope: CoroutineScope = _testCoroutineScope
}
