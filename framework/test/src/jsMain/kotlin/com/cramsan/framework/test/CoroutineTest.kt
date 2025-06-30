package com.cramsan.framework.test

import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

actual abstract class CoroutineTest {

    actual val testCoroutineDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    private val _testCoroutineScope = TestScope(testCoroutineDispatcher)

    @Deprecated(
        message = "Use runCoroutineTest instead",
        replaceWith = ReplaceWith(expression = "runCoroutineTest(block)")
    )
    actual fun runBlockingTest(block: suspend TestScope.() -> Unit) = runCoroutineTest(block)

    actual fun runCoroutineTest(block: suspend TestScope.() -> Unit) {
        _testCoroutineScope.runTest { block() }
    }

    /**
     * Reference to the Scope used to run the tests. This scope can be injected into
     * classes as well.
     */
    actual val testCoroutineScope: TestScope = _testCoroutineScope
}
