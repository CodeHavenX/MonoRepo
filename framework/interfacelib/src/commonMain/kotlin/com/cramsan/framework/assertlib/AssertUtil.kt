package com.cramsan.framework.assertlib

import kotlin.native.concurrent.ThreadLocal

/**
 * @Author cramsan
 * @created 1/17/2021
 *
 * Singleton that manages an instance of an [AssertUtilInterface]. The [_singleton] starts as
 * null and therefore the caller needs to ensure set it to an instance. If an
 * instance is not set, then any calls to a method that tries to access it will result in a
 * [Throwable] being thrown.
 */
@ThreadLocal
object AssertUtil {

    private lateinit var _singleton: AssertUtilInterface

    /**
     * Global [AssertUtilInterface] singleton
     */
    val singleton: AssertUtilInterface
        get() {
            return if (::_singleton.isInitialized) {
                _singleton
            } else {
                error("AssertUtil not initialized. Please call AssertUtil.setInstance() before using it.")
            }
        }

    /**
     * Set the instance to be used for the [singleton].
     */
    fun setInstance(assertUtil: AssertUtilInterface) {
        _singleton = assertUtil
    }
}

/**
 * List of global functions to provide an easy API for assertions
 */

/**
 * Global function that delegates to [AssertUtil._singleton] and calls [AssertUtilInterface.assert].
 *
 * @see AssertUtilInterface.assert
 * @see AssertUtil._singleton
 */
fun assert(condition: Boolean, tag: String, message: String) {
    AssertUtil.singleton.assert(condition, tag, message)
}

/**
 * Global function that delegates to [AssertUtil._singleton] and calls [AssertUtilInterface.assertFalse].
 *
 * @see AssertUtilInterface.assertFalse
 * @see AssertUtil._singleton
 */
fun assertFalse(condition: Boolean, tag: String, message: String) {
    AssertUtil.singleton.assert(!condition, tag, message)
}

/**
 * Global function that delegates to [AssertUtil._singleton] and calls [AssertUtilInterface.assertNull].
 *
 * @see AssertUtilInterface.assertNull
 * @see AssertUtil._singleton
 */
fun assertNull(any: Any?, tag: String, message: String) {
    AssertUtil.singleton.assert(any == null, tag, message)
}

/**
 * Global function that delegates to [AssertUtil._singleton] and calls [AssertUtilInterface.assertNotNull].
 *
 * @see AssertUtilInterface.assertNotNull
 * @see AssertUtil._singleton
 */
fun assertNotNull(any: Any?, tag: String, message: String) {
    AssertUtil.singleton.assert(any != null, tag, message)
}

/**
 * Global function that delegates to [AssertUtil._singleton] and calls [AssertUtilInterface.assertFailure].
 *
 * @see AssertUtilInterface.assertFailure
 * @see AssertUtil._singleton
 */
fun assertFailure(tag: String, message: String) {
    AssertUtil.singleton.assert(false, tag, message)
}
