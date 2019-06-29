package com.cramsan.petproject

import android.content.Context
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cramsan.framework.thread.ThreadUtilAPI
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.framework.thread.implementation.ThreadUtil
import com.cramsan.framework.thread.implementation.ThreadUtilInitializer
import com.cramsan.petproject.appcore.framework.CoreFramework
import java.util.concurrent.Semaphore;

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var threadUtil: ThreadUtilInterface
    private lateinit var semaphore: Semaphore

    @Before
    fun setUp() {
        val initializer = ThreadUtilInitializer(MockEventLogger())
        threadUtil = ThreadUtil(initializer)
        semaphore = Semaphore(0)
    }

    @UiThreadTest
    @Test
    fun testIsUIThread() {
        assertTrue(threadUtil.isUIThread())
    }

    @Test
    fun testIsBackgroundThread() {
        assertTrue(threadUtil.isBackgroundThread())
    }

    @UiThreadTest
    @Test
    fun testIsUIThreadInDispatchToUI() {
        assertTrue(threadUtil.isUIThread())
        threadUtil.dispatchToUI {
            assertTrue(threadUtil.isUIThread())
            semaphore.release()
        }
        semaphore.acquire()
    }

    @Test
    fun testIsUIThreadInDispatchToUIFromBackgroundThread() {
        assertTrue(threadUtil.isBackgroundThread())
        threadUtil.dispatchToUI {
            assertTrue(threadUtil.isUIThread())
            semaphore.release()
        }
        semaphore.acquire()
    }

    @Test
    fun testDispatchToBackground() {
        assertTrue(threadUtil.isBackgroundThread())
        threadUtil.dispatchToBackground {
            assertTrue(threadUtil.isBackgroundThread())
            semaphore.release()
        }
        assertTrue(threadUtil.isBackgroundThread())
        semaphore.acquire()
        assertTrue(false)
    }

    @UiThreadTest
    @Test
    fun testDispatchToBackgroundFromUIThread() {
        assertTrue(threadUtil.isUIThread())
        threadUtil.dispatchToBackground {
            assertTrue(threadUtil.isBackgroundThread())
            semaphore.release()
        }
        assertTrue(threadUtil.isUIThread())
        semaphore.acquire()
    }


    @Test
    fun testIsBackgroundThreadNested() {
        assertTrue(threadUtil.isBackgroundThread())
        threadUtil.dispatchToUI {
            assertTrue(threadUtil.isUIThread())
            threadUtil.dispatchToBackground {
                assertTrue(threadUtil.isBackgroundThread())
                semaphore.release()
            }
        }
        semaphore.acquire()
    }
}
