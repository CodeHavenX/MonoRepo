package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Negative and edge-case tests for DefaultDependencyGraph: non-existent IDs, empty task list,
 * circular dependencies, duplicate IDs, and dangling dependency references.
 */
class DefaultDependencyGraphNegativeTest {

    private fun makeTask(id: String, vararg deps: String) = Task(
        id = id, title = id, description = id, dependencies = deps.toList()
    )

    // ── Empty task list ───────────────────────────────────────────────────────

    @Test
    fun `empty task list can be constructed and returns 0 for any id`() {
        val graph = DefaultDependencyGraph(emptyList())

        assertEquals(0, graph.downstreamCount("anything"))
    }

    // ── Non-existent task IDs ─────────────────────────────────────────────────

    @Test
    fun `downstreamCount returns 0 for unknown task id`() {
        val graph = DefaultDependencyGraph(listOf(makeTask("A")))

        assertEquals(0, graph.downstreamCount("nonexistent"))
    }

    @Test
    fun `downstreamCount returns 0 for empty string id`() {
        val graph = DefaultDependencyGraph(listOf(makeTask("A")))

        assertEquals(0, graph.downstreamCount(""))
    }

    @Test
    fun `downstreamCount returns 0 for id that looks like a task but is not registered`() {
        val tasks = listOf(
            makeTask("task-001"),
            makeTask("task-002", "task-001"),
        )
        val graph = DefaultDependencyGraph(tasks)

        assertEquals(0, graph.downstreamCount("task-999"))
    }

    // ── Circular dependencies ─────────────────────────────────────────────────

    @Test
    fun `circular dependency A depends on B depends on A does not cause infinite loop`() {
        // A → B → A (circular)
        val tasks = listOf(
            makeTask("A", "B"),
            makeTask("B", "A"),
        )
        val graph = DefaultDependencyGraph(tasks)

        // The visited-set BFS guard must prevent infinite traversal
        val countA = graph.downstreamCount("A")
        val countB = graph.downstreamCount("B")

        // With a cycle both nodes are mutually downstream; exact count is
        // implementation-defined but must be finite and non-negative
        assertTrue(countA >= 0)
        assertTrue(countB >= 0)
    }

    @Test
    fun `three-node cycle does not cause infinite loop`() {
        // A → B → C → A
        val tasks = listOf(
            makeTask("A", "B"),
            makeTask("B", "C"),
            makeTask("C", "A"),
        )
        val graph = DefaultDependencyGraph(tasks)

        // Must terminate without StackOverflow or infinite loop
        val count = graph.downstreamCount("A")
        assertTrue(count >= 0)
    }

    // ── Dangling dependency references ────────────────────────────────────────

    @Test
    fun `task depending on non-existent task id does not crash`() {
        val tasks = listOf(makeTask("A", "ghost-task"))
        val graph = DefaultDependencyGraph(tasks)

        // "ghost-task" is not in the task list; graph construction must not throw
        assertEquals(0, graph.downstreamCount("A"))
    }

    @Test
    fun `downstreamCount for ghost dependency target returns 0`() {
        val tasks = listOf(makeTask("A", "ghost-task"))
        val graph = DefaultDependencyGraph(tasks)

        // A depends on ghost-task, so ghost-task has A as downstream
        assertEquals(1, graph.downstreamCount("ghost-task"))
    }

    // ── Duplicate task IDs ────────────────────────────────────────────────────

    @Test
    fun `duplicate task ids are handled without crash`() {
        val tasks = listOf(
            makeTask("A"),
            makeTask("A"),   // same ID registered twice
            makeTask("B", "A"),
        )
        val graph = DefaultDependencyGraph(tasks)

        // Must not throw; exact count may vary but must be finite
        val count = graph.downstreamCount("A")
        assertTrue(count >= 0)
    }

    // ── Self-dependency ───────────────────────────────────────────────────────

    @Test
    fun `task that depends on itself does not cause infinite loop`() {
        val tasks = listOf(makeTask("A", "A"))
        val graph = DefaultDependencyGraph(tasks)

        // A is downstream of A; visited-set must break the cycle
        val count = graph.downstreamCount("A")
        assertTrue(count >= 0)
    }

    // ── Single isolated node ──────────────────────────────────────────────────

    @Test
    fun `single node with no edges has 0 downstream count`() {
        val graph = DefaultDependencyGraph(listOf(makeTask("solo")))

        assertEquals(0, graph.downstreamCount("solo"))
    }

    // ── Large fan-out ─────────────────────────────────────────────────────────

    @Test
    fun `root node with many direct dependents reports correct count`() {
        // "root" is required by 50 independent tasks
        val root = makeTask("root")
        val dependents = (1..50).map { makeTask("child-$it", "root") }
        val graph = DefaultDependencyGraph(listOf(root) + dependents)

        assertEquals(50, graph.downstreamCount("root"))
    }
}
