package com.cramsan.plugin

import com.cramsan.plugin.ui.CreateAppDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

/** IDE action that opens the Create App dialog and invokes create-app.sh. */
class CreateAppAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val dialog = CreateAppDialog()
        if (!dialog.showAndGet()) return

        val args = mutableListOf(
            "--name", dialog.appName,
            "--display", dialog.displayName,
        )
        if (!dialog.includeWasm) args += "--no-wasm"
        if (!dialog.includeAndroid) args += "--no-android"
        if (!dialog.includeJvm) args += "--no-jvm"

        ScriptRunner.run(project, "create-app.sh", args)
    }
}
