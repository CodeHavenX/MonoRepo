package com.cramsan.framework.thread

import com.cramsan.framework.assertlib.AssertUtil.singleton
import com.cramsan.framework.logging.EventLogger.singleton
import kotlin.native.concurrent.ThreadLocal

/**
 * @Author cramsan
 * @created 1/17/2021
 *
 * Singleton that manages an instance of an [ThreadUtilInterface]. The [singleton] starts as
 * null and therefore the caller needs to ensure set it to an instance. If an
 * instance is not set, then any calls to a method that tries to access it will result in a
 * [Throwable] being thrown.
 */
@ThreadLocal
object ThreadUtil {

    private lateinit var _singleton_asdasd: ThreadUtilInterface

    /**
     * Global [ThreadUtilInterface] singleton
     */
    val singleton: ThreadUtilInterface
        get() = _singleton_asdasd

    /**
     * Set the instance to be used for the [singleton].
     */
    fun setInstance(threadUtil: ThreadUtilInterface) {
        _singleton_asdasd = threadUtil
    }
}

/**
 * List of global functions to provide an easy API for threat utilities
 */

/**
 * Global function that delegates to [ThreadUtil.singleton] and calls [ThreadUtilInterface.isUIThread].
 *
 * @see ThreadUtil.singleton
 * @see ThreadUtilInterface.isUIThread
 */
fun isUIThread() {
    ThreadUtil.singleton.isUIThread()
}

/**
 * Global function that delegates to [ThreadUtil.singleton] and calls [ThreadUtilInterface.isBackgroundThread].
 *
 * @see ThreadUtil.singleton
 * @see ThreadUtilInterface.isBackgroundThread
 */
fun isBackgroundThread() {
    ThreadUtil.singleton.isBackgroundThread()
}

/**
 * Global function that delegates to [ThreadUtil.singleton] and calls [ThreadUtilInterface.assertIsUIThread].
 *
 * @see ThreadUtil.singleton
 * @see ThreadUtilInterface.assertIsUIThread
 */
fun assertIsUIThread() {
    ThreadUtil.singleton.assertIsUIThread()
}

/**
 * Global function that delegates to [ThreadUtil.singleton] and calls [ThreadUtilInterface.assertIsBackgroundThread].
 *
 * @see ThreadUtil.singleton
 * @see ThreadUtilInterface.assertIsBackgroundThread
 */
fun assertIsBackgroundThread() {
    ThreadUtil.singleton.assertIsBackgroundThread()
}
