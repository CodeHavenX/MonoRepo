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
    fun `AgenticConfig round-trips through JSON with claude-api provider`() {
        val original = AgenticConfig(
            agentPoolSize = 4,
            defaultTaskTimeoutSeconds = 3600L,
            baseBranch = "main",
            claudeModel = "claude-opus-4-6",
            docsDir = ".ai/docs",
            aiProvider = AiProviderConfig.ClaudeApi(anthropicApiKeyEnvVar = "ANTHROPIC_API_KEY"),
            vcsProvider = VcsProviderConfig.GitHub(owner = "cramsan", repo = "MonoRepo"),
        )

        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<AgenticConfig>(encoded)

        assertEquals(original, decoded)
    }

    @Test
    fun `AgenticConfig round-trips through JSON with claude-cli provider`() {
        val original = AgenticConfig(
            agentPoolSize = 2,
            baseBranch = "main",
            docsDir = "docs",
            aiProvider = AiProviderConfig.ClaudeCli(cliPath = "/usr/local/bin/claude"),
            vcsProvider = VcsProviderConfig.GitHub(owner = "owner", repo = "repo"),
        )

        val encoded = json.encodeToString(original)
        val decoded = json.decodeFromString<AgenticConfig>(encoded)

        assertEquals(original, decoded)
        assertIs<AiProviderConfig.ClaudeCli>(decoded.aiProvider)
        assertEquals("/usr/local/bin/claude", (decoded.aiProvider as AiProviderConfig.ClaudeCli).cliPath)
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
    fun `AiProviderConfig ClaudeApi serializes with claude-api discriminator`() {
        val provider: AiProviderConfig = AiProviderConfig.ClaudeApi()

        val encoded = json.encodeToString(provider)

        assertTrue(
            encoded.contains("\"type\":\"claude-api\""),
            "Expected discriminator 'type':'claude-api' but got: $encoded",
        )
    }

    @Test
    fun `AiProviderConfig ClaudeCli serializes with claude-cli discriminator`() {
        val provider: AiProviderConfig = AiProviderConfig.ClaudeCli()

        val encoded = json.encodeToString(provider)

        assertTrue(
            encoded.contains("\"type\":\"claude-cli\""),
            "Expected discriminator 'type':'claude-cli' but got: $encoded",
        )
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
        assertIs<AiProviderConfig.ClaudeCli>(config.aiProvider)
        assertEquals("claude", (config.aiProvider as AiProviderConfig.ClaudeCli).cliPath)
    }

    @Test
    fun `AiProviderConfig ClaudeApi default anthropicApiKeyEnvVar is ANTHROPIC_API_KEY`() {
        val config = AiProviderConfig.ClaudeApi()
        assertEquals("ANTHROPIC_API_KEY", config.anthropicApiKeyEnvVar)
    }

    @Test
    fun `AiProviderConfig ClaudeCli default cliPath is claude`() {
        val config = AiProviderConfig.ClaudeCli()
        assertEquals("claude", config.cliPath)
    }
}
