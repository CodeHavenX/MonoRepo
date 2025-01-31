package com.cramsan.framework.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestScope

actual abstract class TestBase actual constructor() {
    actual val testCoroutineScope: CoroutineScope
        get() = TODO("Not yet implemented")

    actual fun runBlockingTest(block: suspend TestScope.() -> Unit) = Unit

    actual abstract fun setupTest()
}
