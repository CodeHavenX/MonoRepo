package com.cramsan.framework.test

import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.BeforeTest

/**
 * This is a copy-paste of the Android version of this file. We are waiting for support of code sharing
 * between Android and JVM so we can finally use a single file.
 */
@Suppress("UnnecessaryAbstractClass")
@OptIn(ExperimentalCoroutinesApi::class)
actual abstract class TestBase {

    @ExtendWith
    var testCoroutineRule: TestCoroutineRule = TestCoroutineRule()

    actual fun runBlockingTest(block: suspend TestScope.() -> Unit) = testCoroutineRule.runBlockingTest { block() }

    /**
     * Reference to the Scope used to run the tests. This scope can be injected into
     * classes as well.
     */
    actual val testCoroutineScope: TestScope
        get() = testCoroutineRule.testCoroutineScope

    actual val testCoroutineDispatcher: TestDispatcher
        get() = testCoroutineRule.testCoroutineDispatcher

    @BeforeTest
    fun internalSetupTest() {
        MockKAnnotations.init(this)
    }
}
