package com.cramsan.framework.logging.implementation

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cramsan.framework.logging.EventLoggerErrorCallbackInterface
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit test. This will be executed in a mocked Android environment.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class EventLoggerTest {

    private lateinit var eventLoggerTest: EventLoggerCommonTest

    @Before
    fun setUp() {
        eventLoggerTest = EventLoggerCommonTest()
    }

    @Test
    fun logWithVerboseSeverity() {
        val errorCallback = mockk<EventLoggerErrorCallbackInterface>(relaxUnitFun = true)
        eventLoggerTest.logWithVerboseSeverity(LoggerAndroid(), errorCallback)
    }

    @Test
    fun logWithDebugSeverity() {
        val errorCallback = mockk<EventLoggerErrorCallbackInterface>(relaxUnitFun = true)
        eventLoggerTest.logWithDebugSeverity(LoggerAndroid(), errorCallback)
    }

    @Test
    fun logWithInfoSeverity() {
        val errorCallback = mockk<EventLoggerErrorCallbackInterface>(relaxUnitFun = true)
        eventLoggerTest.logWithInfoSeverity(LoggerAndroid(), errorCallback)
    }

    @Test
    fun logWithWarningSeverity() {
        val errorCallback = mockk<EventLoggerErrorCallbackInterface>(relaxUnitFun = true)
        eventLoggerTest.logWithWarningSeverity(LoggerAndroid(), errorCallback)
    }

    @Test
    fun logWithErrorSeverity() {
        val errorCallback = mockk<EventLoggerErrorCallbackInterface>(relaxUnitFun = true)
        eventLoggerTest.logWithErrorSeverity(LoggerAndroid(), errorCallback)
    }
}