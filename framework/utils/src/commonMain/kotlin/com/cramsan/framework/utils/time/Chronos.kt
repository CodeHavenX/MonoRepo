package com.cramsan.framework.utils.time

import com.cramsan.framework.annotations.TestOnly
import com.cramsan.framework.utils.time.Chronos.timeZone
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * This class represent a globally accessible singleton object that provides
 * utility functions for working with time and date. Internally it will be implemented
 * using the [Kotlinx DateTime] library.
 *
 * It is designed to be used in a multiplatform context, allowing for a consistent API.
 *
 * Users are still encouraged to use the [Kotlinx DateTime] library directly for more direct operations.
 * This library will provide a higher level of abstraction and convenience functions for cases in which
 * injection is not possible or practical.
 */
@OptIn(ExperimentalTime::class)
object Chronos {

    private var clock: Clock? = null
    private var timeZoneOverride: TimeZone? = null

    /**
     * Initializes the Chronos object with a [Clock] and [TimeZone].
     *
     * @param clock The clock to be used for time operations. Default is [Clock.System].
     * @param timeZone The time zone to be used for date operations. Default is the current system default.
     */
    fun initializeClock(clock: Clock = Clock.System) {
        require(this.clock == null) { "Clock already initialized. Use setClock() to change it." }
        this.clock = clock
    }

    /**
     * Forces the initialization of the [Clock] instance for testing purposes.
     *
     * @param clock The clock to be used for time operations. Default is [Clock.System].
     */
    @TestOnly
    fun forceInitializeClock(clock: Clock = Clock.System) {
        this.clock = clock
    }

    /**
     * Sets the [Clock] instance to be used for time operations.
     */
    fun setClock(clock: Clock) {
        require(this.clock != null) { "Clock not initialized. Call initializeClock() first." }
        this.clock = clock
    }

    /**
     * Returns the current [Clock] instance.
     *
     * @throws IllegalStateException if the clock has not been initialized.
     */
    fun clock(): Clock = clock ?: error("Clock not initialized. Call initializeClock() first.")

    /**
     * Returns the current [Instant] in the system time zone.
     *
     * @return The current [Instant].
     */
    fun currentInstant(): Instant = clock().now()

    /**
     * Returns the current [LocalDateTime] in the system time zone.
     *
     * @return The current [LocalDateTime].
     */
    fun currentLocalDateTime(): LocalDateTime = currentInstant().toLocalDateTime(timeZone())

    /**
     * Returns the current [LocalDate] in the system time zone.
     *
     * @return The current [LocalDate].
     */
    fun currentLocalDate(): LocalDate = currentLocalDateTime().date

    /**
     * Returns the current [TimeZone] instance used for date operations. This function
     * take in consideration any override that may have been set.
     *
     * @return The current [TimeZone].
     */
    fun timeZone(): TimeZone = timeZoneOverride ?: TimeZone.currentSystemDefault()

    /**
     * Sets the [TimeZone] override instance to be used for date operations.
     */
    @TestOnly
    fun setTimeZoneOverride(timeZone: TimeZone) {
        this.timeZoneOverride = timeZone
    }

    /**
     * Returns the current [TimeZone] instance used as an override.
     */
    @TestOnly
    fun timeZoneOverride(): TimeZone? = timeZoneOverride

    /**
     * Clear the clock and time zone for testing purposes.
     */
    @TestOnly
    fun clear() {
        clock = null
        timeZoneOverride = null
    }
}
