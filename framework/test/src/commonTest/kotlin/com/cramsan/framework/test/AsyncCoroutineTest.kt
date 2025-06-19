package com.cramsan.framework.test

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

@ExperimentalCoroutinesApi
class AsyncCoroutineTest : TestBase() {

    lateinit var viewModel: SimpleViewModel
    lateinit var repository: Repository

    @BeforeTest

    fun setupTest() {
        this.repository = Repository()
        this.viewModel = SimpleViewModel(testCoroutineScope, repository)
    }

    @Test
    fun test_that_async_code_is_executed_eagerly() = runBlockingTest {
        var value = 0
        viewModel.postDelayed(2.minutes) {
            value = 2
        }

        viewModel.postDelayed(1.minutes) {
            value = 1
        }
        advanceUntilIdle()

        assertEquals(2, value, "The second postDelayed should execute first due to its shorter delay")
    }
}
