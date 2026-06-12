## Architecture Guidelines

Look at <project-root>/../wiki and ensure that this folder contains a git repo. This folder will contain our wiki and documentation. Without it, we will be missing a lot of crucial information. If the folder is not in place, request the user to checkout the wiki repo into that location.

### Back End

Strict layering: **Controllers → Services → Datastores**

- **Controllers** (example: `edifikana/back-end/.../controller/`): Validate API permissions and extract parameters only.
- **Services** (example: `edifikana/back-end/.../service/`): All business logic lives here.
- **Datastores** (example: `edifikana/back-end/.../datastore/`): Optimized data access, minimal business logic.

**For more in-depth information look at: <project-root>/../wiki/Projects/Architecture/back-end**

### Front End

Each screen/feature lives in `<projectname>/front-end/app/src/commonMain/.../features/<section>/<featurename>/` and consists of 5 files:

| File | Purpose |
|------|---------|
| `<Feature>Screen.kt` | Composable screen + `<Feature>Destination` nav destination |
| `<Feature>ViewModel.kt` | Extends `BaseViewModel<Event, UIState>`, registered via `viewModelOf(::...)` in DI |
| `<Feature>UIState.kt` | Immutable data class modeling the screen state, has a companion `Initial` |
| `<Feature>Event.kt` | Sealed class of `ViewModelEvent`s emitted from VM, consumed by UI |
| `<Feature>Preview.kt` | `@Preview` composable for design-time rendering |

A test file `<Feature>ViewModelTest.kt` belongs in the `jvmTest` source set.

**Use the IDEA file template** `Compose Feature` (in `.idea/fileTemplates/`) to scaffold all 5 files at once.

**Dependency injection:** Uses Koin. Register new ViewModels with `viewModelOf(::FeatureViewModel)` in the appropriate DI module.

**Navigation:** Uses `org.jetbrains.androidx.navigation:navigation-compose`. Each screen declares a `@Serializable data object <Feature>Destination : Destination()` and must be registered as a route in the relevant router.

**For more in-depth information look at: <project-root>/../wiki/Projects/Architecture/front-end**

## Repository Structure

This is a **Kotlin Multiplatform monorepo** targeting Android, JVM Desktop, and WASM/Web.

```
framework/          # Reusable cross-platform libraries (logging, core, network, etc.)
framework-samples/  # Sample apps exercising the framework modules
architecture/       # Shared architecture modules (front-end-architecture, back-end-architecture)
edifikana/          # Main project
  shared/           # Shared domain models and serialization
  api/              # API contracts
  back-end/         # Ktor back-end server
  front-end/
    shared-ui/      # Reusable Compose UI components
    app/            # Main shared app code (features, VMs, services, DI)
    launcher-android/ # Android entry point
    launcher-desktop/ # JVM Desktop entry point
    launcher-web/   # WASM/Web entry point
runasimi/           # Second project (same structure as edifikana)
templatereplaceme/  # Pure template module — compiles but has no demo functionality (see below)
ui-catalog/         # Shared UI component catalog
gradle/             # Custom Gradle plugins/scripts for each KMP target type
```

When adding a new module, also add it to `settings.gradle.kts` and the `releaseAll` task in the root `build.gradle.kts`.

### templatereplaceme placeholder conventions

`templatereplaceme/` is a **pure template module**: it compiles but carries no demo functionality. Every file in it serves as a source for the devtools component and app generators.

Placeholder convention (do not rename without updating `Generators.kt` and `TemplateEngine.kt`):

| Scope | PascalCase | lowercase | Replaced by |
|---|---|---|---|
| App level | `TemplateReplaceMe` | `templatereplaceme` | new app name |
| Component level | `ComponentReplaceMe` | `componentreplaceme` | component name (e.g., `Employee`) |
| Feature level | `MainMenu` | `main.menu` | feature name (e.g., `AddProperty`) |
| Activity level | `Main` | `main` | activity name (e.g., `Auth`) |

