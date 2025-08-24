package com.cramsan.discordbot

import com.cramsan.discordbot.config.AppConfig
import com.cramsan.discordbot.config.createJson
import com.cramsan.discordbot.github.GitHubService
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BasicFunctionalityTest {

    @Test
    fun `test JSON configuration creation`() {
        val json = createJson()
        assertNotNull(json)
        assertTrue(json.configuration.ignoreUnknownKeys)
    }

    @Test
    fun `test AppConfig creation with mock values`() {
        // This test validates that AppConfig can be created with valid parameters
        val config = AppConfig(
            discordToken = "mock_token",
            githubToken = null,
            serverPort = 8080,
            githubOwner = "testowner",
            githubRepo = "testrepo"
        )

        assertNotNull(config)
        assertTrue(config.discordToken.isNotEmpty())
        assertTrue(config.serverPort > 0)
    }

    @Test
    fun `test GitHubService creation`() {
        val config = AppConfig(
            discordToken = "mock_token",
            githubToken = null,
            serverPort = 8080,
            githubOwner = "testowner",
            githubRepo = "testrepo"
        )
        val json = createJson()

        val githubService = GitHubService(config, json)
        assertNotNull(githubService)

        // Clean up
        githubService.close()
    }
}
