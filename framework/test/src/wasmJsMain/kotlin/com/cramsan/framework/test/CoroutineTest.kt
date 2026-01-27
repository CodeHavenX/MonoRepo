package com.cramsan.framework.test

import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope

actual abstract class CoroutineTest actual constructor() {
    actual val testCoroutineScope: TestScope
        get() = TODO("Not yet implemented")

    actual val testCoroutineDispatcher: TestDispatcher
        get() = TODO("Not yet implemented")
    actual fun runCoroutineTest(block: suspend TestScope.() -> Unit) = Unit

    @Deprecated(
        message = "Use runCoroutineTest instead",
        replaceWith = ReplaceWith(expression = "runCoroutineTest(block)"),
    )
    actual fun runBlockingTest(block: suspend TestScope.() -> Unit) = runCoroutineTest(block)
}