Feature/activity templates live at: `templatereplaceme/front-end/app/src/.../features/main/`

**For more in-depth information look at: https://wikijs-ok0o4wwsow8s80wwsgo40k4s.cramsan.com/en/Projects/Architecture/overview**

## Kotlin Code Style

Style is enforced by detekt (`config/detekt-config.yml` and `config/detekt-architecture-config.yml`) and ktlint. The build fails on violations. The rules below are the ones most commonly violated by generated code.

### Documentation (not auto-corrected — must be written manually)

Every **public class** and every **public function** requires a KDoc comment.

```kotlin
// WRONG — build will fail
class FlyerManager { ... }
fun loadFlyers(): List<Flyer> { ... }

// CORRECT
/** Coordinates flyer retrieval and caching. */
class FlyerManager { ... }

/** Returns all currently active flyers from the remote service. */
fun loadFlyers(): List<Flyer> { ... }
```

Exceptions (KDoc not required):
- Functions annotated with `@Preview`
- The default `companion object` inside a class

### Trailing commas

Trailing commas are required on **both** declaration and call sites whenever the list spans multiple lines.

```kotlin
// WRONG
fun FlyerCard(
    title: String,
    onClick: () -> Unit
)
FlyerCard(
    title = "Hello",
    onClick = {}
)

// CORRECT
fun FlyerCard(
    title: String,
    onClick: () -> Unit,   // ← trailing comma
)
FlyerCard(
    title = "Hello",
    onClick = {},          // ← trailing comma
)
```

### Imports

- **No wildcard imports** (`import com.example.*` is forbidden).
- Unused imports are removed automatically by ktlint on the next build; do not leave them in.

### Composable functions

`@Composable` functions are **exempt** from several complexity rules:
- `LongParameterList` — up to 12 parameters allowed
- `LongMethod` and `CyclomaticComplexity` — no limit enforced

Documentation is still required: every public `@Composable` must have a KDoc comment (only `@Preview` functions are exempt).

### Architecture annotations and naming

Classes in the layered architecture must carry the correct annotation **and** name suffix. Detekt enforces both:

| Annotation | Required suffix | May reference |
|---|---|---|
| `@BackendDatastore` | `Datastore` | — |
| `@BackendService` | `Service` | `@BackendDatastore`, `@BackendService` |
| `@BackendController` | `Controller` | `@BackendService` |
| `@FrontendService` | `Service` | — |
| `@FrontendManager` | `Manager` | `@FrontendService` |
| `@FrontendViewModel` | `ViewModel` | `@FrontendManager` |

Cross-layer calls not listed above will fail the architecture rule checks.

### Coroutine dispatchers

Never use `Dispatchers.IO` / `Dispatchers.Main` directly. Inject dispatchers via `@BackgroundDispatcher` or `@UIThreadDispatcher` annotations so they can be swapped in tests.

### Formatting (auto-corrected)

The following are automatically fixed by `./gradlew ktlintf`, but generating correct code avoids a noisy diff:
- 4-space indentation, no tabs
- No semicolons
- Annotations on their own line (not on the same line as the declaration)
- Single blank line between top-level declarations; no consecutive blank lines

### Detekt baseline policy

**Never suppress a detekt warning by adding it to the baseline.** If a violation cannot be fixed (e.g. it is in generated code, a third-party contract, or requires a non-trivial refactor), stop and ask the developer for guidance rather than silently suppressing it.

## Validating a Change

If you need to validate that a project compiles you should run the `release` task.
Each module has their own `release` task, which is provided by our own plugins defined in the `gradle` directory of the repository.
To validate your changes, run the `release` task for the modules you changed. This operation can be expensive, so only when asked to.

- **Build all projects and run all tests:** `./gradlew releaseAll --quiet`
- **Validate a specific module:** `./gradlew :<module-path>:release --quiet`
  - Example: `./gradlew :edifikana:front-end:app:release --quiet`
