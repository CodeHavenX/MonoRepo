package com.cramsan.framework.haltUtil.implementation

import com.cramsan.framework.halt.implementation.HaltUtilImpl
import com.cramsan.framework.halt.implementation.HaltUtilJVM
import com.cramsan.framework.logging.implementation.NoopEventLoggerDelegate
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.test.CoroutineTest
import kotlin.concurrent.thread
import kotlin.test.BeforeTest
import kotlin.test.Test

/**
 */
class HaltUtilTest : CoroutineTest() {

    @BeforeTest
    fun setupTest() = Unit

    @Test
    fun testStopThread() = runCoroutineTest {
        val haltUtil = HaltUtilImpl(HaltUtilJVM(PassthroughEventLogger(NoopEventLoggerDelegate())))

        thread {
            Thread.sleep(1500)
            haltUtil.resumeThread()
        }
        haltUtil.stopThread()
    }
}
