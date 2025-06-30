package com.cramsan.framework.test

import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope

/**
 * Base class that should handle running unit tests. This class will be implemented on each platform
 * to provide the right approach for each one of them.
 */
expect abstract class CoroutineTest() {

    /**
     * Reference to the Scope used to run the tests. This scope can be injected into
     * classes as well.
     */
    val testCoroutineScope: TestScope

    val testCoroutineDispatcher: TestDispatcher

    /**
     * We need to make sure that tests will be started with this function. Each platform will provide
     * the right configuration and rules to run unit tests.
     */
    fun runCoroutineTest(block: suspend TestScope.() -> Unit)

    @Deprecated(
        message = "Use runCoroutineTest instead",
        replaceWith = ReplaceWith("runCoroutineTest(block)")
    )
    fun runBlockingTest(block: suspend TestScope.() -> Unit)
}
