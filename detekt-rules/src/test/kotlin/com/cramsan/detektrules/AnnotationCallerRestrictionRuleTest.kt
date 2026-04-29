package com.cramsan.detektrules

import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.test.utils.createEnvironment
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AnnotationCallerRestrictionRuleTest {
    private val testConfig =
        TestConfig(
            "layers" to
                listOf(
                    "BackendDatastore:BackendService",
                    "BackendService:BackendController,BackendService",
                    "FrontendService:FrontendManager",
                    "FrontendManager:FrontendViewModel",
                ),
        )

    private val env: KotlinEnvironmentContainer = createEnvironment()

    private fun rule() = AnnotationCallerRestrictionRule(testConfig)

    // ── Back-end: Datastore layer ─────────────────────────────────────────────

    @Test
    fun `service referencing datastore - no violation`() {
        val code =
            """
            annotation class BackendDatastore
            annotation class BackendService

            @BackendDatastore class MyDatastore
            @BackendService class MyService(val ds: MyDatastore)
            """.trimIndent()
        assertTrue(rule().lintWithContext(env, code).isEmpty())
    }

    @Test
    fun `controller referencing datastore directly - violation`() {
        val code =
            """
            annotation class BackendDatastore
            annotation class BackendController

            @BackendDatastore class MyDatastore
            @BackendController class MyController(val ds: MyDatastore)
            """.trimIndent()
        assertEquals(1, rule().lintWithContext(env, code).size)
    }

    @Test
    fun `unannotated class referencing datastore - no violation`() {
        val code =
            """
            annotation class BackendDatastore

            @BackendDatastore class MyDatastore
            class SomeDiModule(val ds: MyDatastore)
            """.trimIndent()
        assertTrue(rule().lintWithContext(env, code).isEmpty())
    }

    @Test
    fun `multiple references to same forbidden type - only one report per class`() {
        val code =
            """
            annotation class BackendDatastore
            annotation class BackendController

            @BackendDatastore class MyDatastore
            @BackendController class MyController {
                val ds1: MyDatastore? = null
                val ds2: MyDatastore? = null
            }
            """.trimIndent()
        assertEquals(1, rule().lintWithContext(env, code).size)
    }

    // ── Back-end: Service layer ───────────────────────────────────────────────

    @Test
    fun `controller referencing service - no violation`() {
        val code =
            """
            annotation class BackendService
            annotation class BackendController

            @BackendService class MyService
            @BackendController class MyController(val svc: MyService)
            """.trimIndent()
        assertTrue(rule().lintWithContext(env, code).isEmpty())
    }

    @Test
    fun `service referencing another service - no violation`() {
        val code =
            """
            annotation class BackendService

            @BackendService class ServiceA
            @BackendService class ServiceB(val a: ServiceA)
            """.trimIndent()
        assertTrue(rule().lintWithContext(env, code).isEmpty())
    }

    // ── Front-end: Manager layer ──────────────────────────────────────────────

    @Test
    fun `manager referencing frontend service - no violation`() {
        val code =
            """
            annotation class FrontendService
            annotation class FrontendManager

            @FrontendService class ApiService
            @FrontendManager class Mgr(val svc: ApiService)
            """.trimIndent()
        assertTrue(rule().lintWithContext(env, code).isEmpty())
    }

    @Test
    fun `viewmodel referencing frontend service directly - violation`() {
        val code =
            """
            annotation class FrontendService
            annotation class FrontendViewModel

            @FrontendService class ApiService
            @FrontendViewModel class MyVm(val svc: ApiService)
            """.trimIndent()
        assertEquals(1, rule().lintWithContext(env, code).size)
    }

    // ── Front-end: ViewModel layer ────────────────────────────────────────────

    @Test
    fun `viewmodel referencing manager - no violation`() {
        val code =
            """
            annotation class FrontendManager
            annotation class FrontendViewModel

            @FrontendManager class Mgr
            @FrontendViewModel class MyVm(val mgr: Mgr)
            """.trimIndent()
        assertTrue(rule().lintWithContext(env, code).isEmpty())
    }

    // ── Constructor call expressions (inferred types) ────────────────────────

    @Test
    fun `controller constructing datastore instance directly - violation`() {
        val code =
            """
            annotation class BackendDatastore
            annotation class BackendController

            @BackendDatastore data class MyEntity(val id: String)
            @BackendController class MyController {
                fun handle() {
                    val e = MyEntity(id = "x")
                    println(e)
                }
            }
            """.trimIndent()
        assertEquals(1, rule().lintWithContext(env, code).size)
    }

    @Test
    fun `datastore constructing a database model entity - no violation`() {
        // DatabaseModel types may only be referenced by BackendDatastore (per layer config).
        // A @BackendDatastore class constructing a @DatabaseModel entity is therefore allowed.
        val databaseModelConfig =
            TestConfig(
                "layers" to
                    listOf(
                        "BackendDatastore:BackendService",
                        "BackendService:BackendController,BackendService",
                        "DatabaseModel:BackendDatastore",
                    ),
            )
        val code =
            """
            annotation class DatabaseModel
            annotation class BackendDatastore

            @DatabaseModel data class MyEntity(val id: String)
            @BackendDatastore class MyDatastore {
                fun find(): MyEntity = MyEntity(id = "x")
            }
            """.trimIndent()
        assertTrue(AnnotationCallerRestrictionRule(databaseModelConfig).lintWithContext(env, code).isEmpty())
    }

    // ── Qualified constructor calls: Outer.Inner(...) ────────────────────────

    @Test
    fun `controller constructing qualified nested datastore type directly - violation`() {
        val databaseModelConfig =
            TestConfig(
                "layers" to
                    listOf(
                        "BackendDatastore:BackendService",
                        "BackendService:BackendController,BackendService",
                        "DatabaseModel:BackendDatastore",
                    ),
            )
        val code =
            """
            annotation class DatabaseModel
            annotation class BackendController

            class UserEntity {
                @DatabaseModel data class CreateUserEntity(val id: String)
            }
            @BackendController class UserController {
                fun createUser() {
                    val e = UserEntity.CreateUserEntity(id = "x")
                    println(e)
                }
            }
            """.trimIndent()
        assertEquals(1, AnnotationCallerRestrictionRule(databaseModelConfig).lintWithContext(env, code).size)
    }

    // ── Empty layer config ────────────────────────────────────────────────────

    @Test
    fun `no layers configured - no violation for any reference`() {
        val emptyConfig = TestConfig("layers" to emptyList<String>())
        val code =
            """
            annotation class BackendDatastore
            annotation class BackendController

            @BackendDatastore class MyDatastore
            @BackendController class MyController(val ds: MyDatastore)
            """.trimIndent()
        assertTrue(AnnotationCallerRestrictionRule(emptyConfig).lintWithContext(env, code).isEmpty())
    }

    // ── Constructors are allowed to invoke constructors if they are of the same type ──────
    @Test
    fun `subtype calling supertypes - no violation`() {
        val databaseModelConfig =
            TestConfig(
                "layers" to
                    listOf(
                        "BackendDatastore:BackendService",
                    ),
            )
        val code =
            """
            annotation class BackendService
            annotation class BackendDatastore

            @BackendDatastore class BaseDatastore {
            }

            @BackendDatastore class MyDatastore : BaseDatastore() {
            }
            """.trimIndent()
        assertTrue(AnnotationCallerRestrictionRule(databaseModelConfig).lintWithContext(env, code).isEmpty())
    }
}
