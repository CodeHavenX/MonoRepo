package com.cramsan.agentic.core

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AgenticConfigTest {

    private val json = Json { prettyPrint = false }

    @Test
    fun `AgenticConfig round-trips through JSON`() {
        val original = AgenticConfig(
            agentPoolSize = 4,
            defaultTaskTimeoutSeconds = 3600L,
            baseBranch = "main",
            claudeModel = "claude-opus-4-6",
            docsDir = ".ai/docs",
            anthropicApiKeyEnvVar = "ANTHROPIC_API_KEY",
            vcsProvider = VcsProviderConfig.GitHub(owner = "cramsan", repo = "MonoRepo"),
        )

        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<AgenticConfig>(encoded)

        assertEquals(original, decoded)
    }

    @Test
    fun `VcsProviderConfig GitHub serializes with correct SerialName discriminator`() {
        val provider: VcsProviderConfig = VcsProviderConfig.GitHub(owner = "acme", repo = "project")

        val encoded = json.encodeToString(provider)

        assertTrue(
            encoded.contains("\"type\":\"github\""),
            "Expected discriminator 'type':'github' in JSON but got: $encoded",
        )
    }

    @Test
    fun `VcsProviderConfig GitHub round-trips through JSON as sealed class`() {
        val original: VcsProviderConfig = VcsProviderConfig.GitHub(owner = "acme", repo = "project")

        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<VcsProviderConfig>(encoded)

        assertIs<VcsProviderConfig.GitHub>(decoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `AgenticConfig default values are applied correctly`() {
        val config = AgenticConfig(
            agentPoolSize = 2,
            baseBranch = "main",
            docsDir = "docs",
            vcsProvider = VcsProviderConfig.GitHub(owner = "owner", repo = "repo"),
        )

        assertEquals(3600L, config.defaultTaskTimeoutSeconds)
        assertEquals("claude-opus-4-6", config.claudeModel)
        assertEquals("ANTHROPIC_API_KEY", config.anthropicApiKeyEnvVar)
    }
}
