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
claude --print --model <model> "<collapsed prompt>"
```

The provider collapses the full conversation history into a single plain-text prompt that is
passed as the final positional argument. For multi-turn conversations, each turn is
rendered as a labelled section so the model retains role attribution:

```
[System]
<system prompt text>

[User]
<turn 1 user content>

[Assistant]
<turn 1 assistant content>

[User]
<turn 2 user content>
```

The collapsed string is passed directly as the CLI argument (quoted). The provider captures
stdout as the response text and discards stderr unless the exit code is non-zero.

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

## Configuration Migration

`AgenticConfig` previously had a top-level `anthropicApiKeyEnvVar: String` field.
This amendment replaces it with `aiProvider: AiProviderConfig`.

**Existing `config.json` files must be migrated before upgrading.** Replace:

```json
{
  "anthropicApiKeyEnvVar": "ANTHROPIC_API_KEY",
  ...
}
```

with the equivalent provider block:

```json
{
  "aiProvider": {
    "type": "claude-api",
    "anthropicApiKeyEnvVar": "ANTHROPIC_API_KEY"
  },
  ...
}
```

There is no automatic migration. The CLI will fail at config-load time if
`anthropicApiKeyEnvVar` is present at the top level; the error message directs
the user to update the configuration manually.

---

## Startup Validation

When the orchestrator or any command that requires an `AiProvider` starts, it
validates that the configured provider is available **before** doing any other work:

- **`claude-api`** — checks that the environment variable named in
  `anthropicApiKeyEnvVar` is set and non-empty.
- **`claude-cli`** — shells out to `<cliPath> --version` and verifies the exit
  code is zero.

If validation fails, the CLI prints a clear error and exits with code 1. No
tasks are started and no state is modified.

Additionally, the `start` and `resume` commands check provider compatibility against
the registered component types. If the resolved `AiProvider` is `ClaudeCliAiProvider`
(which does not support tool use), the commands verify that no agent-session components
that require tool use are bound. If an incompatible component is found, the CLI exits
with a descriptive error before launching any agents.

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
