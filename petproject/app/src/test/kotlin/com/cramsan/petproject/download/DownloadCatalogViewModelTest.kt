package com.cramsan.petproject.download

import androidx.lifecycle.Observer
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.metrics.MetricsInterface
import com.cramsan.framework.test.TestBase
import com.cramsan.framework.thread.ThreadUtilInterface
import com.cramsan.petproject.PetProjectApplication
import com.cramsan.petproject.appcore.provider.ModelProviderInterface
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class DownloadCatalogViewModelTest : TestBase() {

    lateinit var application: PetProjectApplication
    lateinit var modelProvider: ModelProviderInterface
    lateinit var log: EventLoggerInterface
    lateinit var metrics: MetricsInterface
    lateinit var thread: ThreadUtilInterface
    lateinit var testDispatcher: CoroutineDispatcher
    lateinit var viewModel: DownloadCatalogViewModel

    lateinit var observer: Observer<Boolean>

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        application = mockk(relaxed = true)
        modelProvider = mockk(relaxed = true)
        log = mockk(relaxed = true)
        metrics = mockk(relaxed = true)
        thread = mockk(relaxed = true)
        testDispatcher = TestCoroutineDispatcher()
        observer = mockk(relaxed = true)

        viewModel = DownloadCatalogViewModel(application, log, metrics, thread, modelProvider, testDispatcher)
        viewModel.observableIsDownloadComplete.observeForever(observer)
    }

    @Test
    fun testIsCatalogReadyInitialState() = runBlockingTest {
        assertEquals(false, viewModel.observableIsDownloadComplete.value)
        assertFalse(viewModel.isCatalogReady())
        assertEquals(false, viewModel.observableIsDownloadComplete.value)
    }

    @Test
    fun testIsCatalogReadyOnceDownloaded() = runBlockingTest {
        every { modelProvider.isCatalogAvailable(any()) } returns true

        assertEquals(false, viewModel.observableIsDownloadComplete.value)
        assertTrue(viewModel.isCatalogReady())
        assertEquals(true, viewModel.observableIsDownloadComplete.value)
    }

    @Test
    fun testDownloadCatalogOnBackground() = runBlockingTest {
        every { modelProvider.isCatalogAvailable(any()) } returns false
        coEvery { modelProvider.getPlantsWithToxicity(any(), any()) } returns emptyList()

        assertEquals(false, viewModel.observableIsDownloadComplete.value)
        viewModel.downloadCatalog()
        verify { observer.onChanged(true) }
        assertEquals(true, viewModel.observableIsDownloadComplete.value)
    }
}