- **Fix formatting issues:** `./gradlew ktlintf --quiet`
  - Formatting is enforced by the build; the build will fail if format is wrong.
- **Run a single test:** `./gradlew :<module>:jvmTest --quiet --tests "com.cramsan.package.MyTest"`

### Running tasks as an AI agent

Gradle build output is very verbose and will fill the context window with task-progress lines that carry no signal. When running any Gradle task, append `--quiet` to suppress LIFECYCLE/INFO noise while keeping all warnings, compilation errors, test failures, and the final BUILD FAILED/SUCCESSFUL status:

```
./gradlew :<module-path>:release --quiet
./gradlew releaseAll --quiet
./gradlew ktlintf --quiet
```

## Testing

Every component type has a specific testing pattern. Front-end tests live in the `jvmTest` source set; back-end tests live in the `test` source set.

### Shared setup

- **`CoroutineTest`** (`framework/test`) — Base class for all tests involving coroutines. Provides `testCoroutineScope`, `testCoroutineDispatcher`, and `runCoroutineTest { }`.
- **MockK** — All mocking uses `mockk()`, `coEvery`, and `coVerify`.
- **Turbine** (`app.cash.turbine`) — Asserts on `Flow` / `StateFlow` emissions in ViewModel tests.
- Initialize the logger in every `@BeforeEach` / `@BeforeTest`:
  ```kotlin
  EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
  ```

---

### Composable functions (Screen files)

Composable functions are **not unit tested**. Visual correctness is verified at design time via `@Preview` functions in `<Feature>Preview.kt`. No test file is created for `<Feature>Screen.kt`.

#### Rendering a `@Preview` as a screenshot (Roborazzi)

Each module with Compose UI has Roborazzi configured to generate Robolectric tests from every composable annotated with `@ComponentPreviews`, `@ScreenPreviews`, or `@DevicePreviews` (see `PreviewAnnotations.kt`) — a bare `@Preview` is not scanned. Use this to visually verify a component during or after implementation.

**Record a single preview:**
```bash
./gradlew :<module>:recordRoborazziDebug \
  --tests "com.github.takahirom.roborazzi.RoborazziPreviewParameterizedTests.test[<fully.qualified.FileNameKt>:<PreviewFunctionName>]"
```

The test name format is `<fully.qualified.package.FileNameKt>:<PreviewFunctionName>` where `FileNameKt` is the Kotlin file name with `Kt` appended (e.g. `StatusBadgePreview.kt` → `StatusBadgePreviewKt`).

**Example** — render `ApprovedPreview` from `StatusBadgePreview.kt` in `flyerboard:front-end:shared-ui`:
```bash
./gradlew :flyerboard:front-end:shared-ui:recordRoborazziDebug \
  --tests "com.github.takahirom.roborazzi.RoborazziPreviewParameterizedTests.test[com.cramsan.flyerboard.client.ui.components.StatusBadgePreviewKt:ApprovedPreview]"
```

**Screenshot output location:** `<module-dir>/screenshots/<fully.qualified.FileNameKt>_<PreviewFunctionName>.png`

Example: `flyerboard/front-end/shared-ui/screenshots/com.cramsan.flyerboard.client.ui.components.StatusBadgePreviewKt_ApprovedPreview.png`

**Record all previews in a module** (re-renders everything):
```bash
./gradlew :<module>:recordRoborazziDebug
```

**Discovering test names** — if you are unsure of the exact test name for a preview, run the tests without recording first and grep the output:
```bash
./gradlew :<module>:testDebugUnitTest 2>&1 | grep "PASSED\|FAILED"
```
Each line contains the full test name in `[<FileNameKt>:<PreviewFunctionName>]` format.

**Quick filter with a wildcard:** `recordRoborazziDebug` is not itself a `Test` task, so `--tests` only takes effect because the task depends on `testDebugUnitTest`. A simple wildcard against the preview function name works and avoids needing the exact bracketed test name:
```bash
./gradlew :<module>:recordRoborazziDebug --tests "*<PreviewFunctionName>*"
```

