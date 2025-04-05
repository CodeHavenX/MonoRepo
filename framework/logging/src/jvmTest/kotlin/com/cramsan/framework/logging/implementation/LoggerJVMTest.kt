package com.cramsan.framework.logging.implementation

import com.cramsan.framework.logging.Severity
import com.cramsan.framework.test.TestBase
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.BeforeTest
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger
import kotlin.test.Test

class LoggerJVMTest : TestBase() {

    @BeforeTest
    fun setupTest() = Unit

    @Test
    fun `test logging simple message`() = runBlockingTest {
        val log4jLogger: Logger = mockk(relaxed = true)
        val logger = LoggerJVM(log4jLogger, Severity.VERBOSE)

        val exception: Throwable? = null

        logger.log(Severity.VERBOSE, "TEST", "This is a verbose message", exception)

        verify { log4jLogger.log(Level.TRACE, "[TEST]This is a verbose message", exception) }
    }

    @Test
    fun `test logging formatted message`() = runBlockingTest {
        val log4jLogger: Logger = mockk(relaxed = true)
        val logger = LoggerJVM(log4jLogger, Severity.VERBOSE)

        val exception = IllegalStateException()

        logger.log(Severity.ERROR, "TEST", "Message: %s %s", exception, "arg1", "arg2")

        verify { log4jLogger.log(Level.ERROR, "[TEST]Message: arg1 arg2", exception) }
    }
}