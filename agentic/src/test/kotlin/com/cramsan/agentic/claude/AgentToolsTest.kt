package com.cramsan.agentic.claude

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AgentToolsTest {

    @Test
    fun `all tools have non-blank name, description, and inputSchema`() {
        for (tool in ALL_AGENT_TOOLS) {
            assertTrue(tool.name.isNotBlank(), "Tool name is blank: $tool")
            assertTrue(tool.description.isNotBlank(), "Tool description is blank for ${tool.name}")
            assertTrue(tool.inputSchema.isNotEmpty(), "Tool inputSchema is empty for ${tool.name}")
        }
    }

    @Test
    fun `ALL_AGENT_TOOLS contains exactly 9 tools`() {
        assertEquals(9, ALL_AGENT_TOOLS.size)
    }

    @Test
    fun `tool names match expected constants`() {
        val names = ALL_AGENT_TOOLS.map { it.name }.toSet()
        assertTrue("read_file" in names)
        assertTrue("write_file" in names)
        assertTrue("delete_file" in names)
        assertTrue("run_command" in names)
        assertTrue("list_files" in names)
        assertTrue("task_complete" in names)
        assertTrue("task_failed" in names)
        assertTrue("propose_amendment" in names)
        assertTrue("split_task" in names)
    }

    @Test
    fun `READ_FILE_TOOL has path property in schema`() {
        val schemaStr = READ_FILE_TOOL.inputSchema.toString()
        assertNotNull(schemaStr)
        assertTrue(schemaStr.contains("path"))
    }

    @Test
    fun `TASK_COMPLETE_TOOL has prTitle and prBody properties`() {
        val schemaStr = READ_FILE_TOOL.inputSchema.toString()
        assertNotNull(schemaStr)
        val taskCompleteSchema = TASK_COMPLETE_TOOL.inputSchema.toString()
        assertTrue(taskCompleteSchema.contains("prTitle"))
        assertTrue(taskCompleteSchema.contains("prBody"))
    }
}