**`@DevicePreviews` variants:** previews annotated with `@DevicePreviews` (see `PreviewAnnotations.kt`) generate three variants per preview function, with `_Phone`, `_Tablet`, and `_Desktop` suffixes appended to both the test name and the screenshot filename (e.g. `SplashScreenPreview_Phone`). Account for the suffix when constructing an exact `--tests` filter, or just use the wildcard form above.

**⚠️ Check for unrelated screenshot diffs after recording:** `finalizeTestRoborazziDebug` can re-write screenshots beyond the ones targeted by `--tests`, picking up stale images left in `build/intermediates/roborazzi/` from earlier runs. These often differ only by minor font-rendering/anti-aliasing from the machine that produced the committed baseline. After recording, run `git status` on the module's `screenshots/` directory and `git checkout --` any files you didn't intend to change.

---

### ViewModels

**Source set:** `jvmTest` | **Location:** mirrors production path, e.g. `features/auth/signin/SignInViewModelTest.kt`

```kotlin
class SignInViewModelTest : CoroutineTest() {

    private lateinit var authManager: AuthManager
    private lateinit var viewModel: SignInViewModel
    private lateinit var windowEventBus: EventBus<WindowEvent>

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        authManager = mockk()
        windowEventBus = EventBus()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = CollectorCoroutineExceptionHandler(),
            windowEventReceiver = windowEventBus,
            applicationEventReceiver = EventBus(),
        )
        viewModel = SignInViewModel(authManager, dependencies)
    }

    @Test
    fun `signIn emits navigation event on success`() = runCoroutineTest {
        coEvery { authManager.signIn(any(), any()) } returns Result.success(Unit)

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.signIn("user@example.com", "password")

            assertEquals(ExpectedNavEvent, turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
        coVerify { authManager.signIn("user@example.com", "password") }
    }
}
```

**Assert:**
- UI state snapshots via `viewModel.uiState.value.fieldName`
- Navigation/window events via Turbine: `turbine.awaitItem()`
- Manager calls via `coVerify { manager.method(...) }`
- Call `advanceUntilIdleAndAwaitComplete(turbine)` to flush remaining events before the turbine scope closes

---

### Managers

**Source set:** `jvmTest` | **Location:** `managers/<Name>ManagerTest.kt`

```kotlin
class PropertyManagerTest : CoroutineTest() {

    private lateinit var propertyService: PropertyService
    private lateinit var manager: PropertyManager

    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        propertyService = mockk()
        val dependencies = mockk<ManagerDependencies>(relaxed = true)
        every { dependencies.appScope } returns testCoroutineScope
        every { dependencies.dispatcherProvider } returns UnifiedDispatcherProvider(testCoroutineDispatcher)
        manager = PropertyManager(propertyService, dependencies)
    }

    @Test
    fun `getPropertyList returns property list`() = runCoroutineTest {
        val propertyList = listOf(mockk<PropertyModel>())
        coEvery { propertyService.getPropertyList() } returns Result.success(propertyList)

        val result = manager.getPropertyList()

        assertTrue(result.isSuccess)
        assertEquals(propertyList, result.getOrNull())
        coVerify { propertyService.getPropertyList() }
    }
}
```

**Assert:**
- `Result<T>` success or failure: `assertTrue(result.isSuccess)` / `assertTrue(result.isFailure)`
- Returned values: `assertEquals(expected, result.getOrNull())`
- Delegation to services: `coVerify { service.method(...) }`

---

### Client service implementations

**Source set:** `jvmTest` | **Location:** `service/impl/<Name>ServiceImplTest.kt`

HTTP-based services use `KtorTestEngine` to mock server responses without real network calls. Non-HTTP services mock their SDK client directly.

