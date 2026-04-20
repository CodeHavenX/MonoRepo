package com.cramsan.agentic.coordination

import com.cramsan.agentic.core.Task
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DefaultDependencyGraphTest {

    @BeforeEach
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    private fun makeTask(id: String, vararg deps: String) = Task(
        id = id, title = id, description = id, dependencies = deps.toList()
    )

    @Test
    fun `leaf node has 0 downstream count`() {
        val tasks = listOf(
            makeTask("A", "B"),
            makeTask("B", "C"),
            makeTask("C"),
        )
        val graph = DefaultDependencyGraph(tasks)
        assertEquals(0, graph.downstreamCount("A"))
    }

    @Test
    fun `middle node has 1 downstream count`() {
        val tasks = listOf(
            makeTask("A", "B"),
            makeTask("B", "C"),
            makeTask("C"),
        )
        val graph = DefaultDependencyGraph(tasks)
        assertEquals(1, graph.downstreamCount("B"))
    }

    @Test
    fun `root node counts all transitively dependent nodes`() {
        // A -> B -> C and A -> D -> C
        val tasks = listOf(
            makeTask("A", "B", "D"),  // A depends on B and D
            makeTask("B", "C"),        // B depends on C
            makeTask("C"),             // C is a leaf
            makeTask("D", "C"),        // D depends on C
        )
        val graph = DefaultDependencyGraph(tasks)
        // C has: B, D, A as downstream (A depends on B and D, both depend on C)
        assertEquals(3, graph.downstreamCount("C"))
        // B has: A as downstream
        assertEquals(1, graph.downstreamCount("B"))
        // D has: A as downstream
        assertEquals(1, graph.downstreamCount("D"))
        // A has no dependents
        assertEquals(0, graph.downstreamCount("A"))
    }

    @Test
    fun `linear chain downstream counts`() {
        val tasks = listOf(
            makeTask("A", "B"),
            makeTask("B", "C"),
            makeTask("C", "D"),
            makeTask("D"),
        )
        val graph = DefaultDependencyGraph(tasks)
        assertEquals(3, graph.downstreamCount("D")) // D → C → B → A
        assertEquals(2, graph.downstreamCount("C")) // C → B → A
        assertEquals(1, graph.downstreamCount("B")) // B → A
        assertEquals(0, graph.downstreamCount("A")) // leaf
    }

    @Test
    fun `single node with no dependencies has 0 downstream count`() {
        val tasks = listOf(makeTask("solo"))
        val graph = DefaultDependencyGraph(tasks)
        assertEquals(0, graph.downstreamCount("solo"))
    }
}
