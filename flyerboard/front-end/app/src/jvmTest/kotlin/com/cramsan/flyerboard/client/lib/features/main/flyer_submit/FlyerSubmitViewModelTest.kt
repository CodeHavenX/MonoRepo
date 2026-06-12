package com.cramsan.flyerboard.client.lib.features.main.flyer_submit

import app.cash.turbine.turbineScope
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
import com.cramsan.flyerboard.client.lib.managers.FlyerManager
import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertIsNot
import kotlin.test.assertTrue

class FlyerSubmitViewModelTest : CoroutineTest() {
    private lateinit var flyerManager: FlyerManager
    private lateinit var viewModel: FlyerSubmitViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventBus: EventBus<ApplicationEvent>

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        flyerManager = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventBus = EventBus()
        windowEventBus = EventBus()
        viewModel =
            FlyerSubmitViewModel(
                dependencies =
                ViewModelDependencies(
                    appScope = testCoroutineScope,
                    dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                    coroutineExceptionHandler = exceptionHandler,
                    windowEventReceiver = windowEventBus,
                    applicationEventReceiver = applicationEventBus,
                ),
                flyerManager = flyerManager,
            )
    }

    @Test
    fun `submit with valid data calls createFlyer and emits NavigateBack on success`() =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)
                val bytes = byteArrayOf(1, 2, 3)
                val createdFlyer = makeFlyerModel("new-flyer")
                coEvery {
                    flyerManager.createFlyer(
                        title = "Summer Concert",
                        description = "Outdoor concert in the park.",
                        expiresAt = "2026-08-01",
                        fileBytes = bytes,
                        fileName = "concert.jpg",
                        mimeType = "image/jpeg",
                    )
                } returns Result.success(createdFlyer)

                viewModel.onTitleChanged("Summer Concert")
                viewModel.onDescriptionChanged("Outdoor concert in the park.")
                viewModel.onExpiresAtChanged("2026-08-01")
                viewModel.onFileSelected(bytes, "concert.jpg", "image/jpeg")
                viewModel.submit()

                coVerify {
                    flyerManager.createFlyer(
                        title = "Summer Concert",
                        description = "Outdoor concert in the park.",
                        expiresAt = "2026-08-01",
                        fileBytes = bytes,
                        fileName = "concert.jpg",
                        mimeType = "image/jpeg",
                    )
                }
                assertIsNot<SubmitStatus.Submitting>(viewModel.uiState.value.status)

                assertEquals(FlyerBoardWindowsEvent.NavigateBack, turbine.awaitItem())
                advanceUntilIdleAndAwaitComplete(turbine)
            }
        }

    @Test
    fun `submit failure emits ShowSnackbar and clears isSubmitting`() =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)
                coEvery {
                    flyerManager.createFlyer(any(), any(), any(), any(), any(), any())
                } returns Result.failure(RuntimeException("Server error"))

                viewModel.submit()

                val event = turbine.awaitItem()
                assertTrue(event is FlyerBoardWindowsEvent.ShowSnackbar)
                assertIs<SubmitStatus.Failed>(viewModel.uiState.value.status)
                advanceUntilIdleAndAwaitComplete(turbine)
            }
        }

    @Test
    fun `onFileSelected updates selectedFileName in UIState`() =
        runCoroutineTest {
            val bytes = byteArrayOf(10, 20, 30)

            viewModel.onFileSelected(bytes, "photo.jpg", "image/jpeg")

            assertEquals("photo.jpg", viewModel.uiState.value.selectedFileName)
        }

    @Test
    fun `navigateBack emits NavigateBack event`() =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)

                viewModel.navigateBack()

                assertEquals(FlyerBoardWindowsEvent.NavigateBack, turbine.awaitItem())
                advanceUntilIdleAndAwaitComplete(turbine)
            }
        }

    @Test
    fun `submit while isSubmitting is a no-op`() =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)
                // Use a deferred so the first coroutine suspends at createFlyer, keeping it in-flight.
                val deferred = CompletableDeferred<Result<FlyerModel>>()
                coEvery {
                    flyerManager.createFlyer(any(), any(), any(), any(), any(), any())
                } coAnswers { deferred.await() }

                viewModel.submit()
                // First coroutine is now suspended at createFlyer; submitting == true.
                assertIs<SubmitStatus.Submitting>(viewModel.uiState.value.status)

                viewModel.submit()
                // Second call is a no-op because the first is in-flight.

                // Release the first coroutine.
                deferred.complete(Result.success(makeFlyerModel("x")))

                assertEquals(FlyerBoardWindowsEvent.NavigateBack, turbine.awaitItem())
                coVerify(exactly = 1) {
                    flyerManager.createFlyer(any(), any(), any(), any(), any(), any())
                }
                advanceUntilIdleAndAwaitComplete(turbine)
            }
        }

    private fun makeFlyerModel(id: String) =
        FlyerModel(
            id = FlyerId(id),
            title = "Flyer $id",
            description = "Description",
            fileUrl = null,
            status = FlyerStatus.PENDING,
            expiresAt = null,
            uploaderId = UserId("user-1"),
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
        )
}