```kotlin
class AuthServiceImplTest {

    private lateinit var auth: Auth
    private lateinit var service: AuthServiceImpl

    @BeforeTest
    fun setupTest() {
        auth = mockk()
        val ktorTestEngine = KtorTestEngine()
        val http = HttpClient(ktorTestEngine.engine) {
            install(ContentNegotiation) { json(createJson()) }
        }
        service = AuthServiceImpl(auth, http)
        AssertUtil.setInstance(NoopAssertUtil())
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    @Test
    fun `isSignedIn returns true when session is active`() = runTest {
        coEvery { auth.currentUserOrNull() } returns mockk()
        coEvery { auth.refreshCurrentSession() } just Runs

        val result = service.isSignedIn()

        assertTrue(result.getOrThrow())
    }
}
```

**Assert:**
- `Result<T>` success or failure
- Returned domain model values
- SDK/HTTP client interactions via `coVerify`

---

### Network mappers (front-end)

**Source set:** `jvmTest` | **Location:** `service/impl/NetworkMappersTest.kt`

Mapper tests are pure unit tests — no mocks, no coroutines.

```kotlin
class NetworkMappersTest {

    @Test
    fun `toEmployeeModel maps all fields correctly`() {
        val networkResponse = EmployeeNetworkResponse(
            id = EmployeeId("emp-123"),
            firstName = "John",
            ...
        )

        val model = networkResponse.toEmployeeModel()

        assertEquals(EmployeeId("emp-123"), model.id)
        assertEquals("John", model.firstName)
    }
}
```

**Assert:** Every field individually. Include null/missing field cases.

---

### Back-end: Controllers

**Source set:** `test` | **Location:** `controller/<Name>ControllerTest.kt`

Controller tests spin up a full Ktor test server via `testBackEndApplication` and use Koin for DI. All services are resolved from the Koin context and mocked via `coEvery`.

```kotlin
class EmployeeControllerTest : CoroutineTest(), KoinTest {

    @BeforeTest
    fun setupTest() {
        startTestKoin(
            testApplicationModule(createJson()),
            TestControllerModule,
            TestServiceModule,
        )
    }

    @AfterTest
    fun cleanUp() { stopKoin() }

    @Test
    fun `createEmployee succeeds when user has required role`() = testBackEndApplication {
        val requestBody = readFileContent("requests/create_employee_request.json")
        val expectedResponse = readFileContent("requests/create_employee_response.json")
        val employeeService = get<EmployeeService>()
        val rbacService = get<RBACService>()
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()

        coEvery { employeeService.createEmployee(...) } returns Employee(...)
        coEvery { contextRetriever.getContext(any()) } returns AuthenticatedClientContext(...)
        coEvery { rbacService.hasRoleOrHigher(any(), any(), any()) } returns true

        val response = client.post("employee") {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
        coVerify { employeeService.createEmployee(...) }
    }
}
```

**Assert:**
- HTTP status code: `assertEquals(HttpStatusCode.OK, response.status)`
- Response body against JSON fixture: `assertEquals(expectedJson, response.bodyAsText())`
- Service calls via `coVerify`
- Always cover both authorized and unauthorized paths (RBAC positive and negative)
- JSON request/response fixtures live in `src/test/resources/requests/`

---

### Back-end: Services

**Source set:** `test` | **Location:** `service/<Name>ServiceTest.kt`

```kotlin
class UserServiceTest {

    private lateinit var userDatastore: UserDatastore
    private lateinit var userService: UserService
    private lateinit var testTimeSource: TestTimeSource
    private lateinit var clock: Clock

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        userDatastore = mockk()
        testTimeSource = TestTimeSource()
        clock = testTimeSource.asClock(2024, 1, 1, 0, 0)
        userService = UserService(userDatastore, clock)
    }

    @Test
    fun `createUser returns success when datastore succeeds`() = runTest {
        val user = User(id = UserId("user-1"), ...)
        coEvery { userDatastore.createUser(any()) } returns user

        val result = userService.createUser(...)

        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
        coVerify { userDatastore.createUser(any()) }
    }
}
```

