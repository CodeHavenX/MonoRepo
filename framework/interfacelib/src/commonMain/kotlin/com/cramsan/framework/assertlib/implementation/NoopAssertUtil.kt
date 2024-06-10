package com.cramsan.framework.assertlib.implementation

import com.cramsan.framework.assertlib.AssertUtilInterface
import com.cramsan.framework.halt.HaltUtil
import com.cramsan.framework.logging.EventLoggerInterface

/**
 * No-Op implementation of [AssertUtilInterface].
 */
object NoopAssertUtil : AssertUtilInterface {

    override val haltOnFailure: Boolean = false

    override val eventLogger: EventLoggerInterface? = null

    override val haltUtil: HaltUtil? = null

    override fun assert(condition: Boolean, tag: String, message: String) = Unit

    override fun assertFalse(condition: Boolean, tag: String, message: String) = Unit

    override fun assertNull(any: Any?, tag: String, message: String) = Unit

    override fun assertNotNull(any: Any?, tag: String, message: String) = Unit

    override fun assertFailure(tag: String, message: String) = Unit
}
