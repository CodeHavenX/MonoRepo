package com.cramsan.framework.test

import app.cash.turbine.ReceiveTurbine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.assertTrue

/**
 * Helper function to advance the coroutine scope until idle and ensure that all events have been consumed within the
 * [turbine] context.
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> TestScope.advanceUntilIdleAndAwaitComplete(turbine: ReceiveTurbine<T>) {
    this.advanceUntilIdle()
    turbine.ensureAllEventsConsumed()
    assertTrue(turbine.cancelAndConsumeRemainingEvents().isEmpty())
}
