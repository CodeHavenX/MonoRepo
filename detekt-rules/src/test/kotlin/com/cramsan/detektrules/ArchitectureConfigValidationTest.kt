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
              ArchitectureNamingRule:
                active: true
                namingSuffixes: []
            """.trimIndent().reader(),
        )

    /*
    @Test
    fun `ArchitectureNamingRule reads namingSuffixes from production YAML config`() {
        val configFile = File("../config/detekt-architecture-config.yml")
        assertTrue(configFile.exists(), "Config file not found at ${configFile.absolutePath}")

        val yamlConfig = YamlConfig.load(configFile.reader())
        val ruleConfig = yamlConfig.subConfig("architecture").subConfig("ArchitectureNamingRule")
        val rule = ArchitectureNamingRule(ruleConfig)
        val env = createEnvironment()

        val findings = rule.lintWithContext(env, "class MyController")
        assertEquals(
            1,
            findings.size,
            "Expected ArchitectureNamingRule to find 1 violation with production config, " +
                "but found ${findings.size}. Check that namingSuffixes is loaded from the YAML.",
        )
    }
     */

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
