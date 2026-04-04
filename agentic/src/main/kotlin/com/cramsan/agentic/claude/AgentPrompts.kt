package com.cramsan.agentic.claude

import com.cramsan.agentic.ai.AiMessage
import com.cramsan.agentic.core.PullRequestComment
import com.cramsan.agentic.core.Task

fun buildTaskStartPrompt(task: Task, documents: List<Pair<String, String>>): String {
    val docsSection = documents.joinToString("\n\n") { (name, content) ->
        "## Document: $name\n\n$content"
    }
    return """
You are an autonomous software engineering agent. Your goal is to complete the following task by writing code in your assigned worktree.

## Your Task

**ID:** ${task.id}
**Title:** ${task.title}
**Description:** ${task.description}

## Reference Documents

$docsSection

## Instructions

1. Read the existing code to understand the codebase.
2. Implement the required changes in your worktree.
3. Run verification (tests, build) to ensure your changes are correct.
4. When all checks pass, stage and commit your changes (`git add -A && git commit -m "..."`) and push the branch (`git push -u origin HEAD`), then use the `task_complete` tool to open a Pull Request.
5. If you encounter a blocker you cannot resolve, use `task_failed` to report it.
6. If you need to propose changes to the input documents, use `propose_amendment`.
7. If the task is larger than expected, use `split_task` to deliver partial value and queue remaining work.

Work autonomously. Do not ask for clarification — make reasonable decisions and document them in the PR description.
""".trimIndent()
}

fun buildPrOpenedPrompt(task: Task, gitDiff: String, prDescription: String): String {
    return """
You previously completed task **${task.id}: ${task.title}** and opened a Pull Request.

## Pull Request Summary

$prDescription

## Git Diff

```diff
$gitDiff
```

The PR is awaiting review. Stand by for feedback.
""".trimIndent()
}

fun buildChangesRequestedPrompt(task: Task, gitDiff: String, reviewComments: List<PullRequestComment>): String {
    val commentsSection = reviewComments.joinToString("\n\n") { comment ->
        "**${comment.author}** (${comment.createdAtEpochMs}):\n${comment.body}"
    }
    return """
The reviewer has requested changes on task **${task.id}: ${task.title}**.

## What You Built (Git Diff)

```diff
$gitDiff
```

## Reviewer Comments

$commentsSection

## Instructions

1. Address all reviewer feedback in your worktree.
2. Run verification (tests, build) to ensure changes are correct.
3. When ready, stage and commit your changes (`git add -A && git commit -m "..."`), push the branch (`git push`), then use `task_complete` to update the Pull Request.

Work autonomously. Address each comment thoughtfully.
""".trimIndent()
}

fun buildResumeFromWorktreePrompt(task: Task, gitDiff: String): String {
    return """
You are resuming work on task **${task.id}: ${task.title}**.

Your previous session was interrupted. Here is the current state of your work:

## Changes Made So Far (Git Diff)

```diff
$gitDiff
```

## Instructions

1. Review what has been done so far.
2. Continue implementing the remaining work.
3. Run verification (tests, build) when done.
4. Use `task_complete` to open a Pull Request when ready.

Work autonomously. Pick up where you left off.
""".trimIndent()
}
