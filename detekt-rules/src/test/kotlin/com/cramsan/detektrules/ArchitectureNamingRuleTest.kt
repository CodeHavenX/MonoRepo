package com.cramsan.detektrules

import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ArchitectureNamingRuleTest {
    private val testConfig =
        TestConfig(
            "namingSuffixes" to
                listOf(
                    "ViewModel:FrontendViewModel",
                    "Manager:FrontendManager",
                    "Service:FrontendService,BackendService",
                    "Controller:BackendController",
                    "Datastore:BackendDatastore",
                ),
        )

    private fun rule() = ArchitectureNamingRule(testConfig)

    // ── Name → Annotation: ViewModel ─────────────────────────────────────────

    @Test
    fun `class named XxxViewModel with FrontendViewModel annotation - no violation`() {
        val code =
            """
            annotation class FrontendViewModel
            @FrontendViewModel class MyViewModel
            """.trimIndent()
        assertTrue(rule().lint(code).isEmpty())
    }

    @Test
    fun `class named XxxViewModel without annotation - violation`() {
        val code =
            """
            class MyViewModel
            """.trimIndent()
        assertEquals(1, rule().lint(code).size)
    }

    // ── Name → Annotation: Manager ────────────────────────────────────────────

    @Test
    fun `class named XxxManager with FrontendManager annotation - no violation`() {
        val code =
            """
            annotation class FrontendManager
            @FrontendManager class MyManager
            """.trimIndent()
        assertTrue(rule().lint(code).isEmpty())
    }

    @Test
    fun `class named XxxManager without annotation - violation`() {
        val code =
            """
            class MyManager
            """.trimIndent()
        assertEquals(1, rule().lint(code).size)
    }

    // ── Name → Annotation: Service (accepts either FrontendService or BackendService) ──

    @Test
    fun `class named XxxService with FrontendService annotation - no violation`() {
        val code =
            """
            annotation class FrontendService
            @FrontendService class MyService
            """.trimIndent()
        assertTrue(rule().lint(code).isEmpty())
    }

    @Test
    fun `class named XxxService with BackendService annotation - no violation`() {
        val code =
            """
            annotation class BackendService
            @BackendService class MyService
            """.trimIndent()
        assertTrue(rule().lint(code).isEmpty())
    }

    @Test
    fun `class named XxxService without annotation - violation`() {
        val code =
            """
            class MyService
            """.trimIndent()
        assertEquals(1, rule().lint(code).size)
    }

    // ── Name → Annotation: Controller ────────────────────────────────────────

    @Test
    fun `class named XxxController with BackendController annotation - no violation`() {
        val code =
            """
            annotation class BackendController
            @BackendController class MyController
            """.trimIndent()
        assertTrue(rule().lint(code).isEmpty())
    }

    @Test
    fun `class named XxxController without annotation - violation`() {
        val code =
            """
            class MyController
            """.trimIndent()
        assertEquals(1, rule().lint(code).size)
    }

    // ── Name → Annotation: Datastore ─────────────────────────────────────────

    @Test
    fun `class named XxxDatastore with BackendDatastore annotation - no violation`() {
        val code =
            """
            annotation class BackendDatastore
            @BackendDatastore class MyDatastore
            """.trimIndent()
        assertTrue(rule().lint(code).isEmpty())
    }

    @Test
    fun `class named XxxDatastore without annotation - violation`() {
        val code =
            """
            class MyDatastore
            """.trimIndent()
        assertEquals(1, rule().lint(code).size)
    }

    // ── Annotation → Name: wrong name violations ──────────────────────────────

    @Test
    fun `FrontendViewModel annotation with non-matching class name - violation`() {
        val code =
            """
            annotation class FrontendViewModel
            @FrontendViewModel class MyFoo
            """.trimIndent()
        assertEquals(1, rule().lint(code).size)
    }

    @Test
    fun `FrontendManager annotation with non-matching class name - violation`() {
        val code =
            """
            annotation class FrontendManager
            @FrontendManager class MyFoo
            """.trimIndent()
        assertEquals(1, rule().lint(code).size)
    }

    @Test
    fun `FrontendService annotation with non-matching class name - violation`() {
        val code =
            """
            annotation class FrontendService
            @FrontendService class MyFoo
            """.trimIndent()
        assertEquals(1, rule().lint(code).size)
    }

    @Test
    fun `BackendService annotation with non-matching class name - violation`() {
        val code =
            """
            annotation class BackendService
            @BackendService class MyFoo
            """.trimIndent()
        assertEquals(1, rule().lint(code).size)
    }

    @Test
    fun `BackendController annotation with non-matching class name - violation`() {
        val code =
            """
            annotation class BackendController
            @BackendController class MyFoo
            """.trimIndent()
        assertEquals(1, rule().lint(code).size)
    }

    @Test
    fun `BackendDatastore annotation with non-matching class name - violation`() {
        val code =
            """
            annotation class BackendDatastore
            @BackendDatastore class MyFoo
            """.trimIndent()
        assertEquals(1, rule().lint(code).size)
    }

    // ── Edge cases ────────────────────────────────────────────────────────────

    @Test
    fun `class with unrelated name and no annotation - no violation`() {
        val code =
            """
            class MyUtilityClass
            """.trimIndent()
        assertTrue(rule().lint(code).isEmpty())
    }

    @Test
    fun `annotation class with architectural suffix - no violation`() {
        val code =
            """
            annotation class FrontendViewModel
            """.trimIndent()
        assertTrue(rule().lint(code).isEmpty())
    }

    @Test
    fun `no suffixes configured - no violations for any class`() {
        val emptyConfig = TestConfig("namingSuffixes" to emptyList<String>())
        val code =
            """
            class MyViewModel
            class MyService
            """.trimIndent()
        assertTrue(ArchitectureNamingRule(emptyConfig).lint(code).isEmpty())
    }

    @Test
    fun `both name and annotation violations report independently`() {
        // Class named XxxViewModel (triggers name check) but annotated with @BackendController
        // (triggers annotation check) — two distinct violations.
        val code =
            """
            annotation class FrontendViewModel
            annotation class BackendController
            @BackendController class MyViewModel
            """.trimIndent()
        assertEquals(2, rule().lint(code).size)
    }

    @Test
    fun `data class following naming convention with correct annotation - no violation`() {
        val code =
            """
            annotation class BackendDatastore
            @BackendDatastore data class UserDatastore(val id: String)
            """.trimIndent()
        assertTrue(rule().lint(code).isEmpty())
    }

    @Test
    fun `interface following naming convention without annotation - violation`() {
        val code =
            """
            interface UserDatastore
            """.trimIndent()
        assertEquals(1, rule().lint(code).size)
    }

    @Test
    fun `class implementing interface with matching suffix but no annotation - two violations`() {
        // Both the interface and the implementing class end with "Controller",
        // so the rule reports a violation for each.
        val code =
            """
            interface Controller
            class OccupantController : Controller
            """.trimIndent()
        assertEquals(2, rule().lint(code).size)
    }
}
