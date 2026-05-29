package com.cramsan.plugin

import com.cramsan.plugin.ui.CreateComponentDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/** IDE action that opens the Create Component dialog and invokes the matching generator script. */
class CreateComponentAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val dialog = CreateComponentDialog()
        if (!dialog.showAndGet()) return

        val type = dialog.componentType
        val name = dialog.componentName

        val (scriptName, args) = when (type) {
            "feature" -> "create-feature.sh" to mutableListOf("--name", name, "--parent", dialog.parentDir)
            else -> {
                val a = mutableListOf("--name", name, "--app", dialog.appName)
                if (type == "datastore") a += listOf("--provider", dialog.provider)
                "create-$type.sh" to a
            }
        }

        ScriptRunner.run(project, scriptName, args)
    }
}
