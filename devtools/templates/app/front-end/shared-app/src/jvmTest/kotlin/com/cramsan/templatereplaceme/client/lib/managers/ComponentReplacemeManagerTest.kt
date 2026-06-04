package com.cramsan.templatereplaceme.client.lib.managers

import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.templatereplaceme.client.lib.models.ComponentReplacemeModel
import com.cramsan.templatereplaceme.client.lib.service.ComponentReplacemeService
import com.cramsan.templatereplaceme.lib.model.ComponentReplacemeId
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for [ComponentReplacemeManager].
 */
class ComponentReplacemeManagerTest : CoroutineTest() {

    private lateinit var manager: ComponentReplacemeManager
    private lateinit var componentreplacemeService: ComponentReplacemeService
    private lateinit var dependencies: ManagerDependencies

    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        componentreplacemeService = mockk()
        dependencies = mockk(relaxed = true)
        every { dependencies.appScope } returns testCoroutineScope
        every { dependencies.dispatcherProvider } returns UnifiedDispatcherProvider(testCoroutineDispatcher)
        manager = ComponentReplacemeManager(dependencies, componentreplacemeService)
    }

    @Test
    fun `create returns success when service succeeds`() = runCoroutineTest {
        val model = ComponentReplacemeModel(id = ComponentReplacemeId("id-1"))
        coEvery { componentreplacemeService.create("id-1") } returns Result.success(model)

        val result = manager.create("id-1")

        assertTrue(result.isSuccess)
        assertEquals(model, result.getOrNull())
        coVerify { componentreplacemeService.create("id-1") }
    }
}
