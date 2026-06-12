package com.cramsan.plugin.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

/** Dialog for collecting component generation parameters. */
class CreateComponentDialog : DialogWrapper(true) {

    private val typeCombo = JComboBox(arrayOf("controller", "service", "datastore", "manager", "frontend-service", "api", "feature"))
    private val nameField = JTextField(20).apply { toolTipText = "PascalCase, e.g. Payment" }
    private val appField = JTextField(30).apply { toolTipText = "e.g. edifikana (not used for feature type)" }
    private val providerField = JTextField(20).apply { toolTipText = "e.g. Supabase (required for datastore)" }
    private val parentField = JTextField(60).apply {
        toolTipText = "e.g. edifikana/front-end/app/src/commonMain/kotlin/com/cramsan/edifikana/client/lib/features"
    }

    /** The selected component type. */
    val componentType: String get() = typeCombo.selectedItem as String

    /** The component name in PascalCase. */
    val componentName: String get() = nameField.text.trim()

    /** The app module name (not used for feature type). */
    val appName: String get() = appField.text.trim()

    /** The provider name (relevant only for datastore type). */
    val provider: String get() = providerField.text.trim()

    /** The parent directory path (relevant only for feature type). */
    val parentDir: String get() = parentField.text.trim()

    init {
        title = "Create Component"
        init()
        typeCombo.addActionListener { updateFieldStates() }
        updateFieldStates()
    }

    private fun updateFieldStates() {
        val type = typeCombo.selectedItem as String
        val isFeature = type == "feature"
        val isDatastore = type == "datastore"
        appField.isEnabled = !isFeature
        providerField.isEnabled = isDatastore
        parentField.isEnabled = isFeature
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

        row("Component type:", typeCombo, 0)
        row("Name (PascalCase):", nameField, 1)
        row("App:", appField, 2)
        row("Provider:", providerField, 3)
        row("Parent folder:", parentField, 4)

        return panel
    }

    override fun doValidate(): ValidationInfo? {
        if (componentName.isBlank()) return ValidationInfo("Name is required", nameField)
        val type = componentType
        if (type == "feature") {
            if (parentDir.isBlank()) return ValidationInfo("Parent folder is required for feature", parentField)
        } else {
            if (appName.isBlank()) return ValidationInfo("App is required", appField)
            if (type == "datastore" && provider.isBlank()) {
                return ValidationInfo("Provider is required for datastore", providerField)
            }
        }
        return null
    }
}
