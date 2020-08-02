package com.cramsan.petproject.work

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.work.ListenableWorker.Result
import androidx.work.testing.TestListenableWorkerBuilder
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class SyncWorkerTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    @Throws(Exception::class)
    fun testSimpleEchoWorker() {
        val worker = TestListenableWorkerBuilder<SyncWorker>(context).build()
        runBlocking {
            val result = worker.doWork()
            assertThat(result, `is`(Result.success()))
        }
    }
}
