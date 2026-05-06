package com.cramsan.framework.test

import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

actual abstract class CoroutineTest actual constructor() {
    actual val testCoroutineDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    private val _testCoroutineScope = TestScope(testCoroutineDispatcher)

    actual val testCoroutineScope: TestScope = _testCoroutineScope

    actual fun runCoroutineTest(block: suspend TestScope.() -> Unit) {
        _testCoroutineScope.runTest { block() }
    }

    @Deprecated(
        message = "Use runCoroutineTest instead",
        replaceWith = ReplaceWith(expression = "runCoroutineTest(block)"),
    )
    actual fun runBlockingTest(block: suspend TestScope.() -> Unit) = runCoroutineTest(block)
}