**Assert:**
- `Result<T>` success/failure
- Returned domain model
- Datastore delegation via `coVerify`
- Time-sensitive logic: advance the clock with `testTimeSource += N.days` then re-assert
- Use `@ParameterizedTest @CsvSource` / `@CsvFileSource` for data-driven scenarios (e.g., role hierarchy)
- Exception testing: `assertThrows<ForbiddenException> { ... }`

---

### Back-end: Datastores

Datastores connect directly to a database and are tested through **integration tests** in the `integTest` source set (`src/integTest/`). These tests run against a real instance and are intentionally excluded from the normal `release` task (though their compilation is still checked).

**Example of how to run integration tests:** `./gradlew :edifikana:back-end:integTest`

#### Structure

Every datastore integration test extends `SupabaseIntegrationTest`, an abstract base class that handles the full Koin setup and resource cleanup:

```kotlin
class SupabaseEmployeeDatastoreIntegrationTest : SupabaseIntegrationTest() {

    private lateinit var test_prefix: String
    private var propertyId: PropertyId? = null
    private var testUserId: UserId? = null
    private var orgId: OrganizationId? = null

    @BeforeTest
    fun setup() {
        test_prefix = UUID.random()
        runBlocking {
            testUserId = createTestUser("user-${test_prefix}@example.com")
            orgId = createTestOrganization("org_$test_prefix", "")
            propertyId = createTestProperty("${test_prefix}_Property", testUserId!!, orgId!!)
        }
    }

    @Test
    fun `createEmployee should return employee on success`() = runCoroutineTest {
        val result = employeeDatastore.createEmployee(
            idType = IdType.PASSPORT,
            firstName = "${test_prefix}_First",
            lastName = "${test_prefix}_Last",
            role = EmployeeRole.CLEANING,
            propertyId = propertyId!!,
        ).registerEmployeeForDeletion()

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `deleteEmployee should remove employee`() = runCoroutineTest {
        val createResult = employeeDatastore.createEmployee(
            firstName = "${test_prefix}_ToDelete",
            lastName = "${test_prefix}_LastToDelete",
            role = EmployeeRole.SECURITY,
            idType = IdType.DNI,
            propertyId = propertyId!!,
        ).registerEmployeeForDeletion()
        assertTrue(createResult.isSuccess)
        val employee = createResult.getOrNull()!!

        val deleteResult = employeeDatastore.deleteEmployee(employee.id)

        assertTrue(deleteResult.isSuccess)
        val getResult = employeeDatastore.getEmployee(employee.id)
        assertNull(getResult.getOrNull())
    }
}
```

#### Key conventions

- **`<Datastore>IntegrationTest` base class** — Starts Koin with `TestArchitectureModule`, `integTestFrameworkModule("APPLICATION_NAME")`, `DatastoreModule`, and `IntegTestApplicationModule`. Tears down after each test by soft-deleting then purging every created record in reverse FK order, then signing out and stopping Koin.
- **`test_prefix`** — Each test class generates a `UUID.random()` prefix in `@BeforeTest` and uses it in every created record name/email. This prevents collisions when tests run in parallel or are re-run without cleanup.
- **Resource tracking** — Register created records for cleanup by chaining `.registerXxxForDeletion()` on the `Result<T>` returned from the datastore. The base class deletes them automatically in `@AfterTest`.
- **Prerequisite data** — Use the base class factory helpers (`createTestUser`, `createTestOrganization`, `createTestProperty`, etc.) to build required parent records; these are also automatically cleaned up.
- **Test all CRUD operations**: create, get/list, update, delete, and the delete-nonexistent failure case.
- **Configuration** — Integration tests read credentials from `config.properties.integ` or environment variables (prefixed with the domain key `APPLICATION_NAME_`). The real datastore backend(supabase, mongo, mysql, etc) needs to be running, that is outside the scope of this work.

#### Unit tests for parsing-only logic

If a datastore class contains pure parsing or string-extraction helpers (no I/O), those specific functions can be covered in the regular `test` source set without a live connection:

