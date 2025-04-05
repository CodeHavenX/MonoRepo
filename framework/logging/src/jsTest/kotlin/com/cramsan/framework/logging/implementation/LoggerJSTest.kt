package com.cramsan.framework.logging.implementation

import com.cramsan.framework.logging.Severity
import kotlin.test.Test

class LoggerJSTest {

    @Test
    fun testLogVerbose() {
        val logger = LoggerJS()
        logger.log(Severity.VERBOSE, "TestTag", "Verbose message", null)
        // Verify the output in the console (this might require a custom console implementation for testing)
    }

    @Test
    fun testLogDebug() {
        val logger = LoggerJS()
        logger.log(Severity.DEBUG, "TestTag", "Debug message", null)
        // Verify the output in the console
    }

    @Test
    fun testLogInfo() {
        val logger = LoggerJS()
        logger.log(Severity.INFO, "TestTag", "Info message", null)
        // Verify the output in the console
    }

    @Test
    fun testLogWarning() {
        val logger = LoggerJS()
        logger.log(Severity.WARNING, "TestTag", "Warning message", null)
        // Verify the output in the console
    }

    @Test
    fun testLogError() {
        val logger = LoggerJS()
        logger.log(Severity.ERROR, "TestTag", "Error message", null)
        // Verify the output in the console
    }

    @Test
    fun testLogWithThrowable() {
        val logger = LoggerJS()
        val throwable = Throwable("Test throwable")
        logger.log(Severity.ERROR, "TestTag", "Error message with throwable", throwable)
        // Verify the output in the console
    }
}