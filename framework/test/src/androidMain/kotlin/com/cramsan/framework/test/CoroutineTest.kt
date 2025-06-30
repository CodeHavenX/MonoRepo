package com.cramsan.framework.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.MockKAnnotations
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.Rule
import kotlin.test.BeforeTest

@Suppress("UnnecessaryAbstractClass")
actual abstract class CoroutineTest {
    // This is needed so that we can run APIs that interface with the different Android Loopers.
    // As a result all tasks that are dispatched to another looper, are executed instantaneously.
    // A prime example is MutableLiveData.postValue.
    // https://proandroiddev.com/how-to-unit-test-code-with-coroutines-50c1640f6bef
    @get:Rule
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var testCoroutineRule: TestCoroutineRule = TestCoroutineRule()

    actual fun runCoroutineTest(block: suspend TestScope.() -> Unit) = testCoroutineRule.runBlockingTest { block() }

    @Deprecated(
        message = "Use runCoroutineTest instead",
        replaceWith = ReplaceWith(expression = "runCoroutineTest(block)")
    )
    actual fun runBlockingTest(block: suspend TestScope.() -> Unit) = runCoroutineTest(block)

    @BeforeTest
    fun internalSetupTest() {
        MockKAnnotations.init(this)
    }

    /**
     * Reference to the Scope used to run the tests. This scope can be injected into
     * classes as well.
     */
    actual val testCoroutineScope: TestScope
        get() = testCoroutineRule.testCoroutineScope

    actual val testCoroutineDispatcher: TestDispatcher
        get() = testCoroutineRule.testCoroutineDispatcher
}