```kotlin
class SupabaseStorageDatastoreTest {
    private val datastore = SupabaseStorageDatastore(storage = mockk())

    @Test
    fun `extractBucketAndObjectPath splits on first slash`() {
        val (bucket, objectPath) = datastore.extractBucketAndObjectPath(AssetId("images/photo.jpg"))
        assertEquals("images", bucket)
        assertEquals("photo.jpg", objectPath)
    }

    @Test
    fun `extractBucketAndObjectPath throws for missing path`() {
        assertThrows<IllegalArgumentException> {
            datastore.extractBucketAndObjectPath(AssetId("images"))
        }
    }
}
```

---

### Back-end: Datastore mappers (Entity → Domain)

**Source set:** `test` | **Location:** `datastore/<Name>MappersTest.kt`

```kotlin
class SupabaseMappersTest {

    @Test
    fun `UserEntity toUser maps all fields correctly`() {
        val entity = UserEntity(id = "user-123", email = "test@example.com", ...)

        val user = entity.toUser()

        assertEquals(UserId("user-123"), user.id)
        assertEquals("test@example.com", user.email)
    }
}
```

**Assert:** Every field individually. Include nullable fields and edge cases.

---

## New Modules

When creating new modules or projects you can look at the `templatereplaceme` folder in the root of the project.
This module contains a template of a front end and back end applications that can be copied to create new modules or projects.
Get familiar with the high level approach of this project as it works as a reference implementation of our architecture.

## New Screen

When creating new screens or features, look at the `.idea/fileTemplates` folder for templates of common files we use in the project.

## Dependency Management

Dependencies are managed with **refreshVersions** in `versions.properties`. Use `_` as the version placeholder in `build.gradle.kts` files — the actual version is resolved from `versions.properties`.

## Gradle Plugins

Custom Gradle scripts in `gradle/` configure KMP targets. Apply the appropriate script in `build.gradle.kts`:
- `kotlin-mpp-target-common-compose.gradle` — common Compose multiplatform
- `kotlin-mpp-target-android-lib-compose.gradle` — Android Compose library
- `kotlin-mpp-target-jvm-compose.gradle` — JVM Compose
- `kotlin-mpp-target-wasm-compose-application.gradle` — WASM application
- `release-task.gradle` — provides the `release` validation task for every module

## Code Quality Skills

When you finish a task and the change touches **10 or more Kotlin files**, proactively mention the relevant code quality skill(s) at the end of your response so the user knows they can run a deeper review. Choose based on what changed:

| Changed files contain… | Suggest |
|---|---|
| Front-end features, ViewModels, Composables | `/review-ui` |
| Back-end controllers, services, datastores | `/review-be` |
| Framework or architecture modules | `/review-core` |
| A mix of the above, or you're unsure | `/code-quality` |

Phrase it as a one-liner, for example:
> This change touches N files — you can run `/code-quality changes` for a full quality review.

Do not run the skill automatically. Only suggest it; let the user decide.

# Commits

When committing changes, use the following format for commit messages to maintain consistency and clarity:

```
[<MODULE>] <Short description of the change> (#<GitHub issue number>)
```

Examples of commit messages following our conventional commit style:
```
[EDIFIKANA] new tables for rent and payments (#445)
[DOCS] Expand AI and Copilot instructions with full architecture reference (#251)
[GRADLE] Reduce verbosity of test runs (NONE)
[RUNASIMI] Add static translation tables for verb conjugations (#13)
```

Do not push changes to `main` directly. All changes must go through a pull request (PR) with a descriptive title and linked GitHub issue.
Avoid pushing changes unless the user explicitly asks you to.

# Branches

**main**: Stable production-ready code. All features must be merged here through the PR process.

Feature branches should follow the naming convention: `<user>/<issue-number>-<short-description>`. For example:
```
- alg/383_rentConfig_paymentRecords_models
- cr/00_migrate_new_turbine_api
```
