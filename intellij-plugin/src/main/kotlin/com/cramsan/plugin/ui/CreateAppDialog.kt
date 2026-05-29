package com.cramsan.plugin.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

/** Dialog for collecting app generation parameters. */
class CreateAppDialog : DialogWrapper(true) {

    private val nameField = JTextField(20).apply { toolTipText = "lowercase, e.g. payments" }
    private val displayField = JTextField(20).apply { toolTipText = "PascalCase, e.g. Payments" }
    private val wasmCheck = JCheckBox("Include WASM", true)
    private val androidCheck = JCheckBox("Include Android", true)
    private val jvmCheck = JCheckBox("Include JVM", true)

    /** The app module name (lowercase). */
    val appName: String get() = nameField.text.trim()

    /** The display name (PascalCase). */
    val displayName: String get() = displayField.text.trim()

    /** Whether WASM platform should be included. */
    val includeWasm: Boolean get() = wasmCheck.isSelected

    /** Whether Android platform should be included. */
    val includeAndroid: Boolean get() = androidCheck.isSelected

    /** Whether JVM platform should be included. */
    val includeJvm: Boolean get() = jvmCheck.isSelected

    init {
        title = "Create App"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints().apply {
            insets = Insets(4, 4, 4, 4)
            fill = GridBagConstraints.HORIZONTAL
        }

        fun row(label: String, component: JComponent, row: Int) {
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0
            panel.add(JLabel(label), gbc)
            gbc.gridx = 1; gbc.weightx = 1.0
            panel.add(component, gbc)
        }

        fun checkRow(component: JComponent, row: Int) {
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 1.0; gbc.gridwidth = 2
            panel.add(component, gbc)
            gbc.gridwidth = 1
        }

        row("App name:", nameField, 0)
        row("Display name:", displayField, 1)
        checkRow(wasmCheck, 2)
        checkRow(androidCheck, 3)
        checkRow(jvmCheck, 4)

        return panel
    }

    override fun doValidate(): ValidationInfo? {
        if (appName.isBlank()) return ValidationInfo("App name is required", nameField)
        if (displayName.isBlank()) return ValidationInfo("Display name is required", displayField)
        return null
    }
}
