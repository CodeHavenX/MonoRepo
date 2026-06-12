package com.cramsan.flyerboard.client.lib.features.main.flyer_edit

import app.cash.turbine.turbineScope
import com.cramsan.flyerboard.client.lib.features.main.flyer_edit.FlyerEditUIState.Editing
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
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FlyerEditViewModelTest : CoroutineTest() {
    private lateinit var flyerManager: FlyerManager
    private lateinit var viewModel: FlyerEditViewModel
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
            FlyerEditViewModel(
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
    fun `onFileSelected updates selectedFileName in UIState`() =
        runCoroutineTest {
            val flyerId = "flyer-123"
            val bytes = byteArrayOf(10, 20, 30)
            coEvery { flyerManager.getFlyer(FlyerId(flyerId)) } returns Result.success(makeFlyerModel(flyerId))

            viewModel.loadFlyer(flyerId)
            viewModel.onFileSelected(bytes, "photo.jpg", "image/jpeg")

            val state = assertIs<Editing>(viewModel.uiState.value)
            assertEquals("photo.jpg", state.selectedFileName)
        }

    @Test
    fun `saveFlyer after onFileSelected includes file data in updateFlyer call`() =
        runCoroutineTest {
            turbineScope {
                val turbine = windowEventBus.events.testIn(backgroundScope)
                val flyerId = "flyer-123"
                val bytes = byteArrayOf(1, 2, 3)
                val updatedFlyer = makeFlyerModel(flyerId)
                coEvery { flyerManager.getFlyer(FlyerId(flyerId)) } returns Result.success(updatedFlyer)
                coEvery {
                    flyerManager.updateFlyer(
                        flyerId = any(),
                        title = any(),
                        description = any(),
                        expiresAt = any(),
                        fileBytes = any(),
                        fileName = any(),
                        mimeType = any(),
                    )
                } returns Result.success(updatedFlyer)

                viewModel.loadFlyer(flyerId)
                viewModel.onFileSelected(bytes, "photo.jpg", "image/jpeg")
                viewModel.saveFlyer(flyerId)

                coVerify {
                    flyerManager.updateFlyer(
                        flyerId = FlyerId(flyerId),
                        title = "Flyer $flyerId",
                        description = "Description",
                        expiresAt = null,
                        fileBytes = bytes,
                        fileName = "photo.jpg",
                        mimeType = "image/jpeg",
                    )
                }
                assertEquals(FlyerBoardWindowsEvent.NavigateBack, turbine.awaitItem())
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
