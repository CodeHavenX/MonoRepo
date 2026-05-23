package com.cramsan.framework.sample.shared.features.main.remoteconfig

import app.cash.turbine.turbineScope
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.remoteconfig.RemoteConfig
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import com.cramsan.framework.sample.shared.stubs.SampleRemoteConfig
import com.cramsan.framework.sample.shared.stubs.SampleRemoteConfigPayload
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import io.mockk.coEvery
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RemoteConfigViewModelTest : CoroutineTest() {

    private lateinit var remoteConfig: RemoteConfig<SampleRemoteConfigPayload>
    private lateinit var viewModel: RemoteConfigViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventBus: EventBus<ApplicationEvent>

    @BeforeTest
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        remoteConfig = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventBus = EventBus()
        windowEventBus = EventBus()
        viewModel = RemoteConfigViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventBus,
            ),
            remoteConfig = remoteConfig,
        )
    }

    @Test
    fun `initial UIState is correct`() = runCoroutineTest {
        assertEquals(RemoteConfigUIState.Initial, viewModel.uiState.value)
        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isPayloadReady)
        assertEquals("No action taken yet", viewModel.uiState.value.lastAction)
        assertEquals("No payload", viewModel.uiState.value.payloadInfo)
    }

    @Test
    fun `checkIsPayloadReady reflects false when payload not downloaded`() = runCoroutineTest {
        every { remoteConfig.isConfigPayloadReady() } returns false

        viewModel.checkIsPayloadReady()

        assertFalse(viewModel.uiState.value.isPayloadReady)
        assertEquals("isConfigPayloadReady() → false", viewModel.uiState.value.lastAction)
    }

    @Test
    fun `checkIsPayloadReady reflects true after payload downloaded`() = runCoroutineTest {
        every { remoteConfig.isConfigPayloadReady() } returns true

        viewModel.checkIsPayloadReady()

        assertTrue(viewModel.uiState.value.isPayloadReady)
        assertEquals("isConfigPayloadReady() → true", viewModel.uiState.value.lastAction)
    }

    @Test
    fun `downloadPayload on success sets isPayloadReady and updates lastAction`() = runCoroutineTest {
        coEvery { remoteConfig.downloadConfigPayload() } returns true
        every { remoteConfig.isConfigPayloadReady() } returns true

        viewModel.downloadPayload()

        assertTrue(viewModel.uiState.value.isPayloadReady)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("downloadConfigPayload() → success=true", viewModel.uiState.value.lastAction)
    }

    @Test
    fun `downloadAsync calls downloadConfigPayloadAsync`() = runCoroutineTest {
        justRun { remoteConfig.downloadConfigPayloadAsync() }
        every { remoteConfig.isConfigPayloadReady() } returns false

        viewModel.downloadAsync()

        verify { remoteConfig.downloadConfigPayloadAsync() }
        assertEquals("downloadConfigPayloadAsync() called", viewModel.uiState.value.lastAction)
    }

    @Test
    fun `getPayloadOrNull with null payload shows null in UI`() = runCoroutineTest {
        every { remoteConfig.getConfigPayloadOrNull() } returns null

        viewModel.getPayloadOrNull()

        assertEquals("null", viewModel.uiState.value.payloadInfo)
        assertEquals("getConfigPayloadOrNull() → null", viewModel.uiState.value.lastAction)
    }

    @Test
    fun `getPayloadOrNull with payload shows payload fields`() = runCoroutineTest {
        val payload = SampleRemoteConfigPayload(featureEnabled = true, configValue = "test-value")
        every { remoteConfig.getConfigPayloadOrNull() } returns payload

        viewModel.getPayloadOrNull()

        assertEquals("featureEnabled=true, configValue=test-value", viewModel.uiState.value.payloadInfo)
    }

    @Test
    fun `getPayloadOrDefault shows default payload fields`() = runCoroutineTest {
        val payload = SampleRemoteConfigPayload(featureEnabled = false, configValue = "default")
        every { remoteConfig.getConfigPayloadOrDefault() } returns payload

        viewModel.getPayloadOrDefault()

        assertEquals("featureEnabled=false, configValue=default", viewModel.uiState.value.payloadInfo)
    }

    @Test
    fun `navigateBack emits NavigateBack event`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateBack()

            assertEquals(SampleWindowEvent.NavigateBack, turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }
}
