@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.cramsan.framework.test

import app.cash.turbine.ReceiveTurbine
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.TestTimeSource
import kotlin.time.asClock

/**
 * Helper function to advance the coroutine scope until idle and ensure that all events have been consumed within the
 * [turbine] context.
 */
suspend fun <T> TestScope.advanceUntilIdleAndAwaitComplete(turbine: ReceiveTurbine<T>) {
    this.advanceUntilIdle()
    turbine.ensureAllEventsConsumed()
    assertTrue(turbine.cancelAndConsumeRemainingEvents().isEmpty())
}

/**
 * Extension function to convert a [TestTimeSource] to a [Clock] set to a specific date and time.
 *
 * @param year The year component of the date.
 * @param month The month component of the date (1-12).
 * @param dayOfMonth The day of the month component of the date (1-31).
 * @param hour The hour component of the time (0-23).
 * @param minute The minute component of the time (0-59).
 * @param second The second component of the time (0-59). Default is 0.
 * @param nanosecond The nanosecond component of the time (0-999,999,999). Default is 0.
 * @param timeZone The time zone for the date and time. Default is UTC.
 * @return A [Clock] instance representing the specified date and time in the given time zone.
 */
@OptIn(ExperimentalTime::class)
@Suppress("LongParameterList")
fun TestTimeSource.asClock(
    year: Int,
    month: Int,
    dayOfMonth: Int,
    hour: Int,
    minute: Int,
    second: Int = 0,
    nanosecond: Int = 0,
    timeZone: TimeZone = TimeZone.UTC,
): Clock {
    val dateTime = LocalDateTime(
        year,
        month,
        dayOfMonth,
        hour,
        minute,
        second,
        nanosecond,
    )
    val instant = dateTime.toInstant(timeZone)
    return asClock(instant)
}
