# Amendment: AI Provider Abstraction Layer

> Status: Approved — pending implementation
> Amends: TECH_DESIGN.md §2, §3

---

## Motivation

The current implementation is tightly coupled to Anthropic's Claude API. References to `ClaudeClient`, `ClaudeMessage`, `ClaudeTool`, `ClaudeResponse`, and `ClaudeContentBlock` leak from the `claude/` package into `input/`, `reviewer/`, and `app/` packages. Swapping AI providers would require changes across multiple unrelated packages.

This amendment introduces an `ai/` abstraction layer that mirrors the existing `vcs/` provider pattern, so the orchestrator, agent session, validation service, and reviewer agents depend only on a generic interface.

---

## New Package: `ai/`

```
ai/
  AiProvider.kt           # Generic interface + shared types
  claude/
    ClaudeAiProvider.kt   # Anthropic implementation of AiProvider
```

### `ai/AiProvider.kt`

Defines all provider-agnostic types and the interface:

```kotlin
data class AiMessage(val role: String, val content: String)

data class AiTool(val name: String, val description: String, val inputSchema: JsonObject)

sealed class AiContentBlock {
    data class Text(val text: String) : AiContentBlock()
    data class ToolCall(val id: String, val name: String, val input: JsonObject) : AiContentBlock()
}

data class AiResponse(
    val id: String,
    val content: List<AiContentBlock>,
    val stopReason: String?,
)

interface AiProvider {
    suspend fun chat(
        model: String,
        systemPrompt: String,
        messages: List<AiMessage>,
        tools: List<AiTool>,
    ): AiResponse
}
```

### `ai/claude/ClaudeAiProvider.kt`

Implements `AiProvider` using the Anthropic Messages API (Ktor HTTP). Internally maps between the generic types above and Claude's wire format (`ClaudeMessage`, `ClaudeTool`, `ClaudeResponse`, `ClaudeContentBlock` from `core/`). Those `@Serializable` core models are retained as internal serialization details — they are no longer part of any public interface.

Carries over the retry logic from `KtorClaudeClient` (up to 3 retries = 4 total attempts, exponential backoff on 429/5xx). This is consistent with the "retry up to 3 times" wording in TECH_DESIGN.md §11.

---

## Files Changed

| File | Action | Reason |
|------|--------|--------|
| `ai/AiProvider.kt` | **Create** | Generic interface and shared types |
| `ai/claude/ClaudeAiProvider.kt` | **Create** | Anthropic implementation |
| `claude/ClaudeClient.kt` | **Delete** | Replaced by `AiProvider` |
| `claude/KtorClaudeClient.kt` | **Delete** | Merged into `ClaudeAiProvider` |
| `claude/AgentTools.kt` | **Modify** | `ClaudeTool` → `AiTool` |
| `claude/AgentPrompts.kt` | **Modify** | `ClaudeMessage` → `AiMessage` |
| `claude/DefaultAgentSession.kt` | **Modify** | `ClaudeClient` → `AiProvider`; generic types throughout |
| `input/DefaultValidationService.kt` | **Modify** | `ClaudeClient` → `AiProvider`; `ClaudeMessage/ContentBlock` → `AiMessage/AiContentBlock` |
| `reviewer/claude/ClaudeReviewerAgent.kt` | **Modify** | `ClaudeClient` → `AiProvider`; generic types |
| `app/AgenticModule.kt` | **Modify** | Bind `AiProvider` to `ClaudeAiProvider`; remove `ClaudeClient` binding |
| `core/ClaudeModels.kt` | **Keep** | Wire-format serialization models; internal to `ai/claude/` use only |

---

## Tests

- `KtorClaudeClientTest.kt` → **Rename/move** to `ClaudeAiProviderTest.kt` in `ai/claude/`; update type references
- All other tests that mock `ClaudeClient` → update to mock `AiProvider`
- No new test files required; coverage is preserved by updating existing tests

---

## Non-Goals

- This amendment does **not** add an OpenAI or Gemini implementation. It only creates the interface and moves Claude behind it.
- The internal `ClaudeModels.kt` wire-format types are not renamed — they remain as serialization models private to the Claude implementation.
- The `claude/` package is retained for `DefaultAgentSession`, `AgentTools`, and `AgentPrompts` since these contain agent behavior logic that is currently expressed in terms that are compatible with any tool-use capable LLM.
