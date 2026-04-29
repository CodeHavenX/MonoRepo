package com.cramsan.detektrules

import dev.detekt.api.Notification
import dev.detekt.core.config.YamlConfig
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Validates that detekt-architecture-config.yml only contains keys that are recognized by the
 * architecture rule set.  A typo or stale key in the YAML would otherwise silently pass all rules
 * (because detekt falls back to the rule's defaultValue when a key is missing).
 */
class ArchitectureConfigValidationTest {
    // Minimal baseline representing every valid key in the architecture rule set.
    // If new config properties are added to AnnotationCallerRestrictionRule, add them here too.
    private val baseline: YamlConfig =
        YamlConfig.load(
            """
            architecture:
              excludes: []
              AnnotationCallerRestrictionRule:
                active: true
                layers: []
            """.trimIndent().reader(),
        )

    @Test
    fun `detekt-architecture-config yml contains only valid keys`() {
        val configFile = File("../config/detekt-architecture-config.yml")
        assertTrue(configFile.exists(), "Config file not found at ${configFile.absolutePath}")

        val userConfig = YamlConfig.load(configFile.reader())
        val errors =
            userConfig
                .validate(baseline, emptySet())
                .filter { it.level == Notification.Level.Error }

        assertTrue(errors.isEmpty()) {
            "detekt-architecture-config.yml contains invalid keys:\n" +
                errors.joinToString("\n") { "  - ${it.message}" }
        }
    }
}
