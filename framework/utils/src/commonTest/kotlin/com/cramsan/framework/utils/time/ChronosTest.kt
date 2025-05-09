package com.cramsan.framework.utils.time

import com.cramsan.framework.annotations.TestOnly
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@OptIn(TestOnly::class)
class ChronosTest {

    lateinit var clockNow: Clock
    lateinit var clockFuture: Clock

    @BeforeTest
    fun setup() {
        clockNow = object : Clock {
            override fun now(): Instant {
                return Instant.fromEpochSeconds(1000)
            }
        }
        // Clocking mocking being 10 seconds in the future
        clockFuture = object : Clock {
            override fun now(): Instant {
                return Instant.fromEpochSeconds(1010)
            }
        }
    }

    @AfterTest
    fun tearDown() {
        // Reset Chronos after each test
        Chronos.clear()
    }

    @Test
    fun `initializeClock should set clock and timeZone`() {
        // Arrange
        val testClock = clockFuture
        val testTimeZone = TimeZone.UTC
        Chronos.setTimeZoneOverride(testTimeZone)

        // Act
        Chronos.initializeClock(testClock)

        // Assert
        assertEquals(testClock, Chronos.clock())
        assertEquals(testTimeZone, Chronos.timeZone())
    }

    @Test
    fun `setClock should update the clock`() {
        // Arrange
        Chronos.initializeClock(clock = clockNow)
        val testClock = clockFuture
        val testTimeZone = TimeZone.UTC
        Chronos.setTimeZoneOverride(testTimeZone)

        Chronos.setClock(testClock)

        assertEquals(testClock, Chronos.clock())
    }

    @Test
    fun `currentInstant should return the current instant`() {
        // Arrange
        Chronos.initializeClock(clock = clockNow)
        val now = clockNow.now()
        val testTimeZone = TimeZone.UTC
        Chronos.setTimeZoneOverride(testTimeZone)

        // Act
        val currentInstant = Chronos.currentInstant()

        // Assert
        assertEquals(now, currentInstant)
    }

    @Test
    fun `currentLocalDateTime should return the current LocalDateTime`() {
        // Arrange
        Chronos.initializeClock(clock = clockNow)
        val testTimeZone = TimeZone.UTC
        Chronos.setTimeZoneOverride(testTimeZone)
        val now = clockNow.now().toLocalDateTime(TimeZone.UTC)

        // Act
        val currentLocalDateTime = Chronos.currentLocalDateTime()

        // Assert
        assertEquals(now.date, currentLocalDateTime.date)
    }

    @Test
    fun `currentLocalDate should return the current LocalDate`() {
        // Arrange
        Chronos.initializeClock(clock = clockNow)
        val testTimeZone = TimeZone.UTC
        Chronos.setTimeZoneOverride(testTimeZone)
        val now = clockNow.now().toLocalDateTime(TimeZone.UTC).date

        // Act
        val currentLocalDate = Chronos.currentLocalDate()

        // Assert
        assertEquals(now, currentLocalDate)
    }

    @Test
    fun `clock should throw exception if not initialized`() {
        // Act & Assert
        val exception = assertFailsWith<IllegalStateException> {
            Chronos.clock()
        }
        assertEquals("Clock not initialized. Call initializeClock() first.", exception.message)
    }

    @Test
    fun `timeZone return a default value if no override has been set`() {
        // Arrange
        Chronos.initializeClock(clock = clockNow)

        // Act
        val timeZone = Chronos.timeZone()

        // Assert
        assertEquals(TimeZone.currentSystemDefault(), timeZone)
    }

    // Currently not supported on JS/WASM platforms
    @Ignore
    @Test
    fun `setTimeZoneOverride should set the time zone override`() {
        // Arrange
        val testTimeZone = TimeZone.of("America/New_York")

        // Act
        Chronos.setTimeZoneOverride(testTimeZone)

        // Assert
        assertEquals(testTimeZone, Chronos.timeZone())
    }
}