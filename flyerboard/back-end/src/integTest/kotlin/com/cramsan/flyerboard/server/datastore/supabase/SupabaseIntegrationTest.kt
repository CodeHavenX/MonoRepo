package com.cramsan.flyerboard.server.datastore.supabase

import com.cramsan.architecture.server.settings.SettingsHolder
import com.cramsan.architecture.server.test.dependencyinjection.TestArchitectureModule
import com.cramsan.architecture.server.test.dependencyinjection.integTestFrameworkModule
import com.cramsan.architecture.server.test.supabase.SupabaseTestSession
import com.cramsan.architecture.server.test.supabase.createAndSignInSupabaseTestUser
import com.cramsan.architecture.server.test.supabase.signInAsExistingSupabaseUser
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.server.dependencyinjection.DatastoreModule
import com.cramsan.flyerboard.server.datastore.impl.SupabaseFileDatastore
import com.cramsan.flyerboard.server.datastore.impl.SupabaseFlyerDatastore
import com.cramsan.flyerboard.server.datastore.impl.SupabaseUserDatastore
import com.cramsan.flyerboard.server.datastore.impl.SupabaseUserProfileDatastore
import com.cramsan.flyerboard.server.service.models.User
import com.cramsan.flyerboard.server.service.models.UserProfile
import com.cramsan.flyerboard.server.settings.FlyerBoardSettingKey
import com.cramsan.framework.utils.password.generateRandomPassword
import io.github.jan.supabase.auth.Auth
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest

/**
 * Base class for datastore integration tests that run against a real, locally running Supabase
 * instance (see `flyerboard/back-end/supabase/`). Nothing is mocked here: every datastore call
 * goes through the same Koin-wired beans ([DatastoreModule]) the production server uses.
 *
 * Every table flyerboard owns (`users`, `user_profiles`, `flyers`) has its primary/foreign key
 * tied to `auth.users(id) ON DELETE CASCADE`, so cleanup only needs to delete the Supabase Auth
 * user created for a test; the cascade removes everything else created for that user.
 *
 * Tests run on a plain [kotlinx.coroutines.runBlocking], not a virtual-time test dispatcher:
 * `kotlinx-coroutines-test`'s `TestScope` fast-forwards `delay()`-based suspension once it sees
 * no other work scheduled, which fires Ktor's per-request `HttpTimeout` near-instantly instead of
 * waiting on the real socket I/O these tests depend on.
 */
abstract class SupabaseIntegrationTest : KoinTest {

    protected val auth: Auth by inject()
    protected val flyerDatastore: SupabaseFlyerDatastore by inject()
    protected val userDatastore: SupabaseUserDatastore by inject()
    protected val userProfileDatastore: SupabaseUserProfileDatastore by inject()
    protected val fileDatastore: SupabaseFileDatastore by inject()
    protected val settingsHolder: SettingsHolder by inject()

    private val authUserIds = mutableSetOf<String>()

    @BeforeEach
    fun supabaseSetup() {
        startKoin {
            allowOverride(true)
            modules(
                TestArchitectureModule,
                integTestFrameworkModule("FLYERBOARD"),
                DatastoreModule,
            )
        }
    }

    /**
     * Creates a real Supabase Auth user for [email] and registers it for deletion at teardown.
     * Deleting this user cascades to delete any `users`/`user_profiles`/`flyers` rows created
     * for it during the test.
     */
    protected fun createTestAuthUser(email: String): UserId {
        val userInfo =
            runBlocking {
                auth.admin.createUserWithEmail {
                    this.email = email
                    this.password = generateRandomPassword()
                    autoConfirm = true
                }
            }
        authUserIds.add(userInfo.id)
        return UserId(userInfo.id)
    }

    /**
     * Creates a brand-new, auto-confirmed Supabase Auth user for [email] and signs in as them,
     * returning a real access token. Registers the user for deletion at teardown.
     */
    protected fun createTestAuthSession(
        email: String,
        password: String = generateRandomPassword(),
    ): SupabaseTestSession {
        val session = runBlocking {
            createAndSignInSupabaseTestUser(
                supabaseUrl = settingsHolder.getString(FlyerBoardSettingKey.SupabaseUrl).orEmpty(),
                supabaseServiceRoleKey = settingsHolder.getString(FlyerBoardSettingKey.SupabaseKey).orEmpty(),
                email = email,
                password = password,
            )
        }
        authUserIds.add(session.userId)
        return session
    }

    /**
     * Sets/overwrites a password on an existing Supabase Auth user identified by [userId] and
     * [email] (e.g. a seeded, OTP-only fixture), then signs in as them, returning a real access
     * token. Does not register the user for deletion — the caller is responsible for its
     * lifecycle since it was not created by this test.
     */
    protected fun signInAsSeededUser(
        userId: UserId,
        email: String,
        password: String = generateRandomPassword(),
    ): SupabaseTestSession = runBlocking {
        signInAsExistingSupabaseUser(
            supabaseUrl = settingsHolder.getString(FlyerBoardSettingKey.SupabaseUrl).orEmpty(),
            supabaseServiceRoleKey = settingsHolder.getString(FlyerBoardSettingKey.SupabaseKey).orEmpty(),
            userId = userId.userId,
            email = email,
            password = password,
        )
    }

    /**
     * Creates a `users` row for [userId] via the real [SupabaseUserDatastore].
     */
    protected fun createTestUser(
        userId: UserId,
        firstName: String = "Test",
        lastName: String = "User",
    ): User =
        runBlocking {
            userDatastore.createUser(userId, firstName, lastName).getOrThrow()
        }

    /**
     * Creates a `user_profiles` row for [userId] via the real [SupabaseUserProfileDatastore].
     */
    protected fun createTestUserProfile(userId: UserId, role: UserRole = UserRole.USER): UserProfile =
        runBlocking {
            userProfileDatastore.createUserProfile(userId, role).getOrThrow()
        }

    @AfterTest
    fun tearDown() {
        try {
            runBlocking {
                authUserIds.forEach { auth.admin.deleteUser(it) }
            }
        } finally {
            authUserIds.clear()
            stopKoin()
        }
    }
}
