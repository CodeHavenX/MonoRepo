package com.cramsan.framework.test

import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope

actual abstract class TestBase actual constructor() {
    actual val testCoroutineScope: TestScope
        get() = TODO("Not yet implemented")

    actual val testCoroutineDispatcher: TestDispatcher
        get() = TODO("Not yet implemented")
    actual fun runBlockingTest(block: suspend TestScope.() -> Unit) = Unit
}
