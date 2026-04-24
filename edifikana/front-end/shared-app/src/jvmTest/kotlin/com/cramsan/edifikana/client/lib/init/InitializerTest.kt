package com.cramsan.edifikana.client.lib.init

import com.cramsan.architecture.client.settings.FrontEndApplicationSettingKey
import com.cramsan.architecture.client.settings.SettingsHolder
import com.cramsan.edifikana.client.lib.BuildConfig
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.implementation.NoopAssertUtil
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CoroutineTest
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.BeforeTest
import kotlin.test.Test

class InitializerTest : CoroutineTest() {

    private lateinit var eventLogger: EventLoggerInterface
    private lateinit var authManager: AuthManager
    private lateinit var settingsHolder: SettingsHolder
    private lateinit var initializer: Initializer

    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        AssertUtil.setInstance(NoopAssertUtil())
        eventLogger = mockk(relaxed = true)
        authManager = mockk()
        settingsHolder = mockk()
        initializer = Initializer(eventLogger, authManager, settingsHolder)
    }

    @Test
    fun `seedDefaults seeds BackEndUrl from BuildConfig when unset`() = runCoroutineTest {
        // Arrange
        every { settingsHolder.getString(FrontEndApplicationSettingKey.BackEndUrl) } returns null
        every { settingsHolder.saveString(any(), any()) } just Runs
        coEvery { authManager.verifyPermissions() } returns Result.success(true)

        // Act
        initializer.startStep()

        // Assert
        verify { settingsHolder.saveString(FrontEndApplicationSettingKey.BackEndUrl, BuildConfig.DEFAULT_API_URL) }
    }

    @Test
    fun `seedDefaults does not overwrite BackEndUrl when already set`() = runCoroutineTest {
        // Arrange
        every { settingsHolder.getString(FrontEndApplicationSettingKey.BackEndUrl) } returns "http://192.168.1.100:9292"
        coEvery { authManager.verifyPermissions() } returns Result.success(true)

        // Act
        initializer.startStep()

        // Assert
        verify(exactly = 0) { settingsHolder.saveString(any(), any()) }
    }
}
