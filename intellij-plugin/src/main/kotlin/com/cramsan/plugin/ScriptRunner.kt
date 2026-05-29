package com.cramsan.plugin

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.SwingUtilities

/** Runs a script from the project's scripts/ directory and streams output to a Run tool window tab. */
object ScriptRunner {

    /** Resolves and executes [scriptName] with [args], streaming output to a "Dev Tools" console tab. */
    fun run(project: Project, scriptName: String, args: List<String>) {
        val root = project.basePath ?: return
        val scriptPath = "$root/scripts/$scriptName"

        val textArea = JTextArea()
        textArea.isEditable = false

        val panel = JPanel(BorderLayout())
        panel.add(JScrollPane(textArea), BorderLayout.CENTER)

        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow("Dev Tools")
            ?: toolWindowManager.registerToolWindow("Dev Tools") { }

        val content = ContentFactory.getInstance().createContent(panel, scriptName, false)
        toolWindow.contentManager.addContent(content)
        toolWindow.show()

        val cmd = GeneralCommandLine(listOf(scriptPath) + args)
            .withWorkDirectory(root)

        val handler = OSProcessHandler(cmd)
        handler.addProcessListener(object : ProcessAdapter() {
            override fun onTextAvailable(event: ProcessEvent, outputType: com.intellij.openapi.util.Key<*>) {
                SwingUtilities.invokeLater {
                    textArea.append(event.text)
                }
            }
        })
        handler.startNotify()
    }
}
