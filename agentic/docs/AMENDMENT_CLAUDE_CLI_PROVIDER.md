# Amendment: Claude CLI AI Provider

> Status: Approved — pending implementation
> Depends on: AMENDMENT_AI_PROVIDER_ABSTRACTION.md
> Amends: TECH_DESIGN.md §2, §3

---

## Motivation

The `ClaudeAiProvider` (HTTP API) requires an `ANTHROPIC_API_KEY` environment variable and makes direct HTTP calls to `api.anthropic.com`. A second provider that shells out to the `claude` CLI is useful for environments where:

- The `claude` CLI is already authenticated (no API key management required).
- Local development where the CLI is the primary way to interact with Claude.
- Lightweight reviewer and validation workloads that do not require tool use.

The pattern mirrors `GitHubVcsProvider`, which shells out to `gh` rather than calling the GitHub REST API directly.

---

## New File: `ai/claude/ClaudeCliAiProvider.kt`

Lives alongside `ClaudeAiProvider` in the same sub-package.

### Implementation

Uses `ShellRunner` to invoke the `claude` CLI in non-interactive print mode:

```bash
claude --print --model <model> [--system-prompt <system>] "<user message>"
```

The provider builds the full conversation as a single formatted prompt passed via stdin (or a temp file) and captures stdout as the response text.

### Capabilities and Limitations

| Capability | Supported |
|------------|-----------|
| Plain text chat (no tools) | ✅ |
| System prompts | ✅ |
| Multi-turn conversation (collapsed to single prompt) | ✅ |
| Tool use / structured tool-call responses | ❌ |
| Streaming | ❌ |

**Tool use is not supported.** When `tools` is non-empty, `ClaudeCliAiProvider.chat()` throws `UnsupportedOperationException`. This means `ClaudeCliAiProvider` **cannot** back `DefaultAgentSession` (which requires tool use), but it **can** back:

- `DefaultValidationService` — calls `chat()` with `tools = emptyList()`
- `ClaudeReviewerAgent` — calls `chat()` with `tools = emptyList()`

### Response Format

The CLI response is plain text. The provider wraps it in a minimal `AiResponse`:

```kotlin
AiResponse(
    id = "cli-${System.currentTimeMillis()}",
    content = listOf(AiContentBlock.Text(stdout.trim())),
    stopReason = "end_turn",
)
```

### Error Handling

- Non-zero exit code → throws `AiProviderException(message, exitCode)` (new shared exception type in `ai/`).
- Uses `ShellRunner` retry logic (up to 3 retries on transient failures).

---

## Configuration

Add `ClaudeCliProviderConfig` to the `AiProviderConfig` sealed class in `AgenticConfig`:

```kotlin
@Serializable
sealed class AiProviderConfig {
    @Serializable
    @SerialName("claude-api")
    data class ClaudeApi(
        val anthropicApiKeyEnvVar: String = "ANTHROPIC_API_KEY",
    ) : AiProviderConfig()

    @Serializable
    @SerialName("claude-cli")
    data class ClaudeCli(
        val cliPath: String = "claude",   // path to the claude binary
    ) : AiProviderConfig()
}
```

`AgenticConfig` gains an `aiProvider: AiProviderConfig` field (replacing the top-level `anthropicApiKeyEnvVar`).

---

## Files Changed

| File | Action | Reason |
|------|--------|--------|
| `ai/claude/ClaudeCliAiProvider.kt` | **Create** | New CLI-backed `AiProvider` implementation |
| `ai/AiProvider.kt` | **Modify** | Add `AiProviderException` to the file |
| `core/AgenticConfig.kt` | **Modify** | Replace `anthropicApiKeyEnvVar: String` with `aiProvider: AiProviderConfig`; add `AiProviderConfig` sealed class |
| `app/AgenticModule.kt` | **Modify** | Select `ClaudeAiProvider` or `ClaudeCliAiProvider` based on `config.aiProvider` |

---

## Tests

- `ai/claude/ClaudeCliAiProviderTest.kt` — **Create**
  - Mock `ShellRunner` to return canned stdout.
  - Test plain text response is wrapped in `AiResponse` correctly.
  - Test that non-zero exit code throws `AiProviderException`.
  - Test that `chat()` with non-empty `tools` throws `UnsupportedOperationException`.

---

## Non-Goals

- This provider does **not** attempt to simulate tool use via prompt engineering. Tool use requires the structured API.
- The `AgentSession` provider selection (API vs CLI) is not configurable at the task level — a single `AiProvider` binding is used for the entire run. Use `ClaudeAiProvider` (HTTP) when running full autonomous agent tasks.